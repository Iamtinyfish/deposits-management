package com.bank.depositsmanagement.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static com.bank.depositsmanagement.entity.EmployeeStatus.WORKING;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employee")
@Getter
@Setter
public class Employee implements Serializable {
	@Id
	@GeneratedValue
	private Long id;
	
	@NotBlank(message = "Không được bỏ trống trường này")
	@Column(nullable = false)
	private String firstName;
	
	@NotBlank(message = "Không được bỏ trống trường này")
	@Column(nullable = false)
	private String lastName;

	@Column(nullable = false)
	private boolean gender;

	@NotNull(message = "Không được bỏ trống trường này")
	@Column(nullable = false)
	@PastOrPresent(message = "Phải là thời gian trong quá khứ")
	private LocalDate birthday;
	
	@Length(message = "CCCD phải có 12 số", min = 12, max = 12)
	@NotBlank(message = "Không được bỏ trống trường này")
	@Column(nullable = false, unique = true, length = 12)
	private String IDCard;

	@Length(max = 20)
	@NotBlank(message = "Không được bỏ trống trường này")
	@Column(nullable = false, length = 20)
	private String phone;

	@NotBlank(message = "Không được bỏ trống trường này")
	@Column(nullable = false, unique = true)
	@Email(message = "Không phải là email")
	private String email;

	@Column(nullable = false)
	@NotBlank(message = "Không được bỏ trống trường này")
	private String address;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@ColumnDefault("WORKING")
	private EmployeeStatus status;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime lastModifiedAt;

	//Foreign Key
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "position_id")
	private Position position;

	//
	@OneToMany(mappedBy = "createBy")
	private Set<DepositAccount> depositAccountSet;

	@OneToMany(mappedBy = "employee")
	private Set<Transaction> transactionSet;

	@PrePersist
	public void creatAt() {
		LocalDateTime now = LocalDateTime.now();
		this.createdAt = now;
		this.lastModifiedAt = now;
	}

	@PreUpdate
	public void lastModifiedAt() {
		this.lastModifiedAt = LocalDateTime.now();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" +
				"id = " + id + ", " +
				"firstName = " + firstName + ", " +
				"lastName = " + lastName + ", " +
				"gender = " + gender + ", " +
				"birthday = " + birthday + ", " +
				"IDCard = " + IDCard + ", " +
				"phone = " + phone + ", " +
				"email = " + email + ", " +
				"address = " + address + ", " +
				"status = " + status + ", " +
				"createdAt = " + createdAt + ", " +
				"lastModifiedAt = " + lastModifiedAt + ", " +
				"user = " + user + ", " +
				"position = " + position + ")";
	}
}
