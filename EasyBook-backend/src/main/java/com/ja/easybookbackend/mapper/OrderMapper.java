package com.ja.easybookbackend.mapper;

import com.ja.easybookbackend.pojo.Order;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {
    @Insert("INSERT INTO orders(order_id, order_no, customer_id, total_amount, discount_amount, actual_amount, shipping_fee, order_status, shipping_address, recipient_name, recipient_phone, payment_method, order_time, notes) "
            + "VALUES(#{orderId}, #{orderNo}, #{customerId}, #{totalAmount}, #{discountAmount}, #{actualAmount}, #{shippingFee}, #{orderStatus}, #{shippingAddress}, #{recipientName}, #{recipientPhone}, #{paymentMethod}, #{orderTime}, #{notes})")
    void insert(Order order);

    @Update("UPDATE orders SET order_status = #{orderStatus}, payment_method = #{paymentMethod}, payment_time = #{paymentTime}, shipping_time = #{shippingTime}, delivery_time = #{deliveryTime} WHERE order_id = #{orderId}")
    void updateStatus(Order order);

    @Select("<script>"
            + "SELECT * FROM orders"
            + " WHERE customer_id = #{customerId}"
            + " <if test=\"status != null and status != ''\"> AND order_status = #{status} </if>"
            + " ORDER BY order_time DESC"
            + " LIMIT #{pageSize} OFFSET #{offset}"
            + "</script>")
    List<Order> list(@Param("customerId") String customerId,
                     @Param("status") String status,
                     @Param("pageSize") int pageSize,
                     @Param("offset") int offset);

    @Select("<script>"
            + "SELECT COUNT(*) FROM orders"
            + " WHERE customer_id = #{customerId}"
            + " <if test=\"status != null and status != ''\"> AND order_status = #{status} </if>"
            + "</script>")
    Integer count(@Param("customerId") String customerId,
                  @Param("status") String status);

    @Select("SELECT * FROM orders WHERE order_id = #{orderId}")
    Order findById(@Param("orderId") String orderId);

    @Delete("DELETE FROM orders WHERE order_id = #{orderId}")
    void delete(@Param("orderId") String orderId);

    @Select("<script>"
            + "SELECT * FROM orders"
            + " <if test=\"status != null and status != ''\"> WHERE order_status = #{status} </if>"
            + " ORDER BY order_time DESC"
            + " LIMIT #{pageSize} OFFSET #{offset}"
            + "</script>")
    List<Order> listAll(@Param("status") String status,
                        @Param("pageSize") int pageSize,
                        @Param("offset") int offset);

    @Select("<script>"
            + "SELECT COUNT(*) FROM orders"
            + " <if test=\"status != null and status != ''\"> WHERE order_status = #{status} </if>"
            + "</script>")
    Integer countAll(@Param("status") String status);

    // 调用存储过程：订单支付确认（只更新状态，不扣款）
    @Select("{CALL sp_pay_order(#{orderId}, #{paymentMethod})}")
    @Options(statementType = org.apache.ibatis.mapping.StatementType.CALLABLE)
    @ResultType(java.util.Map.class)
    java.util.Map<String, Object> callPayOrder(
        @Param("orderId") String orderId,
        @Param("paymentMethod") String paymentMethod
    );

    // 调用存储过程：处理发货（发货时扣款、扣库存、释放预留）
    @Select("{CALL sp_process_delivery(#{orderId}, #{deliveryCompany}, #{trackingNo})}")
    @Options(statementType = org.apache.ibatis.mapping.StatementType.CALLABLE)
    @ResultType(java.util.Map.class)
    java.util.Map<String, Object> callProcessDelivery(
        @Param("orderId") String orderId,
        @Param("deliveryCompany") String deliveryCompany,
        @Param("trackingNo") String trackingNo
    );
}
