package com.example.habittt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.habittt.entity.Task;
import com.example.habittt.entity.User;
import com.example.habittt.mapper.TaskMapper;
import com.example.habittt.mapper.TeamMapper;
import com.example.habittt.mapper.UserMapper;
import com.example.habittt.common.WebSocketServer;
import com.example.habittt.mapper.TeamMemberMapper;
import com.example.habittt.entity.TeamMember;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TaskService {

    @Resource
    private TaskMapper taskMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private TeamMapper teamMapper;

    @Resource
    private TeamMemberMapper teamMemberMapper;

    private static final Map<String, Integer> REWARD_MAP = Map.of(
            "easy", 1,
            "normal", 5,
            "hard", 10
    );

    public List<Task> getUserTasks(Long userId) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getUserId, userId)
                .orderByDesc(Task::getCreateTime);
        return taskMapper.selectList(wrapper);
    }

    @Transactional
    public Task addTask(Long userId, String description, String level) {
        if (!REWARD_MAP.containsKey(level)) {
            throw new RuntimeException("无效的任务难度");
        }

        Task task = new Task();
        task.setUserId(userId);
        task.setDescription(description);
        task.setLevel(level);
        task.setStatus("未完成");
        taskMapper.insert(task);
        return task;
    }

    @Transactional
    public Map<String, Object> completeTask(Long userId, Long taskId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        if (!task.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作该任务");
        }
        if ("已完成".equals(task.getStatus())) {
            throw new RuntimeException("任务已完成");
        }

        task.setStatus("已完成");
        task.setUpdateTime(LocalDateTime.now()); // 更新时间
        taskMapper.updateById(task);

        int reward = REWARD_MAP.get(task.getLevel());
        User user = userMapper.selectById(userId);
        user.setPoints(user.getPoints() + reward);
        userMapper.updateById(user);

        // 【删除】之前添加的 WebSocket 进度推送逻辑，保持这里简洁

        Map<String, Object> result = new HashMap<>();
        result.put("task", task);
        result.put("newPoints", user.getPoints());
        result.put("reward", reward);
        return result;
    }

    @Transactional
    public void resetDailyTasks(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();

        // 只重置今天凌晨之前完成的任务（即昨天及更早的）
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getUserId, userId)
                .eq(Task::getStatus, "已完成")
                .lt(Task::getUpdateTime, todayStart);

        List<Task> completedTasks = taskMapper.selectList(wrapper);
        for (Task task : completedTasks) {
            task.setStatus("未完成");
            taskMapper.updateById(task);
        }
    }

    // ... existing code ...


    @Transactional
    public void deleteTask(Long userId, Long taskId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        if (!task.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作该任务");
        }
        taskMapper.deleteById(taskId);
    }
}
