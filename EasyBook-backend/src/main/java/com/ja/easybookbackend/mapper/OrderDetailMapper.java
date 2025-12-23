package com.ja.easybookbackend.mapper;

import com.ja.easybookbackend.pojo.OrderDetail;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

@Mapper
public interface OrderDetailMapper {
    @Insert("INSERT INTO order_details(detail_id, order_id, isbn, quantity, unit_price, discount_rate, subtotal) "
            + "VALUES(#{detailId}, #{orderId}, #{isbn}, #{quantity}, #{unitPrice}, #{discountRate}, #{subtotal})")
    void insert(OrderDetail detail);

    @Select("SELECT * FROM order_details WHERE order_id = #{orderId}")
    List<OrderDetail> listByOrder(@Param("orderId") String orderId);

    @Delete("DELETE FROM order_details WHERE order_id = #{orderId}")
    void deleteByOrder(@Param("orderId") String orderId);

    @Select("SELECT DISTINCT order_id FROM order_details WHERE isbn = #{isbn}")
    List<String> listOrderIdsByIsbn(@Param("isbn") String isbn);
}
