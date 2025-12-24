package com.ja.easybookbackend.mapper;

import com.ja.easybookbackend.pojo.SupplierBook;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SupplierBookMapper {
    @Select("SELECT * FROM supplier_books WHERE supplier_id = #{supplierId}")
    List<SupplierBook> listBySupplier(@Param("supplierId") String supplierId);

    @Select("SELECT * FROM supplier_books WHERE supplier_id = #{supplierId} AND isbn = #{isbn}")
    SupplierBook find(@Param("supplierId") String supplierId, @Param("isbn") String isbn);

    @Insert("INSERT INTO supplier_books(supplier_id, isbn, supply_price, min_order_quantity, delivery_days, is_available, last_update) "
            + "VALUES(#{supplierId}, #{isbn}, #{supplyPrice}, #{minOrderQuantity}, #{deliveryDays}, #{isAvailable}, #{lastUpdate})")
    void insert(SupplierBook sb);

    @Update("UPDATE supplier_books SET supply_price = #{supplyPrice}, min_order_quantity = #{minOrderQuantity}, delivery_days = #{deliveryDays}, is_available = #{isAvailable}, last_update = #{lastUpdate} "
            + "WHERE supplier_id = #{supplierId} AND isbn = #{isbn}")
    void update(SupplierBook sb);

    @Delete("DELETE FROM supplier_books WHERE supplier_id = #{supplierId} AND isbn = #{isbn}")
    void delete(@Param("supplierId") String supplierId, @Param("isbn") String isbn);

    @Delete("DELETE FROM supplier_books WHERE supplier_id = #{supplierId}")
    void deleteBySupplier(@Param("supplierId") String supplierId);
}
