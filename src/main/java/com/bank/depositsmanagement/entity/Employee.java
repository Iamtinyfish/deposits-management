package com.bank.depositsmanagement.entity;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employee")
@Getter
@Setter
public class Employee {
	@Id
	@GeneratedValue
	private Long id;
	
	@NotBlank
	@Column(nullable = false)
	private String firstName;
	
	@NotBlank
	@Column(nullable = false)
	private String lastName;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private GenderType gender;

	@NotNull
	@Column(nullable = false)
	@PastOrPresent
	private LocalDate birthday;
	
	@Length(min = 10, max = 12)
	@NotBlank
	@Column(nullable = false, unique = true, length = 12)
	private String IDCard;

	@Length(max = 20)
	@NotBlank
	@Column(nullable = false, length = 20)
	private String phone;

	@Column(nullable = false, unique = true)
	@Email
	private String email;

	@Column(nullable = false)
	@NotBlank
	private String address;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "varchar(255) default 'WORKING'")
	private EmployeeStatus status;

	@NotNull
	@Column(nullable = false)
	@PastOrPresent
	private LocalDateTime createdAt;

	@NotNull
	@Column(nullable = false)
	@PastOrPresent
	private LocalDateTime lastModifiedAt;

	//Foreign Key
	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "position_id", nullable = false)
	private Position position;
	
	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "branch_id", nullable = false)
	private Branch branch;

	//
	@OneToMany(mappedBy = "createBy")
	private Set<DepositAccount> depositAccountSet;

	@OneToMany(mappedBy = "employee")
	private Set<Transaction> transactionSet;

	@PrePersist
	void createdAt() {
		this.createdAt = LocalDateTime.now();
	}
}
