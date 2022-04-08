package com.bank.depositsmanagement.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
@Getter
@Setter
public class User {
	@Id
	@GeneratedValue
	@Column(nullable = false, updatable = false)
	private Long id;

	@Pattern(message = "Tên tài khoản gồm 5-20 kí tự chữ, số, gạch dưới và phải bắt đầu bằng chữ, kết thúc bằng chữ hoặc số", regexp = "^[a-zA-Z](_|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$")
	private String username;

//	@Pattern(message = "Mật khẩu phải có ít nhất 8 kí tự và không chứa khoảng trắng", regexp = "[^\\s\\n]{8,}")
	@NotBlank
	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	@NotBlank
	private String role;

	@Column(nullable = false)
	private boolean isActive;
	
	@OneToOne(mappedBy = "user")
	private Employee employee;
}
