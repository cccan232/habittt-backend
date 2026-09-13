package com.example.habittt.controller;

import com.example.habittt.common.JwtUtils;
import com.example.habittt.common.Result;
import com.example.habittt.entity.Reward;
import com.example.habittt.service.RewardService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reward")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RewardController {

    @Resource
    private RewardService rewardService;

    @PostMapping("/universal-card")
    public Result<Map<String, Object>> purchaseUniversalCard(@RequestBody Map<String, Integer> request, HttpServletRequest httpRequest) {
        Long userId = JwtUtils.getUserIdFromToken(httpRequest.getHeader("Authorization"));
        Integer price = request.get("price");
        return Result.success(rewardService.purchaseUniversalCard(userId, price));
    }

    @PostMapping("/create")
    public Result<Map<String, Object>> createReward(@RequestBody Reward reward, HttpServletRequest httpRequest) {
        Long userId = JwtUtils.getUserIdFromToken(httpRequest.getHeader("Authorization"));
        return Result.success(rewardService.createReward(userId, reward.getDescription(), reward.getImage(), reward.getPrice()));
    }

    @GetMapping("/list")
    public Result<List<Reward>> getUserRewards(HttpServletRequest request) {
        Long userId = JwtUtils.getUserIdFromToken(request.getHeader("Authorization"));
        return Result.success(rewardService.getUserRewards(userId));
    }

    @PostMapping("/purchase/{rewardId}")
    public Result<Map<String, Object>> purchaseReward(@PathVariable Long rewardId, HttpServletRequest request) {
        Long userId = JwtUtils.getUserIdFromToken(request.getHeader("Authorization"));
        return Result.success(rewardService.purchaseReward(userId, rewardId));
    }
}