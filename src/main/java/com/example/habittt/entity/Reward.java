package com.example.habittt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("reward")
public class Reward {
    @TableId(type = IdType.AUTO)
    private Long rewardId;
    private Long userId;
    private String description;
    private String image;
    private Integer price;
}

