package com.bank.depositsmanagement;

import com.bank.depositsmanagement.dao.BranchRepository;
import com.bank.depositsmanagement.dao.EmployeeRepository;
import com.bank.depositsmanagement.dao.UserRepository;
import com.bank.depositsmanagement.entity.*;
import com.bank.depositsmanagement.utils.EncryptedPasswordUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class DepositsManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(DepositsManagementApplication.class, args);
	}

//	@Bean
//	public CommandLineRunner loadData(BranchRepository branchRepository, UserRepository userRepository, EmployeeRepository employeeRepository) {
//
//		return args -> {
//			Branch branch = branchRepository.save(
//					Branch.builder()
//							.branchName("Unknown")
//							.address("Unknown")
//							.build()
//			);
//
//			User admin = userRepository.save(
//					User.builder()
//							.username("admin")
//							.password(EncryptedPasswordUtils.encryptPassword("12345678"))
//							.role("ROLE_ADMIN")
//							.isActive(true)
//							.build()
//			);
//
//			employeeRepository.save(
//					Employee.builder()
//							.firstName("Unknown")
//							.lastName("Unknown")
//							.gender(GenderType.MALE)
//							.birthday(LocalDate.now())
//							.IDCard("012345678910")
//							.phone("0123456789")
//							.email("admin@bank.com")
//							.address("Unknown")
//							.status(EmployeeStatus.WORKING)
//							.user(admin)
//							.branch(branch)
//							.build()
//			);
//
//			User user1 = userRepository.save(
//					User.builder()
//							.username("user1")
//							.password(EncryptedPasswordUtils.encryptPassword("12345678"))
//							.role("ROLE_USER")
//							.isActive(true)
//							.build()
//			);
//
//			employeeRepository.save(
//					Employee.builder()
//							.firstName("Unknown")
//							.lastName("Unknown")
//							.gender(GenderType.MALE)
//							.birthday(LocalDate.now())
//							.IDCard("123456789100")
//							.phone("1234567890")
//							.email("employee@bank.com")
//							.address("Unknown")
//							.status(EmployeeStatus.WORKING)
//							.user(user1)
//							.branch(branch)
//							.build());
//		};
//	}

}
