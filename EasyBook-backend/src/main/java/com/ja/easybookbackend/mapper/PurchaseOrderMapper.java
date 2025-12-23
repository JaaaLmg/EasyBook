package com.ja.easybookbackend.mapper;

import com.ja.easybookbackend.pojo.PurchaseOrder;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PurchaseOrderMapper {
    @Insert("INSERT INTO purchase_orders(purchase_id, purchase_no, supplier_id, total_amount, status, order_date, expected_delivery, actual_delivery, create_time, create_by, approve_time, approve_by) "
            + "VALUES(#{purchaseId}, #{purchaseNo}, #{supplierId}, #{totalAmount}, #{status}, #{orderDate}, #{expectedDelivery}, #{actualDelivery}, #{createTime}, #{createBy}, #{approveTime}, #{approveBy})")
    void insert(PurchaseOrder order);

    @Update("UPDATE purchase_orders SET total_amount = #{totalAmount}, status = #{status}, expected_delivery = #{expectedDelivery}, actual_delivery = #{actualDelivery}, approve_time = #{approveTime}, approve_by = #{approveBy} WHERE purchase_id = #{purchaseId}")
    void update(PurchaseOrder order);

    @Select("<script>"
            + "SELECT * FROM purchase_orders"
            + " WHERE 1=1"
            + " <if test=\"supplierId != null and supplierId != ''\"> AND supplier_id = #{supplierId} </if>"
            + " <if test=\"status != null and status != ''\"> AND status = #{status} </if>"
            + " ORDER BY create_time DESC"
            + " LIMIT #{pageSize} OFFSET #{offset}"
            + "</script>")
    List<PurchaseOrder> list(@Param("supplierId") String supplierId,
                             @Param("status") String status,
                             @Param("pageSize") int pageSize,
                             @Param("offset") int offset);

    @Select("<script>"
            + "SELECT COUNT(*) FROM purchase_orders"
            + " WHERE 1=1"
            + " <if test=\"supplierId != null and supplierId != ''\"> AND supplier_id = #{supplierId} </if>"
            + " <if test=\"status != null and status != ''\"> AND status = #{status} </if>"
            + "</script>")
    Integer count(@Param("supplierId") String supplierId,
                  @Param("status") String status);

    @Select("SELECT * FROM purchase_orders WHERE purchase_id = #{purchaseId}")
    PurchaseOrder findById(@Param("purchaseId") String purchaseId);
}
