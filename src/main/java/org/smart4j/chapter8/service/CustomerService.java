package org.smart4j.chapter8.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.chapter8.annotation.Service;
import org.smart4j.chapter8.bean.FileParam;
import org.smart4j.chapter8.helper.DatabaseHelper;
import org.smart4j.chapter8.helper.UploadHelper;
import org.smart4j.chapter8.model.Customer;

import java.util.List;
import java.util.Map;

@Service
public class CustomerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);

    /**
     * 获取客户列表
     */
    public List<Customer> getCustomerList(String keyword){
        String sql = "select * from customer";
        return DatabaseHelper.queryEntityList(Customer.class,sql,null);
    }

    /**
     * 获取客户
     */
    public Customer getCustomer(long id){
        return DatabaseHelper.getEntity(Customer.class,id);
    }

    /**
     * 创建客户
     */
    public boolean createCustomer(Map<String,Object> fieldMap, FileParam fileParam){
        boolean result = DatabaseHelper.insertEntity(Customer.class,fieldMap);
        if(result){
            UploadHelper.uploadFile("/tmp/upload",fileParam);
        }
        return result;
    }

    /**
     * 更新客户
     */
    public boolean updateCustomer(long id ,Map<String,Object> fieldMap){
        return DatabaseHelper.updateEntity(Customer.class,id,fieldMap);
    }

    /**
     * 删除客户
     */
    public boolean deleteCustomer(long id){
        return DatabaseHelper.deleteEntity(Customer.class,id);
    }

}
