package com.pods.spring.delivery.model;

public class Wallet {
    private int custId;
    private int amount;

    public Wallet(int custId, int amount){
        this.custId = custId;
        this.amount = amount;
    }

    public Wallet(int custId){
        this.custId = custId;
        this.amount = 0;
    }

    public void setCustId(int custId) {
        this.custId = custId;
    }

    public int getCustId() {
        return custId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
