package com.ja.easybookbackend.mapper;

import com.ja.easybookbackend.pojo.CreditRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CreditRuleMapper {
    @Select("SELECT level, level_name, discount_rate, overdraft_enabled, overdraft_limit, min_total_purchase, description FROM credit_rules ORDER BY level ASC")
    List<CreditRule> list();
}

