package com.bank.depositsmanagement;

import com.bank.depositsmanagement.config.InitData;
import com.bank.depositsmanagement.dao.AccountRepository;
import com.bank.depositsmanagement.dao.EmployeeRepository;
import com.bank.depositsmanagement.dao.InterestRateReferenceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DepositsManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(DepositsManagementApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(AccountRepository accountRepository, EmployeeRepository employeeRepository, InterestRateReferenceRepository interestRateReferenceRepository) {

		return args -> {
			if (!accountRepository.existsById(1L)) {
				InitData.createUser(accountRepository,employeeRepository);
				InitData.createInterestRateReference(interestRateReferenceRepository);
			}
		};
	}

}
