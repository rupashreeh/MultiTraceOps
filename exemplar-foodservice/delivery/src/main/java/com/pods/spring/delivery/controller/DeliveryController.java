package com.pods.spring.delivery.controller;

import com.pods.spring.delivery.model.Agent;
import com.pods.spring.delivery.model.OnlyOrderId;
import com.pods.spring.delivery.model.Order;
import com.pods.spring.delivery.model.PartOrder;
import com.pods.spring.delivery.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.IOException;

@RestController
@Component
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @RequestMapping("/hello")
    public String index(){
        return "Hello from Delivery Service!";
    }

    @PostConstruct
    public void init(){
        deliveryService.initialize();
    }

    @PostMapping(path= "/requestOrder", consumes = "application/json", produces = "application/json")
    public ResponseEntity<OnlyOrderId> requestOrder(@RequestBody Order order)
    {
        int orderId = deliveryService.requestOrder(order);
        if(orderId != -1) {
            OnlyOrderId o = new OnlyOrderId(orderId);
            return new ResponseEntity<>(o, HttpStatus.CREATED);
        }
        else {
            return new ResponseEntity<>(HttpStatus.GONE);
        }
    }

    @PostMapping(path= "/agentSignIn", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> agentSignIn(@RequestBody Agent agent)
    {
        deliveryService.agentSignIn(agent.getAgentId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(path= "/agentSignOut", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> agentSignOut(@RequestBody Agent agent)
    {
        deliveryService.agentSignOut(agent.getAgentId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path= "/orderDelivered", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> orderDelivered(@RequestBody Order order)
    {
        deliveryService.orderDelivered(order.getOrderId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/order/{order_id}")
    public ResponseEntity<PartOrder> getOrderById(@PathVariable(value = "order_id") Integer orderId){
        PartOrder order = new PartOrder();
        if(deliveryService.getOrder(orderId) == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else{
           order = deliveryService.getOrder(orderId);
           return new ResponseEntity<>(order, HttpStatus.OK);
        }
    }

    @GetMapping("/agent/{num}")
    public ResponseEntity<Agent> getAgentById(@PathVariable(value = "num") Integer agentId){
        Agent agent = deliveryService.getAgent(agentId);
        return new ResponseEntity<>(agent, HttpStatus.OK);
    }

    @PostMapping(path= "/reInitialize", produces = "application/json")
    public ResponseEntity<Object> reinitialize()
    {
        try {
            deliveryService.reinitialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
