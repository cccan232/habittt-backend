package com.example.habittt.controller;

import com.example.habittt.common.JwtUtils;
import com.example.habittt.common.Result;
import com.example.habittt.service.CostumeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/costume")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CostumeController {

    @Resource
    private CostumeService costumeService;

    /**
     * 按角色查询商城装扮
     */
    @GetMapping("/role/{roleId}")
    public Result<List<Map<String, Object>>> getCostumesByRole(@PathVariable Integer roleId, HttpServletRequest request) {
        Long userId = JwtUtils.getUserIdFromToken(request.getHeader("Authorization"));
        return Result.success(costumeService.getCostumesByRole(roleId, userId));
    }

    /**
     * 按风格查询商城装扮
     */
    @GetMapping("/sort/{sort}")
    public Result<List<Map<String, Object>>> getCostumesBySort(@PathVariable String sort, HttpServletRequest request) {
        Long userId = JwtUtils.getUserIdFromToken(request.getHeader("Authorization"));
        return Result.success(costumeService.getCostumesBySort(sort, userId));
    }

    /**
     * 购买装扮
     */
    @PostMapping("/purchase")
    public Result<Map<String, Object>> purchaseCostume(@RequestBody Map<String, Long> request, HttpServletRequest httpRequest) {
        Long userId = JwtUtils.getUserIdFromToken(httpRequest.getHeader("Authorization"));
        Long costumeId = request.get("costumeId");
        return Result.success(costumeService.purchaseCostume(userId, costumeId));
    }

     /**
     * 获取所有风格分类
     */
    @GetMapping("/sorts")
    public Result<List<String>> getAllSorts() {
        return Result.success(costumeService.getAllSorts());
    }

    /**
     * 幸运箱抽奖
     */
    @PostMapping("/draw")
    public Result<Map<String, Object>> draw(@RequestBody Map<String, Integer> request, HttpServletRequest httpRequest) {
        Long userId = JwtUtils.getUserIdFromToken(httpRequest.getHeader("Authorization"));
        int count = request.getOrDefault("count", 1);
        return Result.success(costumeService.drawCostume(userId, count));
    }

}
