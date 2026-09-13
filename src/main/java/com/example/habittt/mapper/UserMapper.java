package com.example.habittt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.habittt.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}

