package com.pods.spring.restaurant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

public class RestaurantAcceptOrderTest extends RestaurantApplicationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testAcceptOrder() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Restaurant res = new Restaurant(101, 2,5);
        String djson = objectMapper.writeValueAsString(res);
        ResultActions result = mockMvc.perform(post("http://localhost:8080/acceptOrder").
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
    public void testAcceptOrderGone() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Restaurant res = new Restaurant(111, 2,5);
        String djson = objectMapper.writeValueAsString(res);
        ResultActions result = mockMvc.perform(post("http://localhost:8080/acceptOrder").
                contentType(MediaType.APPLICATION_JSON).
                content(djson)).
                andExpect(status().isGone());
        if(result.andReturn().getResponse().getStatus() == 410){
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
