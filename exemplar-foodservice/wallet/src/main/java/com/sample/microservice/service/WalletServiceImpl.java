package com.sample.microservice.service;

import com.sample.microservice.dao.WalletRepository;
import com.sample.microservice.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WalletServiceImpl implements WalletService{
    @Autowired
    private WalletRepository repo;

    public void save(Wallet wallet) {
        repo.save(wallet);
    }
    public List<Wallet> listAll() {
        return repo.findAll();
    }

    public void updateBalance(float balance, int custId){
        Wallet w = get(custId);
        w.setBalance(balance);
        save(w);
    }

    public Wallet get(Integer id) {
        return repo.findById(id).get();
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }

}
