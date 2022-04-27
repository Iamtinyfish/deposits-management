package com.bank.depositsmanagement.entity;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Indexed;

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
@Indexed
@Getter
@Setter
public class Customer {
	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	@NotBlank(message = "Không được bỏ trống trường này")
	private String firstName;

	@Column(nullable = false)
	@NotBlank(message = "Không được bỏ trống trường này")
	private String lastName;

	@Column(nullable = false)
	private boolean gender;

	@NotNull(message = "Không được bỏ trống trường này")
	@Column(nullable = false)
	@PastOrPresent(message = "Phải là thời gian trong quá khứ")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate birthday;
	
	@NotBlank(message = "Không được bỏ trống trường này")
	@Length(message = "CCCD phải có 12 số", min = 12, max = 12)
	@Column(nullable = false, unique = true, length = 12)
	private String IDCard;

	@Length(max = 20)
	@NotBlank(message = "Không được bỏ trống trường này")
	@Column(nullable = false, length = 20)
	private String phone;

	@Column(unique = true)
	@Email
	private String email;
	
	@Column(nullable = false)
	@NotBlank(message = "Không được bỏ trống trường này")
	private String address;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime lastModifiedAt;

	@ManyToOne(optional = false)
	@JoinColumn(name = "last_modified_by", nullable = false)
	private Employee lastModifiedBy;

	@OneToMany(mappedBy = "holder")
	private Set<DepositAccount> depositAccountSet = new java.util.LinkedHashSet<>();

	@PrePersist
	public void setTimeInfo() {
		LocalDateTime now = LocalDateTime.now();
		this.createdAt = now;
		this.lastModifiedAt = now;
	}
}
