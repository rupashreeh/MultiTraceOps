package com.pods.spring.delivery.model;

import com.pods.spring.delivery.utils.DeliveryAgentStatus;

public class Agent {
  private Integer agentId;
  private String status;

    public Agent(Integer agentId, String status){
        this.agentId = agentId;
        this.status = status;
    }

    public Agent(Integer agentId){
        this.agentId = agentId;
        this.status = DeliveryAgentStatus.SIGNEDOUT.name().toLowerCase();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    public Integer getAgentId() {
        return agentId;
    }
}
