package com.pods.spring.restaurant.model;

public class Restaurant {

    private int restId;
    private int itemId;
    private int price;
    private int qty;

    public Restaurant(){

    }
    public Restaurant(int restId, int itemId, int price,  int qty){
        this.restId = restId;
        this.itemId = itemId;
        this.price = price;
        this.qty = qty;
    }
    public Restaurant(int restId, int itemId, int qty){
        this.restId = restId;
        this.itemId = itemId;
        this.price = 0;
        this.qty = qty;
    }
    public int getItemId() {
        return itemId;
    }

    public int getQty() {
        return qty;
    }

    public int getRestId() {
        return restId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void setRestId(int restId) {
        this.restId = restId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Restuarant [restId=" + restId + ", itemId=" + itemId + " , price ="+price + " , qty = "+qty+"]";
    }
}
