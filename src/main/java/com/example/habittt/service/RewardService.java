package com.example.habittt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.habittt.entity.Reward;
import com.example.habittt.entity.User;
import com.example.habittt.mapper.RewardMapper;
import com.example.habittt.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RewardService {

    @Resource
    private RewardMapper rewardMapper;

    @Resource
    private UserMapper userMapper;

    /**
     * 购买万能卡（增加自定义奖励资格次数）
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> purchaseUniversalCard(Long userId, Integer price) {
        Map<String, Object> result = new HashMap<>();

        // 1. 查询用户余额
        User user = userMapper.selectById(userId);
        if (user.getPoints() < price) {
            result.put("success", false);
            result.put("message", "货币不足");
            return result;
        }

        // 2. 扣除货币
        user.setPoints(user.getPoints() - price);
        // 3. 增加自定义奖励资格次数
        user.setCreateQuota(user.getCreateQuota() == null ? 1 : user.getCreateQuota() + 1);
        userMapper.updateById(user);

        result.put("success", true);
        result.put("message", "购买成功");
        result.put("createQuota", user.getCreateQuota());
        result.put("remainingPoints", user.getPoints());
        return result;
    }

    /**
     * 创建自定义奖励
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> createReward(Long userId, String description, String image, Integer price) {
        Map<String, Object> result = new HashMap<>();

        // 1. 检查用户是否有资格
        User user = userMapper.selectById(userId);
        if (user.getCreateQuota() == null || user.getCreateQuota() <= 0) {
            result.put("success", false);
            result.put("message", "没有创建资格");
            return result;
        }

        // 2. 创建奖励
        Reward reward = new Reward();
        reward.setUserId(userId);
        reward.setDescription(description);
        reward.setImage(image);
        reward.setPrice(price);
        rewardMapper.insert(reward);

        // 3. 减少资格次数
        user.setCreateQuota(user.getCreateQuota() - 1);
        userMapper.updateById(user);

        result.put("success", true);
        result.put("message", "创建成功");
        result.put("createQuota", user.getCreateQuota());
        result.put("reward", reward);
        return result;
    }

    /**
     * 获取用户的自定义奖励列表
     */
    public List<Reward> getUserRewards(Long userId) {
        LambdaQueryWrapper<Reward> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reward::getUserId, userId);
        return rewardMapper.selectList(wrapper);
    }

    /**
     * 购买自定义奖励
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> purchaseReward(Long userId, Long rewardId) {
        Map<String, Object> result = new HashMap<>();

        // 1. 查询奖励信息
        Reward reward = rewardMapper.selectById(rewardId);
        if (reward == null) {
            result.put("success", false);
            result.put("message", "奖励不存在");
            return result;
        }

        // 2. 验证是否是当前用户的奖励
        if (!reward.getUserId().equals(userId)) {
            result.put("success", false);
            result.put("message", "无权购买此奖励");
            return result;
        }

        // 3. 查询用户余额
        User user = userMapper.selectById(userId);
        if (user.getPoints() < reward.getPrice()) {
            result.put("success", false);
            result.put("message", "货币不足");
            return result;
        }

        // 4. 扣除货币
        user.setPoints(user.getPoints() - reward.getPrice());
        // 5. 增加自定义奖励资格次数
        user.setCreateQuota(user.getCreateQuota() == null ? 1 : user.getCreateQuota() + 1);
        userMapper.updateById(user);

        // 6. 删除已购买的奖励记录
        rewardMapper.deleteById(rewardId);

        result.put("success", true);
        result.put("message", "购买成功");
        result.put("createQuota", user.getCreateQuota());
        result.put("remainingPoints", user.getPoints());
        return result;
    }

}
