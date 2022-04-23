package com.bank.depositsmanagement.config;

import com.bank.depositsmanagement.dao.EmployeeRepository;
import com.bank.depositsmanagement.dao.InterestRateReferenceRepository;
import com.bank.depositsmanagement.dao.UserRepository;
import com.bank.depositsmanagement.entity.*;
import com.bank.depositsmanagement.utils.EncryptedPasswordUtils;

import java.time.LocalDate;
import java.util.ArrayList;
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
//
//    public static void createInterestRateReference(InterestRateReferenceRepository interestRateReferenceRepository) {
//        List<InterestRateReference> interestRateReferences = new ArrayList<>();
//        interestRateReferenceRepository.add(new InterestRateReference(null,2.3f,0, CurrencyType.VND));
//        interestRateReferenceRepository.add(new InterestRateReference(null,2.9f,1, CurrencyType.VND));
//        interestRateReferenceRepository.add(new InterestRateReference(null,3.6f,3, CurrencyType.VND));
//        interestRateReferenceRepository.add(new InterestRateReference(null,4.55f,6, CurrencyType.VND));
//        interestRateReferenceRepository.add(new InterestRateReference(null,2.3f,0, CurrencyType.VND));
//        interestRateReferenceRepository.add(new InterestRateReference(null,2.3f,0, CurrencyType.VND));
//        interestRateReferenceRepository.add(new InterestRateReference(null,2.3f,0, CurrencyType.VND));
//        interestRateReferenceRepository.add(new InterestRateReference(null,2.3f,0, CurrencyType.VND));
//        interestRateReferenceRepository.add(new InterestRateReference(null,2.3f,0, CurrencyType.VND));
//        interestRateReferenceRepository.add(new InterestRateReference(null,2.3f,0, CurrencyType.VND));
//                
//
//
//        interestRateReferenceRepository.saveAll(interestRateReferences);
//    }
}
