package com.bank.depositsmanagement.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Getter
@Setter
public class Transaction {
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "deposit_account_id", nullable = false)
	private DepositAccount depositAccount;
	
	@NotNull(message = "Không được bỏ trống trường này")
	@Column(nullable = false)
	private BigDecimal amount;

	@Column(nullable = false)
	private LocalDateTime time;
	
	private String description;

	@ManyToOne(optional = false)
	@JoinColumn(name = "employee_id", nullable = false)
	private Employee employee;

	@PrePersist
	void time() {
		this.time = LocalDateTime.now();
	}
	
}
