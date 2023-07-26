package com.pods.spring.delivery.model;

import com.pods.spring.delivery.utils.OrderStatus;

import java.util.concurrent.atomic.AtomicInteger;

public class Order {
    private static final AtomicInteger count = new AtomicInteger(1000);
    private int orderId;
    private int custId;
    private int restId;
    private int agentId;
    private int itemId;
    private int qty;
    private OrderStatus orderStatus;

    public Order(){

    }
    public Order(int custId, int restId, int agentId,int itemId, int qty, OrderStatus orderStatus) {
        this.orderId = getNewOrderId();
        this.custId = custId;
        this.restId = restId;
        this.agentId = agentId;
        this.itemId = itemId;
        this.qty = qty;
        this.orderStatus = orderStatus;
    }

    public Order(int orderId, int custId, int restId, int agentId,int itemId, int qty, OrderStatus orderStatus) {
        this.orderId = orderId;
        this.custId = custId;
        this.restId = restId;
        this.agentId = agentId;
        this.itemId = itemId;
        this.qty = qty;
        this.orderStatus = orderStatus;
    }

    public void setRestId(int restId) {
        this.restId = restId;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getRestId() {
        return restId;
    }

    public int getQty() {
        return qty;
    }

    public int getItemId() {
        return itemId;
    }

    public int getCustId() {
        return custId;
    }

    public int getOrderId() {
        return orderId;
    }

    public static int getNewOrderId(){
        return count.incrementAndGet();
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setCustId(int custId) {
        this.custId = custId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public static AtomicInteger getCount() {
        return count;
    }

    public void setAgentId(int agentId) {
        this.agentId = agentId;
    }

    public int getAgentId() {
        return agentId;
    }
}
