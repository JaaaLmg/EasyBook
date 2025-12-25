package com.ja.easybookbackend.mapper;

import com.ja.easybookbackend.pojo.Customer;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CustomerMapper {
    @Select("select * from customers")
    public List<Customer> list();

    @Select("SELECT * FROM customers WHERE username = #{username}")
    Customer findByUsername(String username);

    @Select("SELECT * FROM customers WHERE customer_id = #{customerId}")
    Customer findById(String customerId);

    @Insert("INSERT INTO customers(customer_id, username, password_hash, real_name, email, phone, address, account_balance, credit_level, total_purchase, registration_date, last_login, account_status) " +
            "VALUES(#{customerId}, #{userName}, #{passwordHash}, #{realName}, #{email}, #{phone}, #{address}, #{accountBalance}, #{creditLevel}, #{totalPurchase}, #{registrationDate}, #{lastLogin}, #{accountStatus})")
    void insert(Customer customer);

    @Update("UPDATE customers SET last_login = #{lastLogin} WHERE customer_id = #{customerId}")
    void updateLastLogin(Customer customer);

    @Update("UPDATE customers SET password_hash = #{passwordHash} WHERE customer_id = #{customerId}")
    void updatePassword(Customer customer);

    @Update("UPDATE customers SET real_name = #{realName}, phone = #{phone}, address = #{address} WHERE customer_id = #{customerId}")
    void updateProfile(Customer customer);

    @Update("UPDATE customers SET account_balance = #{accountBalance} WHERE customer_id = #{customerId}")
    void updateBalance(Customer customer);

    @Update("UPDATE customers SET total_purchase = #{totalPurchase} WHERE customer_id = #{customerId}")
    void updateTotalPurchase(Customer customer);

    @Update("UPDATE customers SET credit_level = #{creditLevel} WHERE customer_id = #{customerId}")
    void updateCreditLevel(Customer customer);

    @Update("UPDATE customers SET account_status = #{accountStatus} WHERE customer_id = #{customerId}")
    void updateAccountStatus(Customer customer);

    // 删除客户（软删除：将状态设为 closed）
    @Update("UPDATE customers SET account_status = 'closed' WHERE customer_id = #{customerId}")
    void softDelete(@Param("customerId") String customerId);

    // 管理员分页查询所有客户
    @Select("<script>"
            + "SELECT * FROM customers"
            + " <where>"
            + "   <if test=\"keyword != null and keyword != ''\">"
            + "     AND (username LIKE CONCAT('%', #{keyword}, '%') "
            + "          OR real_name LIKE CONCAT('%', #{keyword}, '%') "
            + "          OR email LIKE CONCAT('%', #{keyword}, '%'))"
            + "   </if>"
            + "   <if test=\"creditLevel != null\">"
            + "     AND credit_level = #{creditLevel}"
            + "   </if>"
            + "   <if test=\"accountStatus != null and accountStatus != ''\">"
            + "     AND account_status = #{accountStatus}"
            + "   </if>"
            + " </where>"
            + " ORDER BY registration_date DESC"
            + " LIMIT #{pageSize} OFFSET #{offset}"
            + "</script>")
    List<Customer> listAllWithFilters(@Param("keyword") String keyword,
                                      @Param("creditLevel") Integer creditLevel,
                                      @Param("accountStatus") String accountStatus,
                                      @Param("pageSize") int pageSize,
                                      @Param("offset") int offset);

    @Select("<script>"
            + "SELECT COUNT(*) FROM customers"
            + " <where>"
            + "   <if test=\"keyword != null and keyword != ''\">"
            + "     AND (username LIKE CONCAT('%', #{keyword}, '%') "
            + "          OR real_name LIKE CONCAT('%', #{keyword}, '%') "
            + "          OR email LIKE CONCAT('%', #{keyword}, '%'))"
            + "   </if>"
            + "   <if test=\"creditLevel != null\">"
            + "     AND credit_level = #{creditLevel}"
            + "   </if>"
            + "   <if test=\"accountStatus != null and accountStatus != ''\">"
            + "     AND account_status = #{accountStatus}"
            + "   </if>"
            + " </where>"
            + "</script>")
    Integer countWithFilters(@Param("keyword") String keyword,
                             @Param("creditLevel") Integer creditLevel,
                             @Param("accountStatus") String accountStatus);

    // 调用存储过程：账户充值
    @Select("{CALL sp_recharge(" +
            "#{customerId, mode=IN, jdbcType=VARCHAR}, " +
            "#{amount, mode=IN, jdbcType=DECIMAL}, " +
            "#{method, mode=IN, jdbcType=VARCHAR}, " +
            "#{newBalance, mode=OUT, jdbcType=DECIMAL})}")
    @Options(statementType = org.apache.ibatis.mapping.StatementType.CALLABLE)
    void callRecharge(java.util.Map<String, Object> params);
}
