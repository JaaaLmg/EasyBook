package com.ja.easybookbackend.mapper;

import com.ja.easybookbackend.pojo.BookSeries;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BookSeriesMapper {
    @Select("SELECT * FROM book_series")
    List<BookSeries> listAll();

    @Select("SELECT * FROM book_series WHERE series_id = #{seriesId}")
    BookSeries findById(@Param("seriesId") String seriesId);

    @Select("<script>"
            + "SELECT * FROM book_series"
            + " <where>"
            + "   <if test=\"keyword != null and keyword != ''\">"
            + "     AND (series_name LIKE CONCAT('%', #{keyword}, '%') "
            + "          OR description LIKE CONCAT('%', #{keyword}, '%'))"
            + "   </if>"
            + "   <if test=\"publisherId != null and publisherId != ''\">"
            + "     AND publisher_id = #{publisherId}"
            + "   </if>"
            + " </where>"
            + " ORDER BY series_name"
            + "</script>")
    List<BookSeries> search(@Param("keyword") String keyword,
                           @Param("publisherId") String publisherId);

    @Insert("INSERT INTO book_series(series_id, series_name, publisher_id, total_books, description) "
            + "VALUES(#{seriesId}, #{seriesName}, #{publisherId}, #{totalBooks}, #{description})")
    void insert(BookSeries series);

    @Update("UPDATE book_series SET series_name = #{seriesName}, publisher_id = #{publisherId}, "
            + "total_books = #{totalBooks}, description = #{description} WHERE series_id = #{seriesId}")
    void update(BookSeries series);

    @Delete("DELETE FROM book_series WHERE series_id = #{seriesId}")
    void delete(@Param("seriesId") String seriesId);
}
