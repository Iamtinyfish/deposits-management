package com.bank.depositsmanagement.dao;

import com.bank.depositsmanagement.entity.DepositAccount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepositAccountRepository extends CrudRepository<DepositAccount, Long> {
}
