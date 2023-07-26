package com.pods.spring.delivery.model;

public class PartOrder {
    private int orderId;
    private String status;
    private int agentId;

    public PartOrder(){

    }

    public PartOrder(int orderId, String status, int agentId){
        this.orderId = orderId;
        this.status = status;
        this.agentId = agentId;
    }
    public int getAgentId() {
        return agentId;
    }

    public void setAgentId(int agentId) {
        this.agentId = agentId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public int getOrderId() {
        return orderId;
    }
}
