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
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "customer")
@Indexed
@Getter
@Setter
public class Customer implements Serializable {
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

	@Length(message = "Độ dài 12 số", min = 12, max = 12)
	@Column(nullable = false, unique = true, length = 12)
	private String IDCard;

	@Length(message = "Độ dài từ 7 - 20 số", min = 7, max = 20)
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

	@PreUpdate
	public void lastModifiedAt() {
		this.lastModifiedAt = LocalDateTime.now();
	}

	@PostLoad
	public void sortDepositAccountSet() {
		this.depositAccountSet = this.depositAccountSet.stream()
				.sorted(Comparator.comparing(DepositAccount::getId))
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}
}
