package com.bank.depositsmanagement.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Entity
@Table(name = "period_and_interest_rate")
@Getter
@Setter
public class PeriodAndInterestRate {

	@Id
	@GeneratedValue
	private Long id;
	
	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "deposit_type_id", nullable = false)
	private DepositType depositType;
	
	@Column(nullable = false)
	private float interestRate;

	@PositiveOrZero
	@Column(nullable = false)
	private int period;

	@NotNull
	@Column(nullable = false)
	@PastOrPresent
	private LocalDateTime lastModifiedAt;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(nullable = false)
	private Employee lastModifiedBy;
}
