package com.pods.spring.restaurant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pods.spring.restaurant.model.Restaurant;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RefillItemTest extends RestaurantApplicationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testRefillItem() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        Restaurant res = new Restaurant(101, 2,5);
        String djson = objectMapper.writeValueAsString(res);
        ResultActions result = mockMvc.perform(post("http://localhost:8080/refillItem").
                contentType(MediaType.APPLICATION_JSON).
                content(djson)).
                andExpect(status().isCreated());
        if(result.andReturn().getResponse().getStatus() == 201){
            System.out.println("PASS");
        }
        else {
            System.out.println("FAIL");
        }
    }

    @Test
    public void testRefillItemNew() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Restaurant res = new Restaurant(100, 2,5);
        String djson = objectMapper.writeValueAsString(res);
        ResultActions result = mockMvc.perform(post("http://localhost:8080/refillItem").
                contentType(MediaType.APPLICATION_JSON).
                content(djson)).
                andExpect(status().isCreated());
        if(result.andReturn().getResponse().getStatus() == 201){
            System.out.println("PASS");
        }
        else {
            System.out.println("FAIL");
        }
    }
    @AfterEach
    public void cleanUp() throws Exception {
        mockMvc.perform(post("http://localhost:8080/reInitialize"));
    }

}
