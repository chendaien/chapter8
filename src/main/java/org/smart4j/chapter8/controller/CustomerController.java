package org.smart4j.chapter8.controller;

import org.smart4j.chapter8.annotation.Action;
import org.smart4j.chapter8.annotation.Controller;
import org.smart4j.chapter8.annotation.Inject;
import org.smart4j.chapter8.bean.Data;
import org.smart4j.chapter8.bean.FileParam;
import org.smart4j.chapter8.bean.Param;
import org.smart4j.chapter8.bean.View;
import org.smart4j.chapter8.model.Customer;
import org.smart4j.chapter8.service.CustomerService;

import java.util.List;
import java.util.Map;

@Controller
public class CustomerController {

    @Inject
    private CustomerService customerService;

    /**
     * 处理 创建客户 请求
     */
    @Action("post:/customer_create")
    public Data createSubmit(Param param){
        Map<String,Object> fieldMap = param.getFieldMap();
        FileParam fileParam = param.getFile("photo");
        boolean result = customerService.createCustomer(fieldMap,fileParam);
        return new Data(result);
    }

    /**
     * 进入 客户列表 界面
     */
    @Action("get:/customer")
    public View index(){
        List<Customer> customerList = customerService.getCustomerList("");
        return new View("customer.jsp").addModel("customerList",customerList);
    }

}
