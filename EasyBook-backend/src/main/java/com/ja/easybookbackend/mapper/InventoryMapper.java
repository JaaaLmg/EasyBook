package com.ja.easybookbackend.mapper;

import com.ja.easybookbackend.pojo.Inventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface InventoryMapper {
    @Select("SELECT * FROM inventory WHERE isbn = #{isbn}")
    Inventory findByIsbn(@Param("isbn") String isbn);

    @Select("<script>"
            + "SELECT i.* FROM inventory i JOIN books b ON i.isbn = b.isbn"
            + " WHERE 1=1"
            + " <if test=\"keyword != null and keyword != ''\"> AND (i.isbn LIKE CONCAT('%', #{keyword}, '%') OR b.title LIKE CONCAT('%', #{keyword}, '%')) </if>"
            + "</script>")
    java.util.List<Inventory> search(@Param("keyword") String keyword);

    @Select("SELECT COUNT(*) FROM inventory")
    Integer countAll();

    @Update("UPDATE inventory SET quantity = #{quantity}, safety_stock = #{safetyStock}, reserved_quantity = #{reservedQuantity}, location_code = #{locationCode}, warehouse = #{warehouse}, last_restock = #{lastRestock}, last_check = #{lastCheck} WHERE isbn = #{isbn}")
    void update(Inventory inventory);

    @Select("SELECT * FROM inventory WHERE COALESCE(quantity,0) = 0 OR COALESCE(quantity,0) < COALESCE(safety_stock,0)")
    java.util.List<Inventory> findLowStock();

    @Insert("INSERT INTO inventory(inventory_id, isbn, quantity, safety_stock, reserved_quantity, location_code, warehouse, last_restock, last_check) "
            + "VALUES(#{inventoryId}, #{isbn}, #{quantity}, #{safetyStock}, #{reservedQuantity}, #{locationCode}, #{warehouse}, #{lastRestock}, #{lastCheck})")
    void insert(Inventory inventory);

    // 清空库存（软删除图书时使用）
    @Update("UPDATE inventory SET quantity = 0, reserved_quantity = 0, last_check = NOW() WHERE isbn = #{isbn}")
    void clearInventory(@Param("isbn") String isbn);
}
