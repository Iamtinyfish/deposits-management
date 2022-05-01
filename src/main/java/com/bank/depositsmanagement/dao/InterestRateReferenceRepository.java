package com.bank.depositsmanagement.dao;

import com.bank.depositsmanagement.entity.CurrencyType;
import com.bank.depositsmanagement.entity.InterestRateReference;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface InterestRateReferenceRepository extends CrudRepository<InterestRateReference, Long> {
    Optional<InterestRateReference> findByPeriodAndCurrency(int period, CurrencyType currency);
}
