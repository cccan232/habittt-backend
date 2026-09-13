package com.example.habittt.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("costume")
public class Costume {
    @TableId
    private Long id;
    private Integer roleId;
    private String name;// 角色名称
    private String imageUrl;
    private Integer price;
    //private Boolean isFree;
    private String sort;// 风格
}