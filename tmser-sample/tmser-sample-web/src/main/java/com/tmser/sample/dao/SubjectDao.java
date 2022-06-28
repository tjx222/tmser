package com.tmser.sample.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tmser.database.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import javax.security.auth.Subject;

@Mapper
@DS("master")
public interface SubjectDao extends BaseMapper<Object>, com.baomidou.mybatisplus.core.mapper.BaseMapper {
    @Select("select * from subject where appro_status = #{status}")
    IPage<Subject> selectPageVo(IPage<?> page, @Param("status") Integer status);
}
