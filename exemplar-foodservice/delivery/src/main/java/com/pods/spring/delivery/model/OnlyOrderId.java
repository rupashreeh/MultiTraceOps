package com.pods.spring.delivery.model;

public class OnlyOrderId {
    private int orderId;

    public OnlyOrderId(int orderId){
        this.orderId = orderId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
