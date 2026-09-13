package com.example.habittt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.habittt.entity.Task;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {
}
