package com.example.habittt.service;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.habittt.entity.Admin;
import com.example.habittt.entity.Costume;
import com.example.habittt.entity.User;
import com.example.habittt.mapper.AdminMapper;
import com.example.habittt.mapper.CostumeMapper;
import com.example.habittt.mapper.UserMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    @Resource
    private AdminMapper adminMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private CostumeMapper costumeMapper;

    private static final SecretKey KEY = Keys.hmacShaKeyFor(
            "habittt-secret-key-for-jwt-token-generation".getBytes(StandardCharsets.UTF_8));

    /**
     * 管理员登录
     */
    public Map<String, Object> login(String username, String password) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, username);
        Admin admin = adminMapper.selectOne(wrapper);

        if (admin == null || !BCrypt.checkpw(password, admin.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        if ("封禁".equals(admin.getStatus())) {
            throw new RuntimeException("该管理员账号已被封禁");
        }

        String token = Jwts.builder()
                .subject(admin.getId().toString())
                .claim("username", admin.getUsername())
                .claim("role", "admin")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 17L * 24 * 60 * 60 * 1000))
                .signWith(KEY)
                .compact();

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("admin", admin);
        return result;
    }

    /**
     * 管理员注册（仅用于首次创建管理员）
     */
    public void register(String username, String password, String email) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, username);
        if (adminMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(BCrypt.hashpw(password));
        admin.setEmail(email);
        admin.setStatus("正常");
        admin.setCreateTime(LocalDateTime.now());
        admin.setUpdateTime(LocalDateTime.now());
        adminMapper.insert(admin);
    }

    /**
     * 获取所有用户列表
     */
    public List<User> getAllUsers() {
        return userMapper.selectList(null);
    }

    /**
     * 重置用户密码
     */
    public void resetUserPassword(Long userId, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setPassword(BCrypt.hashpw(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    /**
     * 封禁/解封用户
     */
    public void banUser(Long userId, String status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!"正常".equals(status) && !"封禁".equals(status)) {
            throw new RuntimeException("状态参数错误");
        }
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    /**
     * 添加新装扮
     */
    public void addCostume(Costume costume) {
        if (costume.getRoleId() == null || costume.getRoleId() < 1 || costume.getRoleId() > 14) {
            throw new RuntimeException("角色ID必须在1-14之间");
        }
        if (costume.getName() == null || costume.getName().isEmpty()) {
            throw new RuntimeException("装扮名称不能为空");
        }
        if (costume.getImageUrl() == null || costume.getImageUrl().isEmpty()) {
            throw new RuntimeException("图片URL不能为空");
        }
        if (costume.getSort() == null || costume.getSort().isEmpty()) {
            throw new RuntimeException("风格分类不能为空");
        }

        costume.setPrice(costume.getPrice() == null ? 0 : costume.getPrice());
        costumeMapper.insert(costume);
    }

    /**
     * 批量添加装扮（一次性上传14个角色的图片）
     */
    public void batchAddCostumes(List<Costume> costumes) {
        if (costumes == null || costumes.isEmpty()) {
            throw new RuntimeException("装扮列表不能为空");
        }
        for (Costume costume : costumes) {
            addCostume(costume);
        }
    }

    /**
     * 获取所有装扮风格分类
     */
    public List<String> getAllStyles() {
        LambdaQueryWrapper<Costume> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Costume::getSort)
                .groupBy(Costume::getSort);
        List<Costume> costumes = costumeMapper.selectList(wrapper);
        return costumes.stream()
                .map(Costume::getSort)
                .distinct()
                .toList();
    }
}

