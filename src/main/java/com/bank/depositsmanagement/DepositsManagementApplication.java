package com.bank.depositsmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DepositsManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(DepositsManagementApplication.class, args);
	}

//	@Bean
//	public CommandLineRunner loadData(UserRepository userRepository, EmployeeRepository employeeRepository) {
//
//		return args -> {
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
//							.gender(true)
//							.birthday(LocalDate.now())
//							.IDCard("012345678910")
//							.phone("0123456789")
//							.email("admin@bank.com")
//							.address("Unknown")
//							.status(EmployeeStatus.WORKING)
//							.user(admin)
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
//							.gender(false)
//							.birthday(LocalDate.now())
//							.IDCard("123456789100")
//							.phone("1234567890")
//							.email("employee@bank.com")
//							.address("Unknown")
//							.status(EmployeeStatus.WORKING)
//							.user(user1)
//							.build());
//		};
//	}

}
