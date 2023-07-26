package com.pods.spring.restaurant.model;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * @author Rupashree Rangaiyengar
 * Stopgap class for in-memory data structure only for phase 1. It will be replaced with the database
 * access code once we move to an in-mem db.
 */
@Component
public class RestaurantAggregate {

    private Map<Integer, Map<Integer, Restaurant>> restaurantMap = new ConcurrentHashMap<>();

    public  Map<Integer, Map<Integer, Restaurant>>  getRestaurantMap() {
        if(restaurantMap == null) {
            restaurantMap = new ConcurrentHashMap<>();
        }
        return restaurantMap;
    }

    public void setRestaurantMap( Map<Integer, Map<Integer, Restaurant>> restaurantMap) {
        this.restaurantMap = restaurantMap;
    }

    public void addRestaurantToMap(Integer restId, Integer itemId, Restaurant restaurant){
        if (!restaurantMap.containsKey(restaurant.getRestId())) {
            Map<Integer, Restaurant> map = new ConcurrentHashMap<>();
            map.put(itemId, restaurant);
            restaurantMap.put(itemId, map);
        }
    }

    public void clearRestaurants(){
        restaurantMap.clear();
    }
    public Map<Integer, Restaurant> getRestuarantFromMap(Integer restId){
        return restaurantMap.get(restId);
    }

    public void updateRestaurantInMap(Integer restId, Integer itemId, Restaurant restaurant){
        Map<Integer, Restaurant> map = null;
        if(!restaurantMap.containsKey(restaurant.getRestId())){
          map = new ConcurrentHashMap<>();
        }
        else{
            map = restaurantMap.get(restaurant.getRestId());
        }
        map.put(itemId, restaurant);
        restaurantMap.put(restId, map);
    }
}
