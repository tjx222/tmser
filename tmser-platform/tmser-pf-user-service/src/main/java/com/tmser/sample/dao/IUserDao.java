package com.tmser.sample.dao;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tmser.database.mybatis.BaseMapper;
import com.tmser.sample.po.UserPo;
import com.tmser.sensitive.SensitiveParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface IUserDao  extends BaseMapper<UserPo> {
    @Select("select * from user where deleted = #{deleted}")
    IPage<UserPo> selectPageVo(IPage<?> page, @Param("deleted") Boolean deleted);

    @Select("select * from user where name = #{name}")
    List<UserPo> findByName(@SensitiveParam @Param("name") String name);

    @Select({"<script>",
            "SELECT * FROM user t where name in ",
            "<foreach collection='names' item='item' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"
    })
    List<UserPo> findByNameIn(@SensitiveParam @Param("names") List<String> names);

    @Update("update user set name= #{name} where biz_id  = #{id}")
    int updateName(@SensitiveParam @Param("name") String name, @Param("id") String id);
}
