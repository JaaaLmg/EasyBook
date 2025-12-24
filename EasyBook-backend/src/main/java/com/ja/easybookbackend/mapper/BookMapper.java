package com.ja.easybookbackend.mapper;

import com.ja.easybookbackend.pojo.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface BookMapper {
    @Select("<script>"
            + "SELECT DISTINCT b.* FROM books b"
            + " <if test=\"categoryId != null and categoryId != ''\"> JOIN book_categories bc ON bc.isbn = b.isbn </if>"
            + " <if test=\"joinAuthors\"> JOIN book_authors ba ON b.isbn = ba.isbn JOIN authors a ON a.author_id = ba.author_id </if>"
            + " WHERE 1=1"
            + " <if test=\"keyword != null and keyword != ''\"> AND (b.title LIKE CONCAT('%', #{keyword}, '%') OR b.isbn LIKE CONCAT('%', #{keyword}, '%') <if test=\"joinAuthors\"> OR a.author_name LIKE CONCAT('%', #{keyword}, '%') </if>) </if>"
            + " <if test=\"categoryId != null and categoryId != ''\"> AND bc.category_id LIKE CONCAT(#{categoryId}, '%') </if>"
            + " <if test=\"publisherId != null and publisherId != ''\"> AND b.publisher_id = #{publisherId} </if>"
            + " <if test=\"minPrice != null\"> AND b.price &gt;= #{minPrice} </if>"
            + " <if test=\"maxPrice != null\"> AND b.price &lt;= #{maxPrice} </if>"
            + " <if test=\"status != null and status != ''\"> AND b.status = #{status} </if>"
            + " <choose>"
            + "   <when test=\"sort == 'price_asc'\"> ORDER BY b.price ASC </when>"
            + "   <when test=\"sort == 'price_desc'\"> ORDER BY b.price DESC </when>"
            + "   <when test=\"sort == 'publish_date_desc'\"> ORDER BY b.publish_date DESC </when>"
            + "   <otherwise> ORDER BY b.create_time DESC </otherwise>"
            + " </choose>"
            + " LIMIT #{pageSize} OFFSET #{offset}"
            + "</script>")
    List<Book> searchBooks(@Param("keyword") String keyword,
                           @Param("categoryId") String categoryId,
                           @Param("publisherId") String publisherId,
                           @Param("minPrice") Double minPrice,
                           @Param("maxPrice") Double maxPrice,
                           @Param("status") String status,
                           @Param("sort") String sort,
                           @Param("pageSize") int pageSize,
                           @Param("offset") int offset,
                           @Param("joinAuthors") boolean joinAuthors);

    @Select("<script>"
            + "SELECT COUNT(DISTINCT b.isbn) FROM books b"
            + " <if test=\"categoryId != null and categoryId != ''\"> JOIN book_categories bc ON bc.isbn = b.isbn </if>"
            + " <if test=\"joinAuthors\"> JOIN book_authors ba ON b.isbn = ba.isbn JOIN authors a ON a.author_id = ba.author_id </if>"
            + " WHERE 1=1"
            + " <if test=\"keyword != null and keyword != ''\"> AND (b.title LIKE CONCAT('%', #{keyword}, '%') OR b.isbn LIKE CONCAT('%', #{keyword}, '%') <if test=\"joinAuthors\"> OR a.author_name LIKE CONCAT('%', #{keyword}, '%') </if>) </if>"
            + " <if test=\"categoryId != null and categoryId != ''\"> AND bc.category_id LIKE CONCAT(#{categoryId}, '%') </if>"
            + " <if test=\"publisherId != null and publisherId != ''\"> AND b.publisher_id = #{publisherId} </if>"
            + " <if test=\"minPrice != null\"> AND b.price &gt;= #{minPrice} </if>"
            + " <if test=\"maxPrice != null\"> AND b.price &lt;= #{maxPrice} </if>"
            + " <if test=\"status != null and status != ''\"> AND b.status = #{status} </if>"
            + "</script>")
    Integer countBooks(@Param("keyword") String keyword,
                       @Param("categoryId") String categoryId,
                       @Param("publisherId") String publisherId,
                       @Param("minPrice") Double minPrice,
                       @Param("maxPrice") Double maxPrice,
                       @Param("status") String status,
                       @Param("joinAuthors") boolean joinAuthors);

    @Select("SELECT * FROM books WHERE isbn = #{isbn}")
    Book findByIsbn(@Param("isbn") String isbn);

    @Select("<script>"
            + "SELECT DISTINCT b.* FROM books b"
            + " LEFT JOIN book_authors ba ON b.isbn = ba.isbn"
            + " LEFT JOIN authors a ON a.author_id = ba.author_id"
            + " WHERE 1=1"
            + " <if test=\"q != null and q != ''\"> AND (b.title LIKE CONCAT('%', #{q}, '%') OR b.isbn LIKE CONCAT('%', #{q}, '%') OR a.author_name LIKE CONCAT('%', #{q}, '%')) </if>"
            + " ORDER BY b.create_time DESC"
            + " LIMIT #{pageSize} OFFSET #{offset}"
            + "</script>")
    java.util.List<Book> fullTextSearch(@Param("q") String q,
                                       @Param("pageSize") int pageSize,
                                       @Param("offset") int offset);

    @Select("<script>"
            + "SELECT COUNT(DISTINCT b.isbn) FROM books b"
            + " LEFT JOIN book_authors ba ON b.isbn = ba.isbn"
            + " LEFT JOIN authors a ON a.author_id = ba.author_id"
            + " WHERE 1=1"
            + " <if test=\"q != null and q != ''\"> AND (b.title LIKE CONCAT('%', #{q}, '%') OR b.isbn LIKE CONCAT('%', #{q}, '%') OR a.author_name LIKE CONCAT('%', #{q}, '%')) </if>"
            + "</script>")
    Integer fullTextCount(@Param("q") String q);

    @Insert("INSERT INTO books(isbn, title, edition, publisher_id, publish_date, price, page_count, format, language, description, table_of_contents, cover_image, series_id, book_type, status, create_time, update_time) "
            + "VALUES(#{isbn}, #{title}, #{edition}, #{publisherId}, #{publishDate}, #{price}, #{pageCount}, #{format}, #{language}, #{description}, #{tableOfContents}, #{coverImage}, #{seriesId}, #{bookType}, #{status}, #{createTime}, #{updateTime})")
    void insert(Book book);

    @Update("UPDATE books SET title = #{title}, edition = #{edition}, publisher_id = #{publisherId}, publish_date = #{publishDate}, price = #{price}, page_count = #{pageCount}, format = #{format}, language = #{language}, description = #{description}, table_of_contents = #{tableOfContents}, cover_image = #{coverImage}, series_id = #{seriesId}, book_type = #{bookType}, status = #{status}, update_time = #{updateTime} WHERE isbn = #{isbn}")
    void update(Book book);

    @Update("UPDATE books SET status = #{status}, update_time = NOW() WHERE isbn = #{isbn}")
    void updateStatus(@Param("isbn") String isbn, @Param("status") String status);

    // 软删除：将图书状态设为 inactive（已下架）
    @Update("UPDATE books SET status = 'inactive', update_time = NOW() WHERE isbn = #{isbn}")
    void softDelete(@Param("isbn") String isbn);
}
