package com.ja.easybookbackend.mapper;

import com.ja.easybookbackend.pojo.Keyword;
import org.apache.ibatis.annotations.*;

@Mapper
public interface BookRelationMapper {
    @Insert("INSERT INTO book_authors(isbn, author_id, author_order, author_type) VALUES(#{isbn}, #{authorId}, #{authorOrder}, 'author')")
    void insertBookAuthor(@Param("isbn") String isbn, @Param("authorId") String authorId, @Param("authorOrder") Integer authorOrder);

    @Delete("DELETE FROM book_authors WHERE isbn = #{isbn}")
    void deleteBookAuthors(@Param("isbn") String isbn);

    @Delete("DELETE FROM book_categories WHERE isbn = #{isbn}")
    void deleteBookCategories(@Param("isbn") String isbn);

    @Insert("INSERT INTO book_categories(isbn, category_id) VALUES(#{isbn}, #{categoryId})")
    void insertBookCategory(@Param("isbn") String isbn, @Param("categoryId") String categoryId);

    @Select("SELECT * FROM keywords WHERE keyword = #{keyword} LIMIT 1")
    Keyword findKeywordByText(@Param("keyword") String keyword);

    @Insert("INSERT INTO keywords(keyword_id, keyword, search_count, create_time) VALUES(#{keywordId}, #{keyword}, #{searchCount}, #{createTime})")
    void insertKeyword(Keyword keyword);

    @Insert("INSERT INTO book_keywords(isbn, keyword_id, relevance) VALUES(#{isbn}, #{keywordId}, #{relevance})")
    void insertBookKeyword(@Param("isbn") String isbn, @Param("keywordId") String keywordId, @Param("relevance") Double relevance);
}
