package com.pods.spring.delivery.model;

import com.pods.spring.delivery.utils.DeliveryAgentStatus;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AgentAggregate {
    private Map<Integer, Agent> agentaggregate = new ConcurrentHashMap<>();

    public Map<Integer, Agent> getAgentaggregate() {
        return agentaggregate;
    }

    public void setAgentaggregate(Map<Integer, Agent> agentaggregate) {
        this.agentaggregate = agentaggregate;
    }

    public void addAgent(Agent agent){
        agentaggregate.put(agent.getAgentId(), agent);
    }

    public void addAgent(Integer agentId){
        agentaggregate.put(agentId, new Agent(agentId));

    }
    public String getAgentStatus(Integer agentId){
        return agentaggregate.get(agentId).getStatus();
    }
    public Agent getAgent(Integer agentId){
        return agentaggregate.get(agentId);
    }

    public void addAgent(Integer agentId, String status){
        agentaggregate.put(agentId, new Agent(agentId, status));
    }

    public void setAgentUnavailable(Integer agentId){
        agentaggregate.put(agentId, new Agent(agentId, DeliveryAgentStatus.UNAVAILABLE.name().toLowerCase()));
    }

    public void clearAgent() {
        agentaggregate.clear();
    }
}