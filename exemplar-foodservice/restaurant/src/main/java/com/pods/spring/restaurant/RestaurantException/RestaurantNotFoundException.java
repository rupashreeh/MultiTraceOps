package com.pods.spring.restaurant.RestaurantException;

public class RestaurantNotFoundException extends RuntimeException{

    RestaurantNotFoundException(Integer restId) {
        super("Could not find restaurant " + restId);
    }
}
