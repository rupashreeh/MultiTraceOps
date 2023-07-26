package com.pods.spring.delivery.model;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CustomerAggregate {
    private Map<Integer, Wallet> customerAggregate = new ConcurrentHashMap<>();

    public Map<Integer, Wallet> getCustomerAggregate() {
        return customerAggregate;
    }

    public void setCustomerAggregate(Map<Integer, Wallet> customerAggregate) {
        this.customerAggregate = customerAggregate;
    }

    public void addCustomer(Integer custId){
        customerAggregate.put(custId, new Wallet(custId));
    }

    public void updateCustomerBalance(Integer custId, Integer balance){
        customerAggregate.put(custId, new Wallet(custId, balance));
    }

    public int getCustomerBalance(Integer custId){
        if(customerAggregate.containsKey(custId)) {
            return customerAggregate.get(custId).getAmount();
        }
        return 0;
    }
}
