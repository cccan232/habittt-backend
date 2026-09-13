package com.example.habittt.controller;

import com.example.habittt.common.JwtUtils;
import com.example.habittt.common.Result;
import com.example.habittt.entity.Task;
import com.example.habittt.service.TaskService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/task")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TaskController {

    @Resource
    private TaskService taskService;

    @GetMapping("/list")
    public Result<List<Task>> getTaskList(HttpServletRequest request) {
        Long userId = JwtUtils.getUserId(request);
        taskService.resetDailyTasks(userId);
        List<Task> tasks = taskService.getUserTasks(userId);
        return Result.success(tasks);
    }

    @PostMapping("/add")
    public Result<Task> addTask(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Long userId = JwtUtils.getUserId(request);
        String description = params.get("description");
        String level = params.get("level");
        Task task = taskService.addTask(userId, description, level);
        return Result.success(task);
    }

    @PutMapping("/complete/{taskId}")
    public Result<Map<String, Object>> completeTask(@PathVariable Long taskId, HttpServletRequest request) {
        Long userId = JwtUtils.getUserId(request);
        Map<String, Object> result = taskService.completeTask(userId, taskId);
        return Result.success(result);
    }

    @DeleteMapping("/{taskId}")
    public Result<Void> deleteTask(@PathVariable Long taskId, HttpServletRequest request) {
        Long userId = JwtUtils.getUserId(request);
        taskService.deleteTask(userId, taskId);
        return Result.success(null);
    }
}

