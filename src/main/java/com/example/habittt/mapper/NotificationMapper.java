package com.example.habittt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.habittt.entity.Notification;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
}

