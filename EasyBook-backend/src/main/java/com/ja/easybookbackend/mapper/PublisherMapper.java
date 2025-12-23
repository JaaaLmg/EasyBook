package com.ja.easybookbackend.mapper;

import com.ja.easybookbackend.pojo.Publisher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PublisherMapper {
    @Select("SELECT * FROM publishers WHERE publisher_id = #{publisherId}")
    Publisher findById(@Param("publisherId") String publisherId);

    @Select("<script>"
            + "SELECT * FROM publishers"
            + " WHERE 1=1"
            + " <if test=\"keyword != null and keyword != ''\"> AND publisher_name LIKE CONCAT('%', #{keyword}, '%') </if>"
            + " ORDER BY create_time DESC"
            + " LIMIT #{pageSize} OFFSET #{offset}"
            + "</script>")
    java.util.List<Publisher> search(@Param("keyword") String keyword,
                                     @Param("pageSize") int pageSize,
                                     @Param("offset") int offset);

    @Select("<script>"
            + "SELECT COUNT(*) FROM publishers"
            + " WHERE 1=1"
            + " <if test=\"keyword != null and keyword != ''\"> AND publisher_name LIKE CONCAT('%', #{keyword}, '%') </if>"
            + "</script>")
    Integer count(@Param("keyword") String keyword);

    @Select("<script>"
            + "SELECT * FROM publishers"
            + " WHERE 1=1"
            + " <if test=\"keyword != null and keyword != ''\"> AND publisher_name LIKE CONCAT('%', #{keyword}, '%') </if>"
            + " ORDER BY publisher_name ASC"
            + "</script>")
    java.util.List<Publisher> findAll(@Param("keyword") String keyword);
}
