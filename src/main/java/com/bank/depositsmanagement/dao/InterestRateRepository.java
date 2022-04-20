package com.bank.depositsmanagement.dao;

import com.bank.depositsmanagement.entity.InterestRate;
import org.springframework.data.repository.CrudRepository;

public interface InterestRateRepository extends CrudRepository<InterestRate, Long> {
}
