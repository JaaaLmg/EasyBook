package com.ja.easybookbackend.mapper;

import com.ja.easybookbackend.pojo.Author;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;

import java.util.List;

@Mapper
public interface AuthorMapper {
    @Select("SELECT a.* FROM authors a JOIN book_authors ba ON a.author_id = ba.author_id WHERE ba.isbn = #{isbn} ORDER BY ba.author_order ASC")
    List<Author> findAuthorsByIsbn(@Param("isbn") String isbn);

    @Select("SELECT * FROM authors WHERE author_name = #{authorName} LIMIT 1")
    Author findByName(@Param("authorName") String authorName);

    @Select("SELECT * FROM authors WHERE author_id = #{authorId} LIMIT 1")
    Author findById(@Param("authorId") String authorId);

    @Insert("INSERT INTO authors(author_id, author_name, nationality, birth_date, biography, create_time) VALUES(#{authorId}, #{authorName}, #{nationality}, #{birthDate}, #{biography}, #{createTime})")
    void insert(Author author);
}
