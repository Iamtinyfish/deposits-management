package com.bank.depositsmanagement.entity;

import lombok.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "deposit_account")
@Getter
@Setter
public class DepositAccount {
	@Id
	@GeneratedValue
	private Long id;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "holder_id", nullable = false)
	private Customer holder;

	@Column(nullable = false)
	private float interestRate;

	@PositiveOrZero
	@Column(nullable = false)
	private int period;

	@Min(500000)
	@NotNull
	@Column(nullable = false)
	@NumberFormat(pattern = "#,###,###,###.##", style = NumberFormat.Style.CURRENCY)
	private BigDecimal balance;

	@PositiveOrZero
	@Transient
	private BigDecimal interestMoney;

	@PositiveOrZero
	@Transient
	private int numberOfDay;

	@Column(nullable = false)
	@NotNull
	@PastOrPresent
	private LocalDateTime createdAt;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "create_by", nullable = false)
	private Employee createBy;

	@NotNull
	@OneToMany(mappedBy = "depositAccount")
	private Set<Transaction> transactionSet = new java.util.LinkedHashSet<>();

	@PrePersist
	void createdAt() {
		this.createdAt = LocalDateTime.now();
	}
}
