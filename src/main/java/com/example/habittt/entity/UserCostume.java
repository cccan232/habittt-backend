package com.example.habittt.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_costume")
public class UserCostume {
    @TableId
    private Long id;
    private Long userId;
    private Long costumeId;
    private LocalDateTime obtainTime;
}
