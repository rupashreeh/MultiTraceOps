package com.pods.spring.restaurant.service;

import com.pods.spring.restaurant.model.Restaurant;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface RestaurantService {

    public void initialize();

    public boolean acceptOrder(Restaurant restaurant);

    public void refillItem(Restaurant restaurant);

    public void reinitialize() throws IOException;
}
