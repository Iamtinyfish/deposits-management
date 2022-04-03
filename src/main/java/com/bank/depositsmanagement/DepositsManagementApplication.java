package com.bank.depositsmanagement;

import com.bank.depositsmanagement.dao.UserRepository;
import com.bank.depositsmanagement.entity.User;
import com.bank.depositsmanagement.utils.EncrytedPasswordUtils;
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
//	public CommandLineRunner loadData(UserRepository repository) {
//		return (args) -> {
//	 		repository.save(
//					User.builder()
//							.username("admin")
//							.password(new EncrytedPasswordUtils().encryptPassword("12345678"))
//							.role("ROLE_ADMIN")
//							.isActive(true)
//							.build()
//			);
//
//			repository.save(
//					User.builder()
//							.username("user1")
//							.password(new EncrytedPasswordUtils().encryptPassword("12345678"))
//							.role("ROLE_USER")
//							.isActive(true)
//							.build()
//			);
//		};
//	}
}
