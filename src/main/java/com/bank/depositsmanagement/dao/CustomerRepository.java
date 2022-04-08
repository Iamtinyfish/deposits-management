package com.bank.depositsmanagement.dao;

import com.bank.depositsmanagement.entity.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {
    Customer findByIDCard(String value);
}
