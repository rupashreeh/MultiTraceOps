package com.sample.microservice.controller;


import com.sample.microservice.model.Wallet;
import com.sample.microservice.observability.annotation.DBLog;
import com.sample.microservice.observability.annotation.Log;
import com.sample.microservice.observability.annotation.MetricLog;
import com.sample.microservice.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

@RestController
public class WalletController {

    static int requestCount=0;
    Random random = new Random();

    @Autowired
    WalletService walletService;

    @RequestMapping("/hello")
    public String index(){
        return "Hello from sample wallet service!";
    }

    @Log
    @PostMapping(path= "/addBalance", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> addBalance(@RequestBody Wallet wallet) throws InterruptedException {
        requestCount++;
        System.out.println(requestCount+ " is the request count");
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Wallet w = walletService.get(wallet.getCustid());
        int myRandInt = random.ints(1, 5).findAny().getAsInt();
        System.out.println(myRandInt+ " is the random number");
        if(requestCount != myRandInt){
        if (w != null){
                float balance = w.getBalance() + wallet.getBalance();
                walletService.updateBalance(balance, wallet.getCustid());
                return new ResponseEntity<>(HttpStatus.CREATED);
        }
        else{
            return new ResponseEntity<>(HttpStatus.GONE);
         }
        }
        else{
            //Thread.sleep(1001);
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "LB Timeout");
        }

    }

    @Log
    @PostMapping(path= "/createCustomer", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> createCustomer(@RequestBody Wallet wallet)
    {
        requestCount++;
        walletService.save(wallet);
        return new ResponseEntity<>(HttpStatus.CREATED);

    }

    @Log
    @PostMapping(path= "/deductBalance", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> deductBalance(@RequestBody Wallet wallet)
    {
        requestCount++;
        Wallet w = walletService.get(wallet.getCustid());
        if (w != null){
            if (w.getBalance() - wallet.getBalance() >=0){
                float balance = w.getBalance() - wallet.getBalance();
                walletService.updateBalance(balance, wallet.getCustid());
                return new ResponseEntity<>(HttpStatus.CREATED);
            }
        }
        else{
            return new ResponseEntity<>(HttpStatus.GONE);
        }

        return null;
    }

    @Log
    @MetricLog
    @GetMapping("/balance/{num}")
    public ResponseEntity<Wallet> getBalance(@PathVariable(value = "num") Integer custId) throws InterruptedException {
        requestCount++;
        System.out.println(requestCount+ " is the request count");
        Wallet w;
        int myRandInt = random.ints(1, 10).findAny().getAsInt();
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        System.out.println(myRandInt+ " is the random number");
        if (requestCount != myRandInt)  {
        try {
            w = walletService.get(custId);
            return ResponseEntity.ok().body(w);
        }catch(Exception e){
            throw e;
        }
        }
        else{
            //Thread.sleep(1001);
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "LB Timeout");
        }

    }

    private void write(final String s) throws IOException {
        Files.writeString(
                Path.of(System.getProperty("user.home")+"/GTF.txt"),
                s + System.lineSeparator(),
                CREATE, APPEND
        );
    }

}