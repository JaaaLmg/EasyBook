package com.ja.easybookbackend.mapper;

import com.ja.easybookbackend.pojo.PurchaseDetail;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PurchaseDetailMapper {
    @Insert("INSERT INTO purchase_details(detail_id, purchase_id, isbn, quantity, unit_price, received_quantity, status) "
            + "VALUES(#{detailId}, #{purchaseId}, #{isbn}, #{quantity}, #{unitPrice}, #{receivedQuantity}, #{status})")
    void insert(PurchaseDetail detail);

    @Update("UPDATE purchase_details SET quantity = #{quantity}, unit_price = #{unitPrice}, received_quantity = #{receivedQuantity}, status = #{status} WHERE detail_id = #{detailId}")
    void update(PurchaseDetail detail);

    @Select("SELECT * FROM purchase_details WHERE purchase_id = #{purchaseId}")
    List<PurchaseDetail> listByPurchase(@Param("purchaseId") String purchaseId);
}
