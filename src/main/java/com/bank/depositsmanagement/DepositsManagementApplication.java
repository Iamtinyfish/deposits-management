package com.bank.depositsmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
