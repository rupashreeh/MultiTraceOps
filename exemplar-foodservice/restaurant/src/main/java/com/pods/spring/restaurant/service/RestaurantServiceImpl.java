package com.pods.spring.restaurant.service;

import com.pods.spring.restaurant.RestaurantException.RestaurantNotFoundException;
import com.pods.spring.restaurant.dao.RestaurantDAO;
import com.pods.spring.restaurant.model.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Map;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    @Autowired
    private RestaurantDAO restaurantDAO;

    @Override
    public void initialize() {
        restaurantDAO.initialize();
    }

    @Override
    public boolean acceptOrder(Restaurant restaurant) throws RestaurantNotFoundException {
        Map<Integer, Restaurant> r = restaurantDAO.getRestaurant(restaurant.getRestId());
        if( r!= null){
            Restaurant res = r.get(restaurant.getItemId());
            if(res != null && res.getQty() >= restaurant.getQty()){
                res.setQty(res.getQty()- restaurant.getQty());
                restaurantDAO.updateRestaurant(restaurant.getRestId(), restaurant.getItemId(), res);
                return true;
            }
        }
        return false;
    }

    @Override
    public void refillItem(Restaurant restaurant) {
        Map<Integer, Restaurant> r = restaurantDAO.getRestaurant(restaurant.getRestId());
        if( r!= null){
            Restaurant res = r.get(restaurant.getItemId());
            res.setQty(res.getQty() + restaurant.getQty());
            restaurantDAO.updateRestaurant(restaurant.getRestId(), restaurant.getItemId(),restaurant);
        }
        else{
            restaurantDAO.addRestaurant(restaurant);
        }
    }

    @Override
    public void reinitialize() throws IOException {
        restaurantDAO.initialize();
    }
}
