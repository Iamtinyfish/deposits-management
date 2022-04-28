package com.bank.depositsmanagement.dao;

import com.bank.depositsmanagement.entity.Transaction;
import org.springframework.data.repository.CrudRepository;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
}
