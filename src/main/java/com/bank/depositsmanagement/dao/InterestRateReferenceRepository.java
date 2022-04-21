package com.bank.depositsmanagement.dao;

import com.bank.depositsmanagement.entity.CurrencyType;
import com.bank.depositsmanagement.entity.InterestRateReference;
import org.springframework.data.repository.CrudRepository;

public interface InterestRateReferenceRepository extends CrudRepository<InterestRateReference, Long> {
    InterestRateReference findByPeriodAndCurrency(int period, CurrencyType currency);
}
