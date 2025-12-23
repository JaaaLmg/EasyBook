package com.ja.easybookbackend.mapper;

import com.ja.easybookbackend.pojo.OutOfStockRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OutOfStockRecordMapper {
    @Select("SELECT * FROM out_of_stock_records WHERE isbn = #{isbn} AND status IN ('pending','processing')")
    List<OutOfStockRecord> findActiveByIsbn(@Param("isbn") String isbn);

    @Select("<script>"
            + "SELECT * FROM out_of_stock_records"
            + " WHERE 1=1"
            + " <if test=\"sourceType != null and sourceType != ''\"> AND source_type = #{sourceType} </if>"
            + " <if test=\"status != null and status != ''\"> AND status = #{status} </if>"
            + " <if test=\"keyword != null and keyword != ''\"> AND (isbn LIKE CONCAT('%', #{keyword}, '%') OR book_title LIKE CONCAT('%', #{keyword}, '%')) </if>"
            + " ORDER BY created_time DESC"
            + " LIMIT #{pageSize} OFFSET #{offset}"
            + "</script>")
    List<OutOfStockRecord> list(@Param("sourceType") String sourceType,
                                @Param("status") String status,
                                @Param("keyword") String keyword,
                                @Param("pageSize") int pageSize,
                                @Param("offset") int offset);

    @Select("<script>"
            + "SELECT COUNT(*) FROM out_of_stock_records"
            + " WHERE 1=1"
            + " <if test=\"sourceType != null and sourceType != ''\"> AND source_type = #{sourceType} </if>"
            + " <if test=\"status != null and status != ''\"> AND status = #{status} </if>"
            + " <if test=\"keyword != null and keyword != ''\"> AND (isbn LIKE CONCAT('%', #{keyword}, '%') OR book_title LIKE CONCAT('%', #{keyword}, '%')) </if>"
            + "</script>")
    Integer count(@Param("sourceType") String sourceType,
                  @Param("status") String status,
                  @Param("keyword") String keyword);

    @Insert("INSERT INTO out_of_stock_records(record_id, isbn, book_title, publisher_name, required_quantity, customer_id, customer_email, customer_phone, source_type, priority, status, notify_status, notify_time, created_time, resolve_time, remark) "
            + "VALUES(#{recordId}, #{isbn}, #{bookTitle}, #{publisherName}, #{requiredQuantity}, #{customerId}, #{customerEmail}, #{customerPhone}, #{sourceType}, #{priority}, #{status}, #{notifyStatus}, #{notifyTime}, #{createdTime}, #{resolveTime}, #{remark})")
    void insert(OutOfStockRecord record);

    @Update("UPDATE out_of_stock_records SET required_quantity = #{requiredQuantity}, priority = #{priority}, status = #{status}, notify_status = #{notifyStatus}, notify_time = #{notifyTime}, resolve_time = #{resolveTime}, remark = #{remark} WHERE record_id = #{recordId}")
    void update(OutOfStockRecord record);

    @Delete("DELETE FROM out_of_stock_records WHERE record_id = #{recordId}")
    void delete(@Param("recordId") String recordId);

    @Select("SELECT * FROM out_of_stock_records WHERE record_id = #{recordId}")
    OutOfStockRecord findById(@Param("recordId") String recordId);
}
