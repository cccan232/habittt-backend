package com.example.habittt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("team")
public class Team {

    @TableId(type = IdType.AUTO)
    private Long teamId;

    private String teamName;

    private Long creatorId;

    private String description;

    private String level;

    private String status;

    private LocalDateTime challengeStartTime;

    private LocalDateTime challengeEndTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
