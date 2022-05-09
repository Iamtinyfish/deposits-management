package com.bank.depositsmanagement.utils;

import com.bank.depositsmanagement.dao.InterestRateReferenceRepository;
import com.bank.depositsmanagement.entity.DepositAccount;
import com.bank.depositsmanagement.entity.InterestRateReference;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

@Component
public class Interest {
    private final InterestRateReferenceRepository interestRateReferenceRepository;

    public Interest(InterestRateReferenceRepository interestRateReferenceRepository) {
        this.interestRateReferenceRepository = interestRateReferenceRepository;
    }

    public HashMap<String, Object> calculateInterest(DepositAccount depositAccount) {
        HashMap<String,Object> result = new HashMap<>();

        int period = depositAccount.getPeriod();

        BigDecimal finalBalance;

        if (period > 0) {
            int dayOfPeriod = period * TimeConstant.DAY_OF_MONTH;
            int timeDepositDays = (depositAccount.getNumberOfDay() / dayOfPeriod) * dayOfPeriod;
            result.put("timeDepositDays", timeDepositDays);
            BigDecimal balance1 = depositAccount.getOriginalAmount().add(depositAccount.getInterest()).setScale(2, RoundingMode.HALF_UP);
            result.put("balance1", balance1);

            int demandDepositDays = depositAccount.getNumberOfDay() - timeDepositDays;
            result.put("demandDepositDays", demandDepositDays);
            InterestRateReference demandInterestRateReference = this.interestRateReferenceRepository.findByPeriodAndCurrency(0,depositAccount.getCurrency()).orElse(null);
            float demandDepositInterestRate = demandInterestRateReference.getInterestRate();
            result.put("demandDepositInterestRate", demandDepositInterestRate);
            BigDecimal demandDepositInterest = balance1
                    .multiply(BigDecimal.valueOf(demandDepositDays * demandDepositInterestRate * 0.01 / 360))
                    .setScale(2, RoundingMode.HALF_UP);
            result.put("demandDepositInterest", demandDepositInterest);
            finalBalance = balance1
                    .add(demandDepositInterest)
                    .setScale(2, RoundingMode.HALF_UP);
        } else {
            finalBalance = depositAccount.getOriginalAmount()
                    .add(depositAccount.getInterest())
                    .setScale(2,RoundingMode.HALF_UP);
        }
        result.put("finalBalance", finalBalance);
        return result;
    }
}
