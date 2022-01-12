package com.tmser.sample.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tmser.sample.po.UserPo;
import com.tmser.sensitive.SensitiveParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface IUserDao  extends BaseMapper<UserPo> {
    @Select("select * from User where deleted = #{deleted}")
    IPage<UserPo> selectPageVo(IPage<?> page, @Param("deleted") Boolean deleted);

    @Select("select * from User where name = #{name}")
    List<UserPo> findByName(@SensitiveParam @Param("name") String name);
}
