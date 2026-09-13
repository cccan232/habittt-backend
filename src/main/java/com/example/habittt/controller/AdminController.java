package com.example.habittt.controller;

import com.example.habittt.common.Result;
import com.example.habittt.entity.Costume;
import com.example.habittt.entity.User;
import com.example.habittt.service.AdminService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AdminController {

    @Resource
    private AdminService adminService;

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        try {
            Map<String, Object> data = adminService.login(
                    params.get("username"),
                    params.get("password")
            );
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员注册（仅首次使用）
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody Map<String, String> params) {
        try {
            adminService.register(
                    params.get("username"),
                    params.get("password"),
                    params.get("email")
            );
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取所有用户列表
     */
    @GetMapping("/users")
    public Result<List<User>> getAllUsers() {
        try {
            List<User> users = adminService.getAllUsers();
            return Result.success(users);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 重置用户密码
     */
    @PutMapping("/user/{userId}/password")
    public Result<Void> resetUserPassword(
            @PathVariable Long userId,
            @RequestBody Map<String, String> params) {
        try {
            adminService.resetUserPassword(userId, params.get("newPassword"));
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 封禁/解封用户
     */
    @PutMapping("/user/{userId}/status")
    public Result<Void> banUser(
            @PathVariable Long userId,
            @RequestBody Map<String, String> params) {
        try {
            adminService.banUser(userId, params.get("status"));
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 添加单个装扮
     */
    @PostMapping("/costume")
    public Result<Void> addCostume(@RequestBody Costume costume) {
        try {
            adminService.addCostume(costume);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量添加装扮
     */
    @PostMapping("/costumes/batch")
    public Result<Void> batchAddCostumes(@RequestBody List<Costume> costumes) {
        try {
            adminService.batchAddCostumes(costumes);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取所有装扮风格分类
     */
    @GetMapping("/costume-styles")
    public Result<List<String>> getAllStyles() {
        try {
            List<String> styles = adminService.getAllStyles();
            return Result.success(styles);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
