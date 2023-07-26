package com.pods.spring.restaurant.controller;

import com.pods.spring.restaurant.dao.RestaurantDAO;
import com.pods.spring.restaurant.model.Restaurant;
import com.pods.spring.restaurant.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;

@RestController
@Component
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @RequestMapping("/hello")
    public String index(){
        return "Hello from restuarant service!";
    }

    @PostConstruct
    public void init(){
        restaurantService.initialize();
    }


    @PostMapping(path= "/acceptOrder", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> receiveOrder(@RequestBody Restaurant restaurant)
    {
        if(restaurant.getQty() < 0){
            //Negative item .Return GONE
            return new ResponseEntity<>(HttpStatus.GONE);
        }
        else {
            if (restaurantService.acceptOrder(restaurant)) {
                return new ResponseEntity<>(HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.GONE);
            }
        }
    }

    @PostMapping(path= "/refillItem", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> refillItem(@RequestBody Restaurant restaurant)
    {
        if(restaurant.getQty() < 0){
            //Negative item .Don't do anything
        }
        else {
            restaurantService.refillItem(restaurant);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);

    }

    @PostMapping(path= "/reInitialize", produces = "application/json")
    public ResponseEntity<Object> reinitialize()
    {
        try {
            restaurantService.reinitialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.CREATED);

    }

}
