package com.bank.depositsmanagement.dao;

import com.bank.depositsmanagement.entity.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {
    Optional<Customer> findByIDCard(String value);
    boolean existsByEmail(String email);
    boolean existsByIDCard(String IDCard);
}
