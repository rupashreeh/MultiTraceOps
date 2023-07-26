package com.sample.microservice.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="wallet")
public class Wallet {
    private Integer custid;
    private float balance;

    public Wallet() {
    }

    public Wallet(Integer custId, float balance ) {
        this.custid = custId;
         this.balance = balance;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getCustid() {
        return custid;
    }

    public void setCustid(Integer custId) {
        this.custid = custId;
    }

    public float getBalance() {return balance;}

    public void setBalance(float balance) {
        this.balance = balance;
    }

    // other getters and setters...
}
