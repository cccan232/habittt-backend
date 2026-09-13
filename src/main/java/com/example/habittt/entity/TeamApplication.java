package com.example.habittt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("team_application")
public class TeamApplication {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long teamId;

    private Long applicantId;

    private String message;

    private String status;

    private LocalDateTime applyTime;

    private LocalDateTime handleTime;
}
