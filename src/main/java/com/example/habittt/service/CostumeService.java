package com.example.habittt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.habittt.entity.Costume;
import com.example.habittt.entity.User;
import com.example.habittt.entity.UserCostume;
import com.example.habittt.mapper.CostumeMapper;
import com.example.habittt.mapper.UserCostumeMapper;
import com.example.habittt.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CostumeService {

    @Resource
    private CostumeMapper costumeMapper;

    @Resource
    private UserCostumeMapper userCostumeMapper;

    @Resource
    private UserMapper userMapper;

    /**
     * 按角色查询商城装扮（返回是否已拥有）
     */
    public List<Map<String, Object>> getCostumesByRole(Integer roleId, Long userId) {
        LambdaQueryWrapper<Costume> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Costume::getRoleId, roleId);
        List<Costume> costumes = costumeMapper.selectList(wrapper);

        // 查询用户已拥有的装扮
        LambdaQueryWrapper<UserCostume> ucWrapper = new LambdaQueryWrapper<>();
        ucWrapper.eq(UserCostume::getUserId, userId);
        List<UserCostume> userCostumes = userCostumeMapper.selectList(ucWrapper);
        List<Long> ownedIds = userCostumes.stream()
                .map(UserCostume::getCostumeId)
                .toList();

        // 组装返回数据
        return costumes.stream().map(costume -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", costume.getId());
            map.put("roleId", costume.getRoleId());
            map.put("name", costume.getName());
            map.put("imageUrl", costume.getImageUrl());
            map.put("price", costume.getPrice());
            map.put("sort", costume.getSort());
            map.put("owned", ownedIds.contains(costume.getId()));
            return map;
        }).toList();
    }

    /**
     * 按风格查询商城装扮（返回是否已拥有）
     */
    public List<Map<String, Object>> getCostumesBySort(String sort, Long userId) {
        LambdaQueryWrapper<Costume> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Costume::getSort, sort);
        List<Costume> costumes = costumeMapper.selectList(wrapper);

        // 查询用户已拥有的装扮
        LambdaQueryWrapper<UserCostume> ucWrapper = new LambdaQueryWrapper<>();
        ucWrapper.eq(UserCostume::getUserId, userId);
        List<UserCostume> userCostumes = userCostumeMapper.selectList(ucWrapper);
        List<Long> ownedIds = userCostumes.stream()
                .map(UserCostume::getCostumeId)
                .toList();

        // 组装返回数据
        return costumes.stream().map(costume -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", costume.getId());
            map.put("roleId", costume.getRoleId());
            map.put("name", costume.getName());
            map.put("imageUrl", costume.getImageUrl());
            map.put("price", costume.getPrice());
            map.put("sort", costume.getSort());
            map.put("owned", ownedIds.contains(costume.getId()));
            return map;
        }).toList();
    }


    /**
     * 购买装扮（事务控制）
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> purchaseCostume(Long userId, Long costumeId) {
        Map<String, Object> result = new HashMap<>();

        // 1. 查询装扮信息
        Costume costume = costumeMapper.selectById(costumeId);
        if (costume == null) {
            result.put("success", false);
            result.put("message", "装扮不存在");
            return result;
        }

        // 2. 检查是否已拥有
        LambdaQueryWrapper<UserCostume> ucWrapper = new LambdaQueryWrapper<>();
        ucWrapper.eq(UserCostume::getUserId, userId)
                .eq(UserCostume::getCostumeId, costumeId);
        if (userCostumeMapper.selectCount(ucWrapper) > 0) {
            result.put("success", false);
            result.put("message", "您已拥有该装扮");
            return result;
        }

        // 3. 查询用户余额
        User user = userMapper.selectById(userId);
        if (user.getPoints() < costume.getPrice()) {
            result.put("success", false);
            result.put("message", "货币不足");
            return result;
        }

        // 4. 扣除货币
        user.setPoints(user.getPoints() - costume.getPrice());
        userMapper.updateById(user);

        // 5. 添加用户装扮记录
        UserCostume userCostume = new UserCostume();
        userCostume.setUserId(userId);
        userCostume.setCostumeId(costumeId);
        userCostume.setObtainTime(LocalDateTime.now());
        userCostumeMapper.insert(userCostume);

        result.put("success", true);
        result.put("message", "购买成功");
        result.put("remainingPoints", user.getPoints());
        return result;
    }

    /**
     * 获取所有风格分类
     */
    public List<String> getAllSorts() {
        LambdaQueryWrapper<Costume> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Costume::getSort)
                .groupBy(Costume::getSort);
        List<Costume> costumes = costumeMapper.selectList(wrapper);
        return costumes.stream()
                .map(Costume::getSort)
                .distinct()
                .toList();
    }

    /**
     * 幸运箱抽奖（事务控制）
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> drawCostume(Long userId, int count) {
        Map<String, Object> result = new HashMap<>();

        // 1. 查询用户余额
        User user = userMapper.selectById(userId);
        int totalPrice = count == 1 ? 100 : 450;

        if (user.getPoints() < totalPrice) {
            result.put("success", false);
            result.put("message", "货币不足");
            return result;
        }

        // 2. 扣除货币
        user.setPoints(user.getPoints() - totalPrice);
        userMapper.updateById(user);

        // 3. 随机抽取装扮
        List<Costume> allCostumes = costumeMapper.selectList(null);
        if (allCostumes.isEmpty()) {
            result.put("success", false);
            result.put("message", "商城暂无装扮");
            return result;
        }

        Random random = new Random();
        List<Map<String, Object>> wonList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Costume c = allCostumes.get(random.nextInt(allCostumes.size()));

            // 检查是否已拥有，未拥有则添加到 user_costume 表
            LambdaQueryWrapper<UserCostume> ucWrapper = new LambdaQueryWrapper<>();
            ucWrapper.eq(UserCostume::getUserId, userId)
                    .eq(UserCostume::getCostumeId, c.getId());
            if (userCostumeMapper.selectCount(ucWrapper) == 0) {
                UserCostume uc = new UserCostume();
                uc.setUserId(userId);
                uc.setCostumeId(c.getId());
                uc.setObtainTime(LocalDateTime.now());
                userCostumeMapper.insert(uc);
            }

            // 组装返回数据
            Map<String, Object> m = new HashMap<>();
            m.put("id", c.getId());
            m.put("name", c.getName());
            m.put("imageUrl", c.getImageUrl());
            wonList.add(m);
        }

        result.put("success", true);
        result.put("message", "抽奖成功");
        result.put("result", count == 1 ? wonList.get(0) : wonList);
        result.put("remainingPoints", user.getPoints());
        return result;
    }
}
