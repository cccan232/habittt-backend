package com.example.habittt.controller;

import com.example.habittt.common.JwtUtils;
import com.example.habittt.common.Result;
import com.example.habittt.entity.Costume;
import com.example.habittt.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Result<Void> register(@RequestBody Map<String, String> params) {
        try {
            userService.register(params.get("email"), params.get("password"), params.get("code"));
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        try {
            Map<String, Object> data = userService.login(
                    params.get("email"),
                    params.get("password"),
                    params.get("captchaKey"),
                    params.get("captcha")
            );
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }


    @PostMapping("/send-code")
    public Result<Void> sendCode(@RequestBody Map<String, String> params) {
        try {
            String email = params.get("email");
            String scene = params.get("scene"); // register 或 reset

            // 如果没有指定场景，默认为注册
            if (scene == null || scene.isEmpty()) {
                scene = "register";
            }

            userService.sendCode(email, scene);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }


    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@RequestBody Map<String, String> params) {
        try {
            userService.resetPassword(params.get("email"), params.get("code"), params.get("newPassword"));
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/captcha")
    public Result<Map<String, String>> getCaptcha(@RequestParam String key) {
        try {
            String text = userService.generateCaptcha(key);
            Map<String, String> data = new HashMap<>();
            data.put("text", text);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // 获取当前登录用户信息
    @GetMapping("/profile")
    public Result<Map<String, Object>> getProfile(HttpServletRequest request) {
        // 修改：使用 JwtUtils 工具类
        Long userId = com.example.habittt.common.JwtUtils.getUserId(request);
        Map<String, Object> profile = userService.getProfile(userId);
        return Result.success(profile);
    }

    // 新手初始化
    @PostMapping("/init")
    public Result<Void> initUser(@RequestBody Map<String, String> params, HttpServletRequest request) {
        // 修改：使用 JwtUtils 工具类
        Long userId = com.example.habittt.common.JwtUtils.getUserId(request);
        userService.initUser(userId, params.get("nickname"), Long.valueOf(params.get("avatarId")));
        return Result.success(null);
    }

    @GetMapping("/normal-costumes")
    public Result<List<Costume>> getNormalCostumes(HttpServletRequest request) {
        Long userId = JwtUtils.getUserId(request);
        List<Costume> costumes = userService.getNormalCostumes();
        return Result.success(costumes);
    }

    // 更换装扮
    @PutMapping("/costume")
    public Result<Void> changeCostume(@RequestBody Map<String, Long> params, HttpServletRequest request) {
        // 修改：使用 JwtUtils 工具类
        Long userId = com.example.habittt.common.JwtUtils.getUserId(request);
        userService.changeCostume(userId, params.get("costumeId"));
        return Result.success(null);
    }

    // 获取用户拥有的指定角色类型的装扮
    @GetMapping("/costumes")
    public Result<List<Costume>> getOwnedCostumes(HttpServletRequest request, @RequestParam Integer roleId) {
        Long userId = JwtUtils.getUserId(request);
        List<Costume> costumes = userService.getOwnedCostumesByRole(userId, roleId);
        return Result.success(costumes);
    }

    // 获取所有角色信息
    @GetMapping("/roles")
    public Result<List<Map<String, Object>>> getAllRoles(HttpServletRequest request) {
        Long userId = JwtUtils.getUserId(request);
        List<Map<String, Object>> roles = userService.getAllRoles();
        return Result.success(roles);
    }

    // 获取用户成就统计
    @GetMapping("/achievements")
    public Result<Map<String, Object>> getAchievements(HttpServletRequest request) {
        Long userId = JwtUtils.getUserId(request);
        return Result.success(userService.getAchievements(userId));
    }

}

