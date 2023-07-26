package com.pods.spring.delivery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pods.spring.delivery.model.Agent;
import com.pods.spring.delivery.utils.DeliveryAgentStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AgentSignedOutTests extends DeliveryApplicationTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testAgentSignedOut() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        Agent a = new Agent(301, DeliveryAgentStatus.AVAILABLE.name().toLowerCase());
        String djson = objectMapper.writeValueAsString(a);
        mockMvc.perform(post("http://localhost:8081/agentSignOut").
                contentType(MediaType.APPLICATION_JSON).
                content(djson)).
                andExpect(status().is2xxSuccessful());

        Agent aSigned = new Agent(301, DeliveryAgentStatus.SIGNEDOUT.name().toLowerCase());
        String djsond = objectMapper.writeValueAsString(aSigned);

        mockMvc.perform(get("http://localhost:8081/agent/301").
                contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().is2xxSuccessful()).andExpect(content().json(djsond));

        a = new Agent(301, DeliveryAgentStatus.UNAVAILABLE.name().toLowerCase());
        djson = objectMapper.writeValueAsString(a);
        mockMvc.perform(post("http://localhost:8081/agentSignOut").
                contentType(MediaType.APPLICATION_JSON).
                content(djson)).
                andExpect(status().is2xxSuccessful());

        mockMvc.perform(get("http://localhost:8081/agent/301").
                contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().is2xxSuccessful()).andExpect(content().json(djsond));

        a = new Agent(301, DeliveryAgentStatus.SIGNEDOUT.name().toLowerCase());
        djson = objectMapper.writeValueAsString(a);
        mockMvc.perform(post("http://localhost:8081/agentSignOut").
                contentType(MediaType.APPLICATION_JSON).
                content(djson)).
                andExpect(status().is2xxSuccessful());

        ResultActions result = mockMvc.perform(get("http://localhost:8081/agent/301").
                contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().is2xxSuccessful()).andExpect(content().json(djsond));

        if(result.andReturn().getResponse().getStatus() == 200){
            System.out.println("PASS");
        }
        else {
            System.out.println("FAIL");
        }
    }

    @AfterEach
    public void cleanUp() throws Exception {
        mockMvc.perform(post("http://localhost:8082/reInitialize"));
      }

}
