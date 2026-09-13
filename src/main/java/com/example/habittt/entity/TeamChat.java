package com.example.habittt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("team_chat")
public class TeamChat {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long teamId;

    private Long senderId;

    private String message;

    private String messageType;

    private LocalDateTime createTime;
}

