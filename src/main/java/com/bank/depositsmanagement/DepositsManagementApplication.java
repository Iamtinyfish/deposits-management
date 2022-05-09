package com.bank.depositsmanagement;

import com.bank.depositsmanagement.config.InitData;
import com.bank.depositsmanagement.dao.EmployeeRepository;
import com.bank.depositsmanagement.dao.InterestRateReferenceRepository;
import com.bank.depositsmanagement.dao.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DepositsManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(DepositsManagementApplication.class, args);
	}

//	@Bean
//	public CommandLineRunner loadData(UserRepository userRepository, EmployeeRepository employeeRepository, InterestRateReferenceRepository interestRateReferenceRepository) {
//
//		return args -> {
//			if (!userRepository.existsById(1L)) {
//				InitData.createUser(userRepository,employeeRepository);
//				InitData.createInterestRateReference(interestRateReferenceRepository);
//			}
//		};
//	}

}
