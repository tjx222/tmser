package com.tmser.blog.repository;

import com.tmser.blog.model.entity.Post;
import com.tmser.blog.model.entity.ShareInfo;
import com.tmser.blog.repository.base.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Log repository.
 *
 * @author johnniang
 */
@Mapper
public interface ShareInfoRepository extends BaseRepository<ShareInfo> {
    /**
     * 统计总访问次数
     * @return
     */
    @Select("select sum(total_visit) from share_info where deleted = 0")
    Long countVisit();

    @Update("update share_info set deleted = 1 where id = #{id}")
    int logicDelete(@Param("id") Integer id);

    @Update({"<script>", "update share_info set deleted = 1 where id in ",
            "<foreach item='item' index='index' collection='ids' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach></script>"})
    void logicDeleteByIds(@Param("ids") List<Integer> ids);
}
