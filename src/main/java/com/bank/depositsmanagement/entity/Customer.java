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
@Table(name = "customer")
@Getter
@Setter
public class Customer {
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(nullable = false)
	@NotBlank
	private String firstName;
	
	@Column(nullable = false)
	@NotBlank
	private String lastName;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private GenderType gender;

	@NotNull
	@Column(nullable = false)
	@PastOrPresent
	private LocalDate birthday;
	
	@NotBlank
	@Length(min = 12, max = 12)
	@Column(nullable = false, unique = true, length = 12)
	private String IDCard;

	@Length(max = 20)
	@NotBlank
	@Column(nullable = false, length = 20)
	private String phone;

	@Column(unique = true)
	@Email
	private String email;
	
	@Column(nullable = false)
	@NotBlank
	private String address;

	@NotNull
	@Column(nullable = false)
	@PastOrPresent
	private LocalDateTime createdAt;

	@NotNull
	@Column(nullable = false)
	@PastOrPresent
	private LocalDateTime lastModifiedAt;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "last_modified_by", nullable = false)
	private Employee lastModifiedBy;

	@OneToMany(mappedBy = "holder")
	private Set<DepositAccount> depositAccountSet;

	@PrePersist
	void createdAt() {
		this.createdAt = LocalDateTime.now();
	}
}
