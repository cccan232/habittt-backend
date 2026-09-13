package com.example.habittt.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.habittt.entity.Task;
import com.example.habittt.entity.User;
import com.example.habittt.mapper.TaskMapper;
import com.example.habittt.mapper.UserMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
public class DailyTaskResetScheduler {

    @Resource
    private TaskMapper taskMapper;

    @Resource
    private UserMapper userMapper;

    /**
     * 每天凌晨 0:00 执行任务重置
     * cron 表达式：秒 分 时 日 月 周
     * "0 0 8 * * ?" 表示每天早上八点执行
     * "0 * * * * ?" 每分钟一次
     * “0/10 * * * * ？” 每十秒一次
     */
    @Scheduled(cron = "0 0 8 * * ?")
    @Transactional
    public void resetAllUsersDailyTasks() {
        log.info("开始执行每日任务重置...");

        try {
            LocalDate today = LocalDate.now();

            // 获取所有用户
            List<User> allUsers = userMapper.selectList(null);
            int resetUserCount = 0;
            int resetTaskCount = 0;

            for (User user : allUsers) {
                // 查找该用户昨天及之前完成的任务
                LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Task::getUserId, user.getId())
                        .eq(Task::getStatus, "已完成");
                        //.lt(Task::getUpdateTime, today.atStartOfDay());

                List<Task> completedTasks = taskMapper.selectList(wrapper);

                if (!completedTasks.isEmpty()) {
                    // 重置这些任务为未完成状态
                    for (Task task : completedTasks) {
                        task.setStatus("未完成");
                        taskMapper.updateById(task);
                        resetTaskCount++;
                    }
                    resetUserCount++;
                    log.info("用户 {} (ID: {}) 的 {} 个任务已重置", user.getNickname(), user.getId(), completedTasks.size());
                }
            }

            log.info("每日任务重置完成！共重置 {} 个用户的 {} 个任务", resetUserCount, resetTaskCount);

        } catch (Exception e) {
            log.error("每日任务重置失败！", e);
            throw e;
        }
    }
}
