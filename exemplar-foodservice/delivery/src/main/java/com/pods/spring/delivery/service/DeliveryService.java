package com.pods.spring.delivery.service;

import com.pods.spring.delivery.model.Agent;
import com.pods.spring.delivery.model.Order;
import com.pods.spring.delivery.model.PartOrder;

import java.io.IOException;

public interface DeliveryService {
    public void initialize();

    public int requestOrder(Order order);

    public void agentSignIn(Integer agentId);

    public void agentSignOut(Integer agentId);

    public void orderDelivered(Integer orderId);

    public PartOrder getOrder(Integer orderId);

    public Agent getAgent(Integer agentId);

    public void reinitialize() throws IOException;
}
