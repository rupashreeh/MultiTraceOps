package com.sample.microservice.service;

import java.util.List;

import javax.transaction.Transactional;

import com.sample.microservice.dao.WalletRepository;
import com.sample.microservice.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Transactional
public interface WalletService {

    public List<Wallet> listAll();

    public void updateBalance(@Param("balance") float balance, @Param("custId") int custId);

    public void save(Wallet wallet) ;

    public Wallet get(Integer id);

    public void delete(Integer id);
}