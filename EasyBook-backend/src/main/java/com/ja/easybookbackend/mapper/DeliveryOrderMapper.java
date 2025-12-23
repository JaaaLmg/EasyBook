package com.ja.easybookbackend.mapper;

import com.ja.easybookbackend.pojo.DeliveryOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

@Mapper
public interface DeliveryOrderMapper {
    @Insert("INSERT INTO delivery_orders(delivery_id, order_id, delivery_no, delivery_company, tracking_no, delivery_address, delivery_status, shipping_fee, prepare_time, ship_time) "
            + "VALUES(#{deliveryId}, #{orderId}, #{deliveryNo}, #{deliveryCompany}, #{trackingNo}, #{deliveryAddress}, #{deliveryStatus}, #{shippingFee}, #{prepareTime}, #{shipTime})")
    void insert(DeliveryOrder delivery);

    @Select("SELECT * FROM delivery_orders WHERE order_id = #{orderId}")
    DeliveryOrder findByOrder(@Param("orderId") String orderId);

    @Delete("DELETE FROM delivery_orders WHERE order_id = #{orderId}")
    void deleteByOrder(@Param("orderId") String orderId);
}
