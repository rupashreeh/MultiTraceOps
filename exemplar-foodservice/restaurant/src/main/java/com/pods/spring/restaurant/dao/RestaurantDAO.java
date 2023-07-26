package com.pods.spring.restaurant.dao;

import com.pods.spring.restaurant.model.Restaurant;
import com.pods.spring.restaurant.model.RestaurantAggregate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
@Component
public class RestaurantDAO {

    @Autowired
    RestaurantAggregate map;

    public void initialize(){
        map.clearRestaurants();
        //File file = null;
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
                Restaurant r = new Restaurant();
                r.setRestId(restNumber);
                r.setItemId(Integer.parseInt(rI[0]));
                r.setPrice(Integer.parseInt(rI[1]));
                r.setQty(Integer.parseInt(rI[2]));
                System.out.println(r.toString());
                map.updateRestaurantInMap(restNumber, r.getItemId(), r);
                count++;
            }
        }
    }

    public int getTotalNumberOfRestaurant(){
        return map.getRestaurantMap().size();
    }

    public RestaurantAggregate getAllRestaurants()
    {
        return map;
    }

    public void addRestaurant(Restaurant restaurant) {
       map.addRestaurantToMap(restaurant.getRestId(), restaurant.getItemId(),restaurant);
    }

    public Map<Integer, Restaurant> getRestaurant(int restId) {
        return map.getRestuarantFromMap(restId);
    }

    public void updateRestaurant(Integer restId, Integer itemId, Restaurant restaurant) {
        map.updateRestaurantInMap(restaurant.getRestId(), restaurant.getItemId(),restaurant);
    }
}
