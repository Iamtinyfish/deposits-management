package com.bank.depositsmanagement.config;

import com.bank.depositsmanagement.dao.EmployeeRepository;
import com.bank.depositsmanagement.dao.InterestRateReferenceRepository;
import com.bank.depositsmanagement.dao.AccountRepository;
import com.bank.depositsmanagement.entity.*;
import com.bank.depositsmanagement.utils.EncryptedPasswordUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("singleton")
public class InitData {
    private final AccountRepository accountRepository;
    private final EmployeeRepository employeeRepository;
    private final InterestRateReferenceRepository interestRateReferenceRepository;

    public InitData(AccountRepository accountRepository, EmployeeRepository employeeRepository, InterestRateReferenceRepository interestRateReferenceRepository) {
        this.accountRepository = accountRepository;
        this.employeeRepository = employeeRepository;
        this.interestRateReferenceRepository = interestRateReferenceRepository;

        if (!accountRepository.existsById(1L)) {
            this.createUser();
            this.createInterestRateReference();
        }
    }

    public void createUser() {
        Account admin = accountRepository.save(
                Account.builder()
                        .username("admin")
                        .password(EncryptedPasswordUtils.encryptPassword("12345678"))
                        .role("ROLE_ADMIN")
                        .isActive(true)
                        .build()
        );

        employeeRepository.save(
                Employee.builder()
                        .firstName("Unknown")
                        .lastName("Unknown")
                        .gender(false)
                        .birthday(LocalDate.now())
                        .IDCard("012345678910")
                        .phone("0123456789")
                        .email("admin@bank.com")
                        .address("Unknown")
                        .status(EmployeeStatus.WORKING)
                        .account(admin)
                        .build()
        );

        Account employee1 = accountRepository.save(
                Account.builder()
                        .username("employee1")
                        .password(EncryptedPasswordUtils.encryptPassword("12345678"))
                        .role("ROLE_USER")
                        .isActive(true)
                        .build()
        );

        employeeRepository.save(
                Employee.builder()
                        .firstName("Unknown")
                        .lastName("Unknown")
                        .gender(false)
                        .birthday(LocalDate.now())
                        .IDCard("123456789100")
                        .phone("1234567890")
                        .email("employee1@bank.com")
                        .address("Unknown")
                        .status(EmployeeStatus.WORKING)
                        .account(employee1)
                        .build()
        );

        Account employee2 = accountRepository.save(
                Account.builder()
                        .username("employee2")
                        .password(EncryptedPasswordUtils.encryptPassword("12345678"))
                        .role("ROLE_USER")
                        .isActive(true)
                        .build()
        );

        employeeRepository.save(
                Employee.builder()
                        .firstName("Unknown")
                        .lastName("Unknown")
                        .gender(false)
                        .birthday(LocalDate.now())
                        .IDCard("123456789122")
                        .phone("1234567890")
                        .email("employee2@bank.com")
                        .address("Unknown")
                        .status(EmployeeStatus.WORKING)
                        .account(employee2)
                        .build()
        );
    }

    public void createInterestRateReference() {
        List<InterestRateReference> interestRateReferences = new ArrayList<>();
        interestRateReferences.add(new InterestRateReference(null, 2.3f, 0, CurrencyType.VND));
        interestRateReferences.add(new InterestRateReference(null, 2.9f, 1, CurrencyType.VND));
        interestRateReferences.add(new InterestRateReference(null, 3.6f, 3, CurrencyType.VND));
        interestRateReferences.add(new InterestRateReference(null, 4.55f, 6, CurrencyType.VND));
        interestRateReferences.add(new InterestRateReference(null, 5.7f, 12, CurrencyType.VND));
        interestRateReferences.add(new InterestRateReference(null, 5.9f, 18, CurrencyType.VND));
        interestRateReferences.add(new InterestRateReference(null, 6.4f, 24, CurrencyType.VND));
        interestRateReferences.add(new InterestRateReference(null, 6.6f, 36, CurrencyType.VND));
        interestRateReferences.add(new InterestRateReference(null, 6.6f, 48, CurrencyType.VND));
        interestRateReferences.add(new InterestRateReference(null, 6.6f, 60, CurrencyType.VND));
        interestRateReferences.add(new InterestRateReference(null, 2.0f, 0, CurrencyType.USD));
        interestRateReferences.add(new InterestRateReference(null, 2.6f, 1, CurrencyType.USD));
        interestRateReferences.add(new InterestRateReference(null, 3.3f, 3, CurrencyType.USD));
        interestRateReferences.add(new InterestRateReference(null, 4.25f, 6, CurrencyType.USD));
        interestRateReferences.add(new InterestRateReference(null, 5.4f, 12, CurrencyType.USD));
        interestRateReferences.add(new InterestRateReference(null, 5.6f, 18, CurrencyType.USD));
        interestRateReferences.add(new InterestRateReference(null, 6.1f, 24, CurrencyType.USD));
        interestRateReferences.add(new InterestRateReference(null, 6.3f, 36, CurrencyType.USD));
        interestRateReferences.add(new InterestRateReference(null, 6.3f, 48, CurrencyType.USD));
        interestRateReferences.add(new InterestRateReference(null, 6.3f, 60, CurrencyType.USD));

        interestRateReferenceRepository.saveAll(interestRateReferences);
    }
}
