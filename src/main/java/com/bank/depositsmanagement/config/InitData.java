package com.bank.depositsmanagement.config;

import com.bank.depositsmanagement.dao.EmployeeRepository;
import com.bank.depositsmanagement.dao.InterestRateReferenceRepository;
import com.bank.depositsmanagement.dao.UserRepository;
import com.bank.depositsmanagement.entity.*;
import com.bank.depositsmanagement.utils.EncryptedPasswordUtils;

import java.time.LocalDate;
import java.util.List;

public class InitData {
    public static void createUser(UserRepository userRepository, EmployeeRepository employeeRepository) {
        User admin = userRepository.save(
            User.builder()
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
                    .user(admin)
                    .build()
        );

        User user1 = userRepository.save(
            User.builder()
                .username("user1")
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
                    .email("employee@bank.com")
                    .address("Unknown")
                    .status(EmployeeStatus.WORKING)
                    .user(user1)
                    .build()
        );
    }

    public static void createInterestRateReference(InterestRateReferenceRepository interestRateReferenceRepository) {
        List<InterestRateReference> interestRateReferences = List.of(
                new InterestRateReference(null,2.3f,0, CurrencyType.VND),
                new InterestRateReference(null,2.9f,1, CurrencyType.VND),
                new InterestRateReference(null,3.6f,3, CurrencyType.VND),
                new InterestRateReference(null,4.55f,6, CurrencyType.VND),
                new InterestRateReference(null,5.7f,12, CurrencyType.VND),
                new InterestRateReference(null,5.9f,18, CurrencyType.VND),
                new InterestRateReference(null,6.4f,24, CurrencyType.VND),
                new InterestRateReference(null,6.6f,36, CurrencyType.VND),
                new InterestRateReference(null,6.6f,48, CurrencyType.VND),
                new InterestRateReference(null,6.6f,60, CurrencyType.VND),
                new InterestRateReference(null,2.0f,0, CurrencyType.USD),
                new InterestRateReference(null,2.6f,1, CurrencyType.USD),
                new InterestRateReference(null,3.3f,3, CurrencyType.USD),
                new InterestRateReference(null,4.25f,6, CurrencyType.USD),
                new InterestRateReference(null,5.4f,12, CurrencyType.USD),
                new InterestRateReference(null,5.6f,18, CurrencyType.USD),
                new InterestRateReference(null,6.1f,24, CurrencyType.USD),
                new InterestRateReference(null,6.3f,36, CurrencyType.USD),
                new InterestRateReference(null,6.3f,48, CurrencyType.USD),
                new InterestRateReference(null,6.3f,60, CurrencyType.USD)
        );

        interestRateReferenceRepository.saveAll(interestRateReferences);
    }
}
