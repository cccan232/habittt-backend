package com.example.habittt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id; // user_id
    private String email;
    private String password;
    private String nickname;
    private Long avatar; // 当前穿戴装扮
    private Integer points; // 货币
    private Long teamId; // 队伍 ID
    private String status; // 账号状态
    private Integer createQuota;//自定义奖励添加允许次数
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}


