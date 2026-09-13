package com.example.habittt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.habittt.entity.TeamMember;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TeamMemberMapper extends BaseMapper<TeamMember> {
}
