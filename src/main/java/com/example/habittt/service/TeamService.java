package com.example.habittt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.habittt.entity.*;
import com.example.habittt.mapper.*;
import com.example.habittt.entity.Task;
import com.example.habittt.mapper.TaskMapper;
import com.example.habittt.common.WebSocketServer;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TeamService {

    @Resource
    private TeamMapper teamMapper;

    @Resource
    private TeamMemberMapper teamMemberMapper;

    @Resource
    private TeamApplicationMapper teamApplicationMapper;

    @Resource
    private NotificationMapper notificationMapper;

    @Resource
    private TeamChatMapper teamChatMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private CostumeMapper costumeMapper;

    @Resource
    private TaskMapper taskMapper;

    /**
     * 获取大厅所有队伍列表
     */
    public List<Map<String, Object>> getTeamLobby() {
        List<Team> teams = teamMapper.selectList(null);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Team team : teams) {
            Map<String, Object> teamInfo = new HashMap<>();
            teamInfo.put("teamId", team.getTeamId());
            teamInfo.put("teamName", team.getTeamName());
            teamInfo.put("level", team.getLevel() != null ? team.getLevel() : "-");
            teamInfo.put("creatorId", team.getCreatorId());

            // 获取队长信息
            User creator = userMapper.selectById(team.getCreatorId());
            teamInfo.put("creatorName", creator != null ? creator.getNickname() : "未知");

            // 获取当前人数
            LambdaQueryWrapper<TeamMember> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TeamMember::getTeamId, team.getTeamId());
            long memberCount = teamMemberMapper.selectCount(wrapper);
            teamInfo.put("memberCount", memberCount);

            result.add(teamInfo);
        }

        return result;
    }

    /**
     * 创建队伍
     */
    @Transactional
    public void createTeam(Long userId, String teamName, String description) {
        // 检查用户是否已经在队伍中
        User user = userMapper.selectById(userId);
        if (user.getTeamId() != null) {
            throw new RuntimeException("你已经在队伍中，无法创建新队伍");
        }

        // 创建队伍
        Team team = new Team();
        team.setTeamName(teamName);
        team.setCreatorId(userId);
        team.setDescription(description);
        team.setStatus("waiting");
        teamMapper.insert(team);

        // 队长加入队伍
        TeamMember member = new TeamMember();
        member.setTeamId(team.getTeamId());
        member.setUserId(userId);
        member.setRole("captain");
        teamMemberMapper.insert(member);

        // 更新用户的teamId
        user.setTeamId(team.getTeamId());
        userMapper.updateById(user);
    }


    /**
     * 获取队伍详情
     */
    public Map<String, Object> getTeamDetail(Long teamId) {
        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            throw new RuntimeException("队伍不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("teamId", team.getTeamId());
        result.put("teamName", team.getTeamName());
        result.put("description", team.getDescription());
        result.put("level", team.getLevel() != null ? team.getLevel() : "-");
        result.put("creatorId", team.getCreatorId());

        // 获取队长信息
        User creator = userMapper.selectById(team.getCreatorId());
        result.put("creatorName", creator != null ? creator.getNickname() : "未知");
        // 修改：返回头像URL而不是ID
        if (creator != null && creator.getAvatar() != null) {
            Costume creatorCostume = costumeMapper.selectById(creator.getAvatar());
            result.put("creatorAvatar", creatorCostume != null ? creatorCostume.getImageUrl() : null);
        } else {
            result.put("creatorAvatar", null);
        }

        // 获取所有成员
        LambdaQueryWrapper<TeamMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamMember::getTeamId, teamId);
        List<TeamMember> members = teamMemberMapper.selectList(wrapper);

        List<Map<String, Object>> memberList = new ArrayList<>();
        // 获取成员时计算进度
        for (TeamMember member : members) {
            User memberUser = userMapper.selectById(member.getUserId());
            Map<String, Object> memberInfo = new HashMap<>();
            memberInfo.put("userId", member.getUserId());
            memberInfo.put("nickname", memberUser.getNickname());
            // 修改：返回头像URL而不是ID
            if (memberUser != null && memberUser.getAvatar() != null) {
                Costume memberCostume = costumeMapper.selectById(memberUser.getAvatar());
                memberInfo.put("avatar", memberCostume != null ? memberCostume.getImageUrl() : null);
            } else {
                memberInfo.put("avatar", null);
            }
            memberInfo.put("role", member.getRole());

            // 获取该用户所有的任务
            LambdaQueryWrapper<Task> allTaskWrapper = new LambdaQueryWrapper<>();
            allTaskWrapper.eq(Task::getUserId, member.getUserId());
            List<Task> allTasks = taskMapper.selectList(allTaskWrapper);
            long total = allTasks.size();

            // 获取今天更新过且状态为已完成的任务
            LambdaQueryWrapper<Task> todayDoneWrapper = new LambdaQueryWrapper<>();
            todayDoneWrapper.eq(Task::getUserId, member.getUserId())
                    .ge(Task::getUpdateTime, LocalDate.now().atStartOfDay())
                    .eq(Task::getStatus, "已完成");
            long done = taskMapper.selectCount(todayDoneWrapper);

            // 计算进度：今日完成数 / 总任务数
            int progress = total == 0 ? 0 : (int)((done * 100) / total);

            memberInfo.put("progress", progress);
            memberList.add(memberInfo);
        }
        result.put("members", memberList);// 把成员列表也返回
        return result;
    }

    /**
     * 申请加入队伍
     */
    @Transactional
    public void applyTeam(Long userId, Long teamId, String message) {
        // 检查用户是否已经在该队伍中
        User user = userMapper.selectById(userId);
        if (user.getTeamId() != null && user.getTeamId().equals(teamId)) {
            throw new RuntimeException("你已经是该队伍的成员，无法重复申请");
        }
        // 检查是否已申请
        LambdaQueryWrapper<TeamApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamApplication::getTeamId, teamId)
                .eq(TeamApplication::getApplicantId, userId)
                .eq(TeamApplication::getStatus, "pending");
        if (teamApplicationMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("你已经申请过该队伍，请等待队长处理");
        }

        // 创建申请记录
        TeamApplication application = new TeamApplication();
        application.setTeamId(teamId);
        application.setApplicantId(userId);
        application.setMessage(message);
        application.setStatus("pending");
        teamApplicationMapper.insert(application);

        // 给队长发送通知
        Team team = teamMapper.selectById(teamId);
        User applicant = userMapper.selectById(userId);

        Notification notification = new Notification();
        notification.setUserId(team.getCreatorId());
        notification.setType("team_apply");
        notification.setTitle("队伍申请");
        notification.setContent(applicant.getNickname() + " 申请加入你的队伍 " + team.getTeamName());
        notification.setIsRead(0);
        notification.setRelatedId(application.getId());
        notificationMapper.insert(notification);
        // 实时推送通知
        WebSocketServer.sendMessageToUser(team.getCreatorId().toString(),
                "{\"type\":\"notification\",\"title\":\"队伍申请\",\"content\":\"" + applicant.getNickname() + " 申请加入你的队伍\"}");

    }

    /**
     * 删除通知
     */
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification != null && notification.getUserId().equals(userId)) {
            notificationMapper.deleteById(notificationId);
        }
    }

    /**
     * 清空用户的所有通知
     */
    public void clearAllNotifications(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId);
        notificationMapper.delete(wrapper);
    }

    /**
     * 处理队伍申请（同意/拒绝）
     */
    @Transactional
    public void handleApplication(Long applicationId, Long captainId, String action) {
        TeamApplication application = teamApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new RuntimeException("申请记录不存在");
        }

        // 验证是否是队长
        Team team = teamMapper.selectById(application.getTeamId());
        if (!team.getCreatorId().equals(captainId)) {
            throw new RuntimeException("只有队长可以处理申请");
        }

        if (!"pending".equals(application.getStatus())) {
            throw new RuntimeException("该申请已处理");
        }

        application.setHandleTime(LocalDateTime.now());

        if ("accept".equals(action)) {
            application.setStatus("accepted");
            teamApplicationMapper.updateById(application);

            // 将申请者加入队伍
            TeamMember member = new TeamMember();
            member.setTeamId(application.getTeamId());
            member.setUserId(application.getApplicantId());
            member.setRole("member");
            teamMemberMapper.insert(member);

            // 更新用户的teamId
            User applicant = userMapper.selectById(application.getApplicantId());
            applicant.setTeamId(application.getTeamId());
            userMapper.updateById(applicant);

            // 发送通知给申请者
            Notification notification = new Notification();
            notification.setUserId(application.getApplicantId());
            notification.setType("team_accept");
            notification.setTitle("队伍申请通过");
            notification.setContent("队长 " + team.getTeamName() + " 同意了你的加入申请");
            notification.setIsRead(0);
            notification.setRelatedId(application.getTeamId());
            notificationMapper.insert(notification);
            WebSocketServer.sendMessageToUser(application.getApplicantId().toString(),
                    "{\"type\":\"notification\",\"title\":\"队伍申请通过\",\"content\":\"队长同意了你的加入申请\"}");
        } else if ("reject".equals(action)) {
            application.setStatus("rejected");
            teamApplicationMapper.updateById(application);

            // 发送通知给申请者
            Notification notification = new Notification();
            notification.setUserId(application.getApplicantId());
            notification.setType("team_reject");
            notification.setTitle("队伍申请被拒绝");
            notification.setContent("你的加入申请被 " + team.getTeamName() + " 拒绝了");
            notification.setIsRead(0);
            notification.setRelatedId(application.getTeamId());
            notificationMapper.insert(notification);
            WebSocketServer.sendMessageToUser(application.getApplicantId().toString(),
                    "{\"type\":\"notification\",\"title\":\"队伍申请被拒\",\"content\":\"你的加入申请被拒绝了\"}");

        }

        // 标记原申请通知为已处理
        LambdaQueryWrapper<Notification> notifWrapper = new LambdaQueryWrapper<>();
        notifWrapper.eq(Notification::getRelatedId, applicationId)
                .eq(Notification::getType, "team_apply");
        List<Notification> relatedNotifications = notificationMapper.selectList(notifWrapper);
        for (Notification notif : relatedNotifications) {
            // 设置自定义属性标记已处理（这里用 isRead=2 表示已处理，0=未读，1=已读）
            notif.setIsRead(2); // 2 表示已处理
            notificationMapper.updateById(notif);
        }
    }

    /**
     * 获取队伍聊天消息
     */
    public List<Map<String, Object>> getTeamChat(Long teamId, Long userId) {
        // 验证是否是队伍成员
        LambdaQueryWrapper<TeamMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamMember::getTeamId, teamId)
                .eq(TeamMember::getUserId, userId);
        if (teamMemberMapper.selectCount(wrapper) == 0) {
            throw new RuntimeException("你不是该队伍成员");
        }

        LambdaQueryWrapper<TeamChat> chatWrapper = new LambdaQueryWrapper<>();
        chatWrapper.eq(TeamChat::getTeamId, teamId)
                .orderByAsc(TeamChat::getCreateTime);
        List<TeamChat> chats = teamChatMapper.selectList(chatWrapper);

        List<Map<String, Object>> result = new ArrayList<>();
        for (TeamChat chat : chats) {
            Map<String, Object> chatInfo = new HashMap<>();
            chatInfo.put("chatId", chat.getId());
            chatInfo.put("senderId", chat.getSenderId());
            chatInfo.put("message", chat.getMessage());
            chatInfo.put("messageType", chat.getMessageType());
            chatInfo.put("createTime", chat.getCreateTime());

            User sender = userMapper.selectById(chat.getSenderId());
            chatInfo.put("senderName", sender != null ? sender.getNickname() : "未知");
            // 修改：返回头像URL而不是ID
            if (sender != null && sender.getAvatar() != null) {
                Costume senderCostume = costumeMapper.selectById(sender.getAvatar());
                chatInfo.put("senderAvatar", senderCostume != null ? senderCostume.getImageUrl() : null);
            } else {
                chatInfo.put("senderAvatar", null);
            }

            result.add(chatInfo);
        }

        return result;
    }

    /**
     * 发送聊天消息
     */
    public void sendChatMessage(Long teamId, Long userId, String message) {
        TeamChat chat = new TeamChat();
        chat.setTeamId(teamId);
        chat.setSenderId(userId);
        chat.setMessage(message);
        chat.setMessageType("text");
        teamChatMapper.insert(chat);

        // 获取团队所有成员 ID 并推送聊天消息
        LambdaQueryWrapper<TeamMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamMember::getTeamId, teamId);
        List<TeamMember> members = teamMemberMapper.selectList(wrapper);

        User sender = userMapper.selectById(userId);
        // 构造更完整的 JSON 消息
        String chatJson = "{\"type\":\"chat\",\"teamId\":" + teamId +
                ",\"senderId\":" + userId +
                ",\"senderName\":\"" + sender.getNickname() +
                "\",\"message\":\"" + message +
                "\",\"messageType\":\"text\"}";

        for (TeamMember member : members) {
            if (!member.getUserId().equals(userId)) {
                WebSocketServer.sendMessageToUser(member.getUserId().toString(), chatJson);
            }
        }
    }


    /**
     * 队长选择挑战难度（进入待确认状态）
     */
    @Transactional
    public void selectChallengeLevel(Long teamId, Long captainId, String level) {
        Team team = teamMapper.selectById(teamId);
        if (team == null || !team.getCreatorId().equals(captainId)) {
            throw new RuntimeException("只有队长可以发起挑战");
        }

        // 检查人数
        LambdaQueryWrapper<TeamMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamMember::getTeamId, teamId);
        long memberCount = teamMemberMapper.selectCount(wrapper);
        if (memberCount < 3) throw new RuntimeException("至少需要 3 人才能开始挑战");

        // 设置为待确认状态，记录待选难度
        team.setStatus("confirming");
        team.setLevel(level); // 这里先存进去，但在前端显示时，如果 status 是 confirming 则显示"待确认"
        team.setChallengeStartTime(LocalDateTime.now());
        team.setChallengeEndTime(LocalDateTime.now().plusWeeks(1));
        teamMapper.updateById(team);

        // 发送系统消息
        TeamChat systemChat = new TeamChat();
        systemChat.setTeamId(teamId);
        systemChat.setSenderId(captainId);
        systemChat.setMessage("队长发起了 " + level.toUpperCase() + " 难度挑战，请确认是否接受");
        systemChat.setMessageType("system");
        teamChatMapper.insert(systemChat);

        // 给所有队员发送通知
        LambdaQueryWrapper<TeamMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(TeamMember::getTeamId, teamId)
                .ne(TeamMember::getUserId, captainId);
        List<TeamMember> members = teamMemberMapper.selectList(memberWrapper);

        for (TeamMember member : members) {
            Notification notification = new Notification();
            notification.setUserId(member.getUserId());
            notification.setType("challenge_start");
            notification.setTitle("新挑战开始");
            notification.setContent("队长发起了 " + level.toUpperCase() + " 难度挑战，请前往队伍聊天室确认");
            notification.setIsRead(0);
            notification.setRelatedId(teamId);
            notificationMapper.insert(notification);
        }
    }

    /**
     * 确认挑战（进入进行中状态）
     */
    @Transactional
    public void confirmChallenge(Long teamId, Long userId) {
        Team team = teamMapper.selectById(teamId);
        if (team == null) throw new RuntimeException("队伍不存在");

        if (!"confirming".equals(team.getStatus())) {
            throw new RuntimeException("当前没有待确认的挑战");
        }

        // 检查是否已接受
        LambdaQueryWrapper<TeamChat> chatCheck = new LambdaQueryWrapper<>();
        chatCheck.eq(TeamChat::getTeamId, teamId)
                .eq(TeamChat::getSenderId, userId)
                .like(TeamChat::getMessage, "接受");
        if (teamChatMapper.selectCount(chatCheck) > 0) {
            throw new RuntimeException("你已经接受过本次挑战");
        }

        // 发送接受消息
        TeamChat chat = new TeamChat();
        chat.setTeamId(teamId);
        chat.setSenderId(userId);
        chat.setMessage("我接受 " + team.getLevel().toUpperCase() + " 难度挑战");
        chat.setMessageType("system");
        teamChatMapper.insert(chat);

        // 【新增】实时推送接受消息给全员
        LambdaQueryWrapper<TeamMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamMember::getTeamId, teamId);
        List<TeamMember> allMembers = teamMemberMapper.selectList(wrapper);

        User sender = userMapper.selectById(userId);
        String acceptJson = "{\"type\":\"chat\",\"teamId\":" + teamId +
                ",\"senderId\":" + userId +
                ",\"senderName\":\"" + sender.getNickname() +
                "\",\"message\":\"我接受 " + team.getLevel().toUpperCase() + " 难度挑战\"," +
                "\"messageType\":\"system\"}";

        for (TeamMember member : allMembers) {
            WebSocketServer.sendMessageToUser(member.getUserId().toString(), acceptJson);
        }

        // 检查是否全员已确认（排除队长）
        LambdaQueryWrapper<TeamMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(TeamMember::getTeamId, teamId)
                .ne(TeamMember::getUserId, team.getCreatorId()); // 排除队长
        List<TeamMember> members = teamMemberMapper.selectList(memberWrapper);

        long confirmedCount = 0;
        for (TeamMember m : members) {
            LambdaQueryWrapper<TeamChat> cWrapper = new LambdaQueryWrapper<>();
            cWrapper.eq(TeamChat::getTeamId, teamId)
                    .eq(TeamChat::getSenderId, m.getUserId())
                    .like(TeamChat::getMessage, "接受");
            if (teamChatMapper.selectCount(cWrapper) > 0) confirmedCount++;
        }

        // 如果所有队员（不含队长）都确认，正式生效
        if (confirmedCount == members.size()) {
            team.setStatus("challenging");
            teamMapper.updateById(team);

            TeamChat finishChat = new TeamChat();
            finishChat.setTeamId(teamId);
            finishChat.setSenderId(team.getCreatorId());
            finishChat.setMessage("全员已确认，" + team.getLevel().toUpperCase() + " 挑战正式开始！");
            finishChat.setMessageType("system");
            teamChatMapper.insert(finishChat);
        }
    }

    /**
     * 队员互相提醒完成任务
     */
    public void remindMember(Long teamId, Long senderId, Long memberId) {
        // 禁止提醒自己
        if (senderId.equals(memberId)) throw new RuntimeException("不能提醒自己");

        // 验证发送者是否是队伍成员
        LambdaQueryWrapper<TeamMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamMember::getTeamId, teamId)
                .eq(TeamMember::getUserId, senderId);
        if (teamMemberMapper.selectCount(wrapper) == 0) {
            throw new RuntimeException("你不是该队伍成员，无法提醒");
        }

        // 发送通知给被提醒的队员
        User sender = userMapper.selectById(senderId);
        Notification notification = new Notification();
        notification.setUserId(memberId);
        notification.setType("task_remind");
        notification.setTitle("任务提醒");
        notification.setContent("来自 " + sender.getNickname() + " 的完成任务提醒");
        notification.setIsRead(0);
        notification.setRelatedId(teamId);
        notificationMapper.insert(notification);
    }

        /**
         * 获取我的队伍信息
         */
    public Map<String, Object> getMyTeam(Long userId) {
        User user = userMapper.selectById(userId);
        if (user.getTeamId() == null) {
            return null;
        }

        return getTeamDetail(user.getTeamId());
    }

    /**
     * 获取用户的通知列表
     */
    public List<Map<String, Object>> getNotifications(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
                .orderByDesc(Notification::getCreateTime);
        List<Notification> notifications = notificationMapper.selectList(wrapper);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Notification notification : notifications) {
            Map<String, Object> notifInfo = new HashMap<>();
            notifInfo.put("id", notification.getId());
            notifInfo.put("type", notification.getType());
            notifInfo.put("title", notification.getTitle());
            notifInfo.put("content", notification.getContent());
            notifInfo.put("isRead", notification.getIsRead());
            notifInfo.put("relatedId", notification.getRelatedId());
            notifInfo.put("createTime", notification.getCreateTime());
            notifInfo.put("actionTaken", notification.getIsRead() == 2);

            // 根据通知类型添加头像
            if ("team_apply".equals(notification.getType()) && notification.getRelatedId() != null) {
                TeamApplication application = teamApplicationMapper.selectById(notification.getRelatedId());
                if (application != null) {
                    User applicant = userMapper.selectById(application.getApplicantId());
                    if (applicant != null && applicant.getAvatar() != null) {
                        Costume applicantCostume = costumeMapper.selectById(applicant.getAvatar());
                        notifInfo.put("applicantAvatar", applicantCostume != null ? applicantCostume.getImageUrl() : null);
                    } else {
                        notifInfo.put("applicantAvatar", null);
                    }
                }
            } else if ("task_remind".equals(notification.getType())) {
                Team team = teamMapper.selectById(notification.getRelatedId());
                if (team != null) {
                    User sender = userMapper.selectById(team.getCreatorId()); // 这里应该查发送者
                    if (sender != null && sender.getAvatar() != null) {
                        Costume senderCostume = costumeMapper.selectById(sender.getAvatar());
                        notifInfo.put("applicantAvatar", senderCostume != null ? senderCostume.getImageUrl() : null);
                    } else {
                        notifInfo.put("applicantAvatar", null);
                    }
                }
            } else if ("challenge_start".equals(notification.getType())) {
                // 【新增】处理挑战开始通知的头像（显示队长头像）
                Team team = teamMapper.selectById(notification.getRelatedId());
                if (team != null) {
                    User captain = userMapper.selectById(team.getCreatorId());
                    if (captain != null && captain.getAvatar() != null) {
                        Costume captainCostume = costumeMapper.selectById(captain.getAvatar());
                        notifInfo.put("applicantAvatar", captainCostume != null ? captainCostume.getImageUrl() : null);
                    } else {
                        notifInfo.put("applicantAvatar", null);
                    }
                }
            } else {
                notifInfo.put("applicantAvatar", null);
            }

            result.add(notifInfo);
        }
        return result;
    }

    /**
     * 标记通知为已读
     */
    public void markNotificationRead(Long notificationId, Long userId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification != null && notification.getUserId().equals(userId)) {
            notification.setIsRead(1);
            notificationMapper.updateById(notification);
        }
    }

    /**
     * 获取未读通知数量
     */
    public Long getUnreadNotificationCount(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0);
        return notificationMapper.selectCount(wrapper);
    }

    /**
     * 获取待处理的申请列表（队长视角）
     */
    public List<Map<String, Object>> getPendingApplications(Long captainId) {
        // 获取队长的队伍
        LambdaQueryWrapper<Team> teamWrapper = new LambdaQueryWrapper<>();
        teamWrapper.eq(Team::getCreatorId, captainId);
        Team team = teamMapper.selectOne(teamWrapper);

        if (team == null) {
            return new ArrayList<>();
        }

        // 获取待处理的申请
        LambdaQueryWrapper<TeamApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamApplication::getTeamId, team.getTeamId())
                .eq(TeamApplication::getStatus, "pending");
        List<TeamApplication> applications = teamApplicationMapper.selectList(wrapper);

        List<Map<String, Object>> result = new ArrayList<>();
        for (TeamApplication app : applications) {
            Map<String, Object> appInfo = new HashMap<>();
            appInfo.put("applicationId", app.getId());
            appInfo.put("applicantId", app.getApplicantId());
            appInfo.put("message", app.getMessage());
            appInfo.put("applyTime", app.getApplyTime());

            User applicant = userMapper.selectById(app.getApplicantId());
            appInfo.put("applicantName", applicant != null ? applicant.getNickname() : "未知");
            // 修改：返回头像URL而不是ID
            if (applicant != null && applicant.getAvatar() != null) {
                Costume applicantCostume = costumeMapper.selectById(applicant.getAvatar());
                appInfo.put("applicantAvatar", applicantCostume != null ? applicantCostume.getImageUrl() : null);
            } else {
                appInfo.put("applicantAvatar", null);
            }

            result.add(appInfo);
        }
        return result;
    }
}

