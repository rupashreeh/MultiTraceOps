package com.observability.platform;

import java.util.Random;

public class RN {

    public static void main(String args[]){
        Random r = new Random();
        Random s = new Random();
         for(int i =0; i< 2000; i++) {
             int x =r.nextInt(500);
               if (x == 100) {
                   //System.out.println(x);
                   if (s.nextInt(2) == 0) {
                    System.out.println("DB slowdown");
                } else {
                    System.out.println("Service slowdown");
                }
            }
        }
    }
}
