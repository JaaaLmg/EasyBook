package com.ja.easybookbackend.mapper;

import com.ja.easybookbackend.pojo.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {
    @Select("SELECT * FROM categories ORDER BY display_order ASC, level ASC, category_name ASC")
    List<Category> listAll();
}

