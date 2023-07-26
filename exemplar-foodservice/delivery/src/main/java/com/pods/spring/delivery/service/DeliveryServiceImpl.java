package com.pods.spring.delivery.service;

import com.pods.spring.delivery.dao.DeliveryDAO;
import com.pods.spring.delivery.model.Agent;
import com.pods.spring.delivery.model.Order;
import com.pods.spring.delivery.model.PartOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Service
public class DeliveryServiceImpl implements DeliveryService {
    
    @Autowired
    private DeliveryDAO deliveryDAO;

    @Override
    public void initialize() {
        deliveryDAO.initialize();
    }

    @Override
    public int requestOrder(Order order) {
        return deliveryDAO.requestOrder(order);
    }

    @Override
    public void agentSignIn(Integer agentId) {
        deliveryDAO.agentSignedIn(agentId);
    }

    @Override
    public void agentSignOut(Integer agentId) {
        deliveryDAO.agentSignedOut(agentId);
    }

    @Override
    public void orderDelivered(Integer orderId) {
        deliveryDAO.orderDelivered(orderId);
    }

    @Override
    public PartOrder getOrder(Integer orderId) {
       return deliveryDAO.getOrder(orderId);
    }

    @Override
    public Agent getAgent(Integer agentId) {
        return deliveryDAO.getAgent(agentId);
    }

    @Override
    public void reinitialize() throws IOException {
      deliveryDAO.initialize();
    }

}
