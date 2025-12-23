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
}
