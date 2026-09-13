package com.example.habittt.controller;

import com.example.habittt.common.JwtUtils;
import com.example.habittt.common.Result;
import com.example.habittt.service.TeamService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/team")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TeamController {

    @Resource
    private TeamService teamService;

    /**
     * 获取大厅队伍列表
     */
    @GetMapping("/lobby")
    public Result getTeamLobby(HttpServletRequest request) {
        List<Map<String, Object>> teams = teamService.getTeamLobby();
        return Result.success(teams);
    }

    /**
     * 创建队伍
     */
    @PostMapping("/create")
    public Result createTeam(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Long userId = JwtUtils.getUserId(request);
        String teamName = params.get("teamName");
        String description = params.get("description");

        teamService.createTeam(userId, teamName, description);
        return Result.success("队伍创建成功");
    }

    /**
     * 获取队伍详情
     */
    @GetMapping("/{teamId}")
    public Result getTeamDetail(@PathVariable Long teamId) {
        Map<String, Object> detail = teamService.getTeamDetail(teamId);
        return Result.success(detail);
    }

    /**
     * 申请加入队伍
     */
    @PostMapping("/apply")
    public Result applyTeam(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = JwtUtils.getUserId(request);
        Long teamId = Long.valueOf(params.get("teamId").toString());
        String message = params.get("message").toString();

        teamService.applyTeam(userId, teamId, message);
        return Result.success("申请已发送");
    }

    /**
     * 处理队伍申请
     */
    @PostMapping("/handle-application")
    public Result handleApplication(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = JwtUtils.getUserId(request);
        Long applicationId = Long.valueOf(params.get("applicationId").toString());
        String action = params.get("action").toString();

        teamService.handleApplication(applicationId, userId, action);
        return Result.success("处理成功");
    }

    /**
     * 获取队伍聊天消息
     */
    @GetMapping("/{teamId}/chat")
    public Result getTeamChat(@PathVariable Long teamId, HttpServletRequest request) {
        Long userId = JwtUtils.getUserId(request);
        List<Map<String, Object>> chats = teamService.getTeamChat(teamId, userId);
        return Result.success(chats);
    }

    /**
     * 发送聊天消息
     */
    @PostMapping("/chat")
    public Result sendChatMessage(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = JwtUtils.getUserId(request);
        Long teamId = Long.valueOf(params.get("teamId").toString());
        String message = params.get("message").toString();

        teamService.sendChatMessage(teamId, userId, message);
        return Result.success("发送成功");
    }

    /**
     * 队长选择挑战难度
     */
    @PostMapping("/select-level")
    public Result selectChallengeLevel(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = JwtUtils.getUserId(request);
        Long teamId = Long.valueOf(params.get("teamId").toString());
        String level = params.get("level").toString();

        teamService.selectChallengeLevel(teamId, userId, level);
        return Result.success("挑战难度已选择");
    }

    /**
     * 队员确认接受挑战
     */
    @PostMapping("/confirm-challenge")
    public Result confirmChallenge(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Long userId = JwtUtils.getUserId(request);
        Long teamId = Long.valueOf(params.get("teamId").toString());

        teamService.confirmChallenge(teamId, userId);
        return Result.success("已确认接受挑战");
    }

    /**
     * 提醒队员完成任务
     */
    @PostMapping("/remind-member")
    public Result remindMember(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = JwtUtils.getUserId(request);
        Long teamId = Long.valueOf(params.get("teamId").toString());
        Long memberId = Long.valueOf(params.get("memberId").toString());

        teamService.remindMember(teamId, userId, memberId);
        return Result.success("提醒已发送");
    }

    /**
     * 获取我的队伍
     */
    @GetMapping("/my-team")
    public Result getMyTeam(HttpServletRequest request) {
        Long userId = JwtUtils.getUserId(request);
        Map<String, Object> team = teamService.getMyTeam(userId);
        return Result.success(team);
    }

    /**
     * 获取通知列表
     */
    @GetMapping("/notifications")
    public Result getNotifications(HttpServletRequest request) {
        Long userId = JwtUtils.getUserId(request);
        List<Map<String, Object>> notifications = teamService.getNotifications(userId);
        return Result.success(notifications);
    }

    /**
     * 标记通知为已读
     */
    @PostMapping("/notifications/{notificationId}/read")
    public Result markNotificationRead(@PathVariable Long notificationId, HttpServletRequest request) {
        Long userId = JwtUtils.getUserId(request);
        teamService.markNotificationRead(notificationId, userId);
        return Result.success();
    }

    /**
     * 获取未读通知数量
     */
    @GetMapping("/notifications/unread-count")
    public Result getUnreadNotificationCount(HttpServletRequest request) {
        Long userId = JwtUtils.getUserId(request);
        Long count = teamService.getUnreadNotificationCount(userId);
        return Result.success(count);
    }

    /**
     * 获取待处理的申请列表
     */
    @GetMapping("/pending-applications")
    public Result getPendingApplications(HttpServletRequest request) {
        Long userId = JwtUtils.getUserId(request);
        List<Map<String, Object>> applications = teamService.getPendingApplications(userId);
        return Result.success(applications);
    }

    /**
     * 删除通知
     */
    @DeleteMapping("/notifications/{notificationId}")
    public Result deleteNotification(@PathVariable Long notificationId, HttpServletRequest request) {
        Long userId = JwtUtils.getUserId(request);
        teamService.deleteNotification(notificationId, userId);
        return Result.success("删除成功");
    }

    /**
     * 清空所有通知
     */
    @DeleteMapping("/notifications/clear")
    public Result clearAllNotifications(HttpServletRequest request) {
        Long userId = JwtUtils.getUserId(request);
        teamService.clearAllNotifications(userId);
        return Result.success("清空成功");
    }

}
