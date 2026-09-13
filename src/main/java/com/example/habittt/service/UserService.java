package com.example.habittt.service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.habittt.entity.Costume;
import com.example.habittt.entity.Reward;
import com.example.habittt.entity.User;
import com.example.habittt.entity.UserCostume;
import com.example.habittt.mapper.CostumeMapper;
import com.example.habittt.mapper.RewardMapper;
import com.example.habittt.mapper.UserMapper;
import com.example.habittt.mapper.UserCostumeMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserCostumeMapper userCostumeMapper;

    @Resource
    private CostumeMapper costumeMapper;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    @Resource
    private EmailService emailService;

    @Resource
    private RewardMapper rewardMapper;

    // JWT 密钥
    private static final SecretKey KEY = Keys.hmacShaKeyFor("habittt-secret-key-for-jwt-token-generation".getBytes(StandardCharsets.UTF_8));

    /**
     * 生成图形验证码
     */
    public String generateCaptcha(String captchaKey) {
        int num1 = (int) (Math.random() * 10);
        int num2 = (int) (Math.random() * 10);
        String answer = String.valueOf(num1 + num2);
        // 存入 Redis，有效期 2 分钟
        redisTemplate.opsForValue().set("captcha:" + captchaKey, answer, 2, TimeUnit.MINUTES);
        return num1 + " + " + num2 + " = ?";
    }

    /**
     * 校验图形验证码
     */
    public boolean verifyCaptcha(String captchaKey, String code) {
        String savedCode = redisTemplate.opsForValue().get("captcha:" + captchaKey);
        if (savedCode == null || !savedCode.equalsIgnoreCase(code)) {
            return false;
        }
        redisTemplate.delete("captcha:" + captchaKey); // 验证成功后删除
        return true;
    }

    /**
     * 发送验证码
     * @param email 邮箱地址
     * @param scene 场景：register-注册，reset-重置密码
     */
    public void sendCode(String email, String scene) {
        // 验证邮箱格式
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new RuntimeException("邮箱格式不正确");
        }

        // 检查邮箱是否存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        boolean userExists = userMapper.selectCount(wrapper) > 0;

        // 根据不同场景进行验证
        if ("register".equals(scene)) {
            // 注册场景：邮箱应该未被注册
            if (userExists) {
                throw new RuntimeException("该邮箱已被注册");
            }
        } else if ("reset".equals(scene)) {
            // 重置密码场景：邮箱应该已注册
            if (!userExists) {
                throw new RuntimeException("该邮箱未注册");
            }
        } else {
            throw new RuntimeException("无效的场景参数");
        }

        // 生成6位随机验证码
        String code = RandomUtil.randomNumbers(6);

        // 存入 Redis，有效期 5 分钟
        redisTemplate.opsForValue().set("code:" + email, code, 5, TimeUnit.MINUTES);

        // 发送真实邮件
        try {
            emailService.sendVerificationCode(email, code);
        } catch (Exception e) {
            // 如果邮件发送失败，删除 Redis 中的验证码
            redisTemplate.delete("code:" + email);
            throw new RuntimeException("邮件发送失败，请稍后重试：" + e.getMessage());
        }
    }

    /**
     * 注册
     */
    public void register(String email, String password, String code) {
        // 验证邮箱验证码
        String savedCode = redisTemplate.opsForValue().get("code:" + email);
        if (savedCode == null || !savedCode.equals(code)) {
            throw new RuntimeException("验证码错误或已过期");
        }

        // 检查邮箱是否已被注册
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        if (userMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("邮箱已被注册");
        }

        // 创建新用户
        User user = new User();
        user.setEmail(email);
        user.setPassword(BCrypt.hashpw(password));
        userMapper.insert(user);

        // 删除已使用的验证码
        redisTemplate.delete("code:" + email);
    }


    /**
     * 登录
     */
    public Map<String, Object> login(String email, String password, String captchaKey, String captchaCode) {
        // 校验图形验证码
        if (!verifyCaptcha(captchaKey, captchaCode)) {
            throw new RuntimeException("验证码错误或已过期");
        }

        // 校验邮箱密码
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        User user = userMapper.selectOne(wrapper);

        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            throw new RuntimeException("邮箱或密码错误");
        }

        // 生成 JWT
        String token = Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 17L * 24 * 60 * 60 * 1000))
                .signWith(KEY)
                .compact();

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        return result;
    }

    /**
     * 重置密码
     */
    public void resetPassword(String email, String code, String newPassword) {
        String savedCode = redisTemplate.opsForValue().get("code:" + email);
        if (savedCode == null || !savedCode.equals(code)) {
            throw new RuntimeException("验证码错误或已过期");
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setPassword(BCrypt.hashpw(newPassword));
        userMapper.updateById(user);
        redisTemplate.delete("code:" + email);
    }

    /**
     * 获取用户详细信息
     */
    public Map<String, Object> getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new RuntimeException("用户不存在");

        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId()); // 【新增】必须把 ID 返回给前端！
        result.put("nickname", user.getNickname());
        result.put("points", user.getPoints());
        result.put("email", user.getEmail());
        result.put("status", user.getStatus() != null ? user.getStatus() : "正常");
        result.put("teamId", user.getTeamId());
        result.put("createQuota", user.getCreateQuota() == null ? 0 : user.getCreateQuota());

        if (user.getAvatar() != null) {
            Costume costume = costumeMapper.selectById(user.getAvatar());
            result.put("avatar", costume);
        } else {
            result.put("avatar", null);
        }
        return result;
    }

    /**
     * 更换装扮
     */
    public void changeCostume(Long userId, Long costumeId) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new RuntimeException("用户不存在");
        user.setAvatar(costumeId);
        userMapper.updateById(user);
    }

    /**
     * 新手初始化：设置昵称和初始装扮
     */
    public void initUser(Long userId, String nickname, Long avatarId) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new RuntimeException("用户不存在");

        if (user.getNickname() != null && !user.getNickname().isEmpty()) {
            throw new RuntimeException("用户已初始化");
        }

        // 新增，验证选择的装扮是否为"普通"风格
        if (avatarId != null) {
            Costume selectedCostume = costumeMapper.selectById(avatarId);
            if (selectedCostume == null || !"普通".equals(selectedCostume.getSort())) {
                throw new RuntimeException("只能选择普通风格的装扮");
            }
        }

        // 1. 更新用户基本信息
        user.setNickname(nickname);
        user.setAvatar(avatarId); // 设置当前穿戴的装扮
        user.setPoints(0);        // 初始货币 0
        user.setStatus("正常");
        userMapper.updateById(user);

        // 2. 【修改】只发放用户选择的这一个装扮，记录到拥有表中
        UserCostume userCostume = new UserCostume();
        userCostume.setUserId(userId);
        userCostume.setCostumeId(avatarId);
        userCostume.setObtainTime(LocalDateTime.now());
        userCostumeMapper.insert(userCostume);
    }

    public List<Costume> getNormalCostumes() {
        LambdaQueryWrapper<Costume> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Costume::getSort, "普通");
        return costumeMapper.selectList(wrapper);
    }

    /**
     * 获取用户拥有的指定角色类型的装扮
     */
    public List<Costume> getOwnedCostumesByRole(Long userId, Integer roleId) {
        // 1. 查询用户拥有的装扮 ID 列表
        LambdaQueryWrapper<UserCostume> ucWrapper = new LambdaQueryWrapper<>();
        ucWrapper.eq(UserCostume::getUserId, userId);
        List<UserCostume> userCostumes = userCostumeMapper.selectList(ucWrapper);
        List<Long> ownedIds = userCostumes.stream()
                .map(UserCostume::getCostumeId)
                .collect(Collectors.toList());

        if (ownedIds.isEmpty()) return List.of();

        // 2. 查询这些装扮的详细信息，并过滤指定角色类型
        LambdaQueryWrapper<Costume> cWrapper = new LambdaQueryWrapper<>();
        cWrapper.in(Costume::getId, ownedIds)
                .eq(Costume::getRoleId, roleId);
        return costumeMapper.selectList(cWrapper);

    }

    /**
     * 获取所有角色信息（去重后的角色列表）
     */
    public List<Map<String, Object>> getAllRoles() {
        // 查询所有装扮，按 roleId 分组，获取每个角色的名称
        LambdaQueryWrapper<Costume> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Costume::getRoleId, Costume::getName)
                .groupBy(Costume::getRoleId, Costume::getName);

        List<Costume> costumes = costumeMapper.selectList(wrapper);

        return costumes.stream()
                .map(costume -> {
                    Map<String, Object> role = new HashMap<>();
                    role.put("id", costume.getRoleId());
                    role.put("name", costume.getName());
                    return role;
                })
                .sorted((r1, r2) -> Integer.compare((Integer)r1.get("id"), (Integer)r2.get("id")))
                .collect(Collectors.toList());
    }

    /**
     * 获取用户成就统计信息
     */
    public Map<String, Object> getAchievements(Long userId) {
        Map<String, Object> result = new HashMap<>();

        LambdaQueryWrapper<UserCostume> ucWrapper = new LambdaQueryWrapper<>();
        ucWrapper.eq(UserCostume::getUserId, userId);
        long costumeCount = userCostumeMapper.selectCount(ucWrapper);

        LambdaQueryWrapper<Reward> rWrapper = new LambdaQueryWrapper<>();
        rWrapper.eq(Reward::getUserId, userId);
        long rewardCount = rewardMapper.selectCount(rWrapper);

        result.put("costumeCount", costumeCount);
        result.put("rewardCount", rewardCount);

        return result;
    }
}


