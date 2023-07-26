package com.pods.spring.delivery.model;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrderAggregate {

    private Map<Integer, Order> orderAggregate = new ConcurrentHashMap<>();

    public Map<Integer, Order> getOrderAggregate() {
        return orderAggregate;
    }

    public void setOrderAggregate(Map<Integer, Order> orderAggregate) {
        this.orderAggregate = orderAggregate;
    }

    public  Order getOrderById(Integer orderId){
        return orderAggregate.get(orderId);
    }

    public void setOrderById(Integer orderId, Order order){
        this.orderAggregate.put(orderId, order);
    }

    public void clearOrder() {
        orderAggregate.clear();
    }
}
