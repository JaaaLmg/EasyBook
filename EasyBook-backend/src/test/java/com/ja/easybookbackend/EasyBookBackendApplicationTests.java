package com.ja.easybookbackend;

import com.ja.easybookbackend.mapper.CustomerMapper;
import com.ja.easybookbackend.pojo.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class EasyBookBackendApplicationTests {

    @Autowired
    private CustomerMapper customerMapper;

    @Test
    public void testList(){
        List<Customer> customerList=customerMapper.list();
        for (Customer customer:customerList){
            System.out.println(customer);
        }
    }

    @Test
    void contextLoads() {
    }

}
