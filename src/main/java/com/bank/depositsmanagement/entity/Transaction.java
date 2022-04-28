package com.bank.depositsmanagement.entity;


import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
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

	@Column(nullable = false)
	private String description;

	@ManyToOne(optional = false)
	@JoinColumn(name = "employee_id", nullable = false)
	private Employee employee;

	@PrePersist
	void time() {
		this.time = LocalDateTime.now();
	}
	
}
