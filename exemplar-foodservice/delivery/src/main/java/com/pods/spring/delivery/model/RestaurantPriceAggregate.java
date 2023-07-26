package com.pods.spring.delivery.model;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * restaurant info is a map of maps with restaurant id as key for the first map an item id as
 * key for second map
 */
@Component
public class RestaurantPriceAggregate {
    Map<Integer, Map<Integer, Integer>> restInfo = new ConcurrentHashMap<>();

    public RestaurantPriceAggregate(){
    }

    public Map<Integer, Map<Integer, Integer>> getRestInfo() {
        return restInfo;
    }

    public void setRestInfo(Map<Integer, Map<Integer, Integer>> restInfo) {
        this.restInfo = restInfo;
    }

    public Integer getItemPrice(int restId, int itemId){
        if(restInfo.get(restId) != null){
            if(restInfo.get(restId).get(itemId) != null){
                return restInfo.get(restId).get(itemId);
            }
        }
        return 0;
    }

    public void updateRestaurantPriceDetails(int restNumber, int itemId, int price){
        if(restInfo.containsKey(restNumber)){
            Map<Integer, Integer> item = restInfo.get(restNumber);
            item.put(itemId, price);
            restInfo.put(restNumber, item);
        }
        else{
            Map<Integer, Integer> item = new HashMap<>();
            item.put(itemId, price);
            restInfo.put(restNumber, item);
        }
    }

    public void clear() {
        restInfo.clear();
    }
}
