package com.pods.spring.delivery.dao;

import com.pods.spring.delivery.model.*;
import com.pods.spring.delivery.utils.DeliveryAgentStatus;
import com.pods.spring.delivery.utils.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.*;

@Component
public class DeliveryDAO {

    @Autowired
    AgentAggregate agentAggregate;//= new AgentAggregate();

    @Autowired
    OrderAggregate orderAggregate ;//= new OrderAggregate();

    @Autowired
    RestaurantPriceAggregate restaurantPriceAggregate;

    @Autowired
    CustomerAggregate customerAggregate;

    @Autowired
    private RestTemplate restTemplate;

    //private static final String WALLET_URL = "http://localhost:8082";
    private static final String WALLET_URL = "http://host.docker.internal:8082";

    //private static final String RESTAURANT_URL = "http://localhost:8080";

    private static final String RESTAURANT_URL = "http://host.docker.internal:8080";


    public int requestOrder(Order order){
        Integer price = restaurantPriceAggregate.getItemPrice(order.getRestId(), order.getItemId());
        Integer totalAmount = price * order.getQty();
        order.setOrderId(Order.getNewOrderId());
        final String walletUrl = WALLET_URL+"/deductBalance";
        final String restoreBalanceUrl = WALLET_URL+"/addBalance";
        final String getBalanceUrl = WALLET_URL+"/balance/"+order.getCustId();
        final String restaurantUrl = RESTAURANT_URL+"/acceptOrder";

        URI walletUri = null;
        URI restaurantUri = null;
        URI restoreBalanceUri = null;
        URI getBalanceUri = null;
        try {
            walletUri = new URI(walletUrl);
            restaurantUri = new URI(restaurantUrl);
            restoreBalanceUri = new URI(restoreBalanceUrl);
            getBalanceUri = new URI(getBalanceUrl);
            System.out.println(walletUri);
            System.out.println(restaurantUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Fetch the wallet balance
        try {
            Wallet wallet = new Wallet(order.getCustId());
            ResponseEntity<Wallet> balance = restTemplate.getForEntity(getBalanceUri, Wallet.class);
            int initialBalance = balance.getBody().getAmount();

            // Perform deduction
            wallet = new Wallet(order.getCustId(), totalAmount);
            Restaurant restaurant = new Restaurant(order.getRestId(), order.getItemId(), order.getQty());
            ResponseEntity<String> resultWallet = restTemplate.postForEntity(walletUri, wallet, String.class);

            // get updated balance
            wallet = new Wallet(order.getCustId());
            ResponseEntity<Wallet> newBalance = restTemplate.getForEntity(getBalanceUri, Wallet.class);
            int balanceAfterDeduction = newBalance.getBody().getAmount();
            try {
                ResponseEntity<String> resultRestaurant = restTemplate.postForEntity(restaurantUri, restaurant, String.class);
            }catch(HttpClientErrorException httpClientErrorException){
                int restoreBalance = initialBalance - balanceAfterDeduction;
                wallet = new Wallet(order.getCustId(), restoreBalance);
                ResponseEntity<String> result = restTemplate.postForEntity(restoreBalanceUri, wallet, String.class);
                return -1;
            }
            Order newOrder = new Order(order.getOrderId(),order.getCustId(), order.getRestId(), -1, order.getItemId(), order.getQty(),
                        OrderStatus.UNASSIGNED);
                //Check if agent is available right now
            for (Map.Entry<Integer, Agent> entry : agentAggregate.getAgentaggregate().entrySet()) {
                    if (entry.getValue().getStatus().equalsIgnoreCase(DeliveryAgentStatus.AVAILABLE.name())) {
                        newOrder.setAgentId(entry.getKey());
                        newOrder.setOrderStatus(OrderStatus.ASSIGNED);
                        agentAggregate.setAgentUnavailable(entry.getKey());
                        break;
                    }
                }
                orderAggregate.setOrderById(newOrder.getOrderId(), newOrder);
                return newOrder.getOrderId();
        }catch(HttpClientErrorException exception){
            return -1;
        }catch(NullPointerException nExp){
            System.out.println("This customer is not found!");
            return -1;
        }
    }

    public void agentSignedIn(Integer agentId){
        if(agentAggregate.getAgent(agentId) != null){
            if(agentAggregate.getAgent(agentId).getStatus().equalsIgnoreCase(DeliveryAgentStatus.AVAILABLE.name())
            || agentAggregate.getAgent(agentId).getStatus().equalsIgnoreCase(DeliveryAgentStatus.UNAVAILABLE.name())){
                //Do nothing
            }
            else{
                if(orderAggregate.getOrderAggregate().size() > 0){
                    for(Map.Entry<Integer, Order> orders: orderAggregate.getOrderAggregate().entrySet()){
                        Order o = orders.getValue();
                        if(o.getOrderStatus() == OrderStatus.UNASSIGNED){
                            agentAggregate.addAgent(agentId, DeliveryAgentStatus.UNAVAILABLE.name().toLowerCase());
                            o.setOrderStatus(OrderStatus.ASSIGNED);
                            o.setAgentId(agentId);
                            orderAggregate.setOrderById(o.getOrderId(), o);
                            System.out.println(agentAggregate.getAgent(agentId).getStatus());
                            System.out.println(orderAggregate.getOrderAggregate().get(o.getOrderId()).getOrderStatus());
                            break;
                        }
                    }
                }
                else{
                    agentAggregate.addAgent(new Agent(agentId, DeliveryAgentStatus.AVAILABLE.name().toLowerCase()));
                }
            }
        }

    }

    public void agentSignedOut(Integer agentId) {
        if(agentAggregate.getAgent(agentId) != null){
            if(agentAggregate.getAgent(agentId).getStatus().equalsIgnoreCase(DeliveryAgentStatus.SIGNEDOUT.name()) ||
                    agentAggregate.getAgent(agentId).getStatus().equalsIgnoreCase(DeliveryAgentStatus.UNAVAILABLE.name())){
                //Do nothing
            }
            else{
                agentAggregate.addAgent(agentId, DeliveryAgentStatus.SIGNEDOUT.name().toLowerCase());
            }
        }
    }

    public void orderDelivered(Integer orderId){
       Order order =  orderAggregate.getOrderById(orderId);
       if(order != null) {
           if (order.getOrderStatus() == OrderStatus.UNASSIGNED || order.getOrderStatus() == OrderStatus.DELIVERED) {
               return; // Do nothing and ignore
           } else {
               order.setOrderStatus(OrderStatus.DELIVERED);
               orderAggregate.setOrderById(orderId, order); // Set the order status to delivered
               Agent agent = agentAggregate.getAgent(order.getAgentId());
               agent.setStatus(DeliveryAgentStatus.AVAILABLE.name().toLowerCase());
               agentAggregate.addAgent(agent);   // Update the agent status to available

               for (Map.Entry<Integer, Order> entry : orderAggregate.getOrderAggregate().entrySet()) {
                   Order o = entry.getValue();
                   if (o.getOrderStatus() == OrderStatus.UNASSIGNED) {
                       agent = agentAggregate.getAgent(order.getAgentId());
                       agent.setStatus(DeliveryAgentStatus.UNAVAILABLE.name().toLowerCase());
                       agentAggregate.addAgent(agent);   // Update the agent status to available
                       o.setOrderStatus(OrderStatus.ASSIGNED);
                       o.setAgentId(agent.getAgentId());
                   }
               }
           }
       }
    }

    public PartOrder getOrder(Integer orderId){
        if(orderAggregate.getOrderAggregate().containsKey(orderId)) {
            Order order = orderAggregate.getOrderById(orderId);
            return new PartOrder(order.getOrderId(), order.getOrderStatus().name().toLowerCase(), order.getAgentId());
        }
        return null;
    }

    public Agent getAgent(Integer agentId) {
        if(agentAggregate.getAgent(agentId) != null){
            return  agentAggregate.getAgent(agentId);
        }
        else{
            return new Agent(agentId);
        }

    }
    public void initialize(){
        agentAggregate.clearAgent();
        orderAggregate.clearOrder();
        restaurantPriceAggregate.clear();
        List<String> fileData= new ArrayList<>();
        try {
            //String userDir = System.getProperty("user.home");
            String os = System.getProperty("os.name");
            //System.out.println(os);
            FileInputStream input = null;
            if(os.contains("Windows")) {
                String userDir = System.getenv("SystemDrive");
                //System.out.println(userDir);
                input = new FileInputStream(String.valueOf(Path.of(userDir+File.separator+"initialData.txt")));

            }
            else{
                input = new FileInputStream(String.valueOf(Path.of(File.separator+"initialData.txt")));

            }
            BufferedReader br = new BufferedReader(new InputStreamReader(input));

            // Declaring a string variable
            String st;
            // Condition holds true till
            // there is character in a string
            while ((st = br.readLine()) != null)
                fileData.add(st);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int count = 0 ;
        int n = fileData.size();
        int initialBalance = Integer.parseInt(fileData.get(n-1));
        while(!fileData.get(count).equalsIgnoreCase("****")){
            String restaurant = fileData.get(count);
            System.out.println(restaurant);
            String restId[] = restaurant.split(" ");
            int restNumber = Integer.parseInt(restId[0]);
            System.out.println(restNumber);
            int noOfItems = Integer.parseInt(restId[1]);
            System.out.println(noOfItems);
            count++;
            for(int i=0 ; i< noOfItems; i++){
                String restInfo = fileData.get(count);
                String rI[] = restInfo.split(" ");
                restaurantPriceAggregate.updateRestaurantPriceDetails(restNumber,  Integer.parseInt(rI[0]), Integer.parseInt(rI[1]));
                count++;
            }
        }
        count++;
        while(!fileData.get(count).equalsIgnoreCase("****")){
            int agent = Integer.parseInt(fileData.get(count));
            System.out.println(agent);
            agentAggregate.addAgent(agent);
            count++;
        }
        count++;
        while(!fileData.get(count).equalsIgnoreCase("****")){
            int custId = Integer.parseInt(fileData.get(count));
            System.out.println(custId);
            customerAggregate.updateCustomerBalance(custId,initialBalance);
            count++;
        }
    }
}
