package com.ja.easybookbackend.mapper;

import com.ja.easybookbackend.pojo.Supplier;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SupplierMapper {
    @Select("<script>"
            + "SELECT * FROM suppliers"
            + " <if test=\"keyword != null and keyword != ''\">"
            + " WHERE supplier_name LIKE CONCAT('%', #{keyword}, '%')"
            + "    OR contact_person LIKE CONCAT('%', #{keyword}, '%')"
            + "    OR contact_phone LIKE CONCAT('%', #{keyword}, '%')"
            + " </if>"
            + " ORDER BY create_time DESC"
            + "</script>")
    List<Supplier> list(@Param("keyword") String keyword);

    @Select("SELECT * FROM suppliers WHERE supplier_id = #{supplierId}")
    Supplier findById(@Param("supplierId") String supplierId);

    @Insert("INSERT INTO suppliers(supplier_id, supplier_name, contact_person, contact_phone, email, address, bank_account, tax_number, cooperation_status, rating, create_time, update_time) "
            + "VALUES(#{supplierId}, #{supplierName}, #{contactPerson}, #{contactPhone}, #{email}, #{address}, #{bankAccount}, #{taxNumber}, #{cooperationStatus}, #{rating}, #{createTime}, #{updateTime})")
    void insert(Supplier supplier);

    @Update("<script>"
            + "UPDATE suppliers"
            + " <set>"
            + " supplier_name = #{supplierName},"
            + " contact_person = #{contactPerson},"
            + " contact_phone = #{contactPhone},"
            + " email = #{email},"
            + " address = #{address},"
            + " bank_account = #{bankAccount},"
            + " tax_number = #{taxNumber},"
            + " cooperation_status = #{cooperationStatus},"
            + " rating = #{rating},"
            + " update_time = #{updateTime}"
            + " </set>"
            + " WHERE supplier_id = #{supplierId}"
            + "</script>")
    void update(Supplier supplier);

    @Delete("DELETE FROM suppliers WHERE supplier_id = #{supplierId}")
    void delete(@Param("supplierId") String supplierId);
}
