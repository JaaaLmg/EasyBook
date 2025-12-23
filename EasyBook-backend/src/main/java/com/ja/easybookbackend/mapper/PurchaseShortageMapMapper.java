package com.ja.easybookbackend.mapper;

import com.ja.easybookbackend.pojo.PurchaseShortageMap;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PurchaseShortageMapMapper {
    @Insert("INSERT INTO purchase_shortage_map(purchase_id, record_id, mapped_quantity) VALUES(#{purchaseId}, #{recordId}, #{mappedQuantity})")
    void insert(PurchaseShortageMap map);

    @Select("SELECT * FROM purchase_shortage_map WHERE purchase_id = #{purchaseId}")
    List<PurchaseShortageMap> listByPurchase(@Param("purchaseId") String purchaseId);

    @Delete("DELETE FROM purchase_shortage_map WHERE purchase_id = #{purchaseId} AND record_id = #{recordId}")
    void delete(@Param("purchaseId") String purchaseId, @Param("recordId") String recordId);
}
