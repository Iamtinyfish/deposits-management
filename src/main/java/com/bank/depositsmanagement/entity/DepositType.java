package com.bank.depositsmanagement.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "deposit_type")
@Getter
@Setter
public class DepositType {
	@Id
	@GeneratedValue
	private Long id;
	
	@NotBlank
	@Column(nullable = false)
	private String name;

	private int depositMoreBeforeMaturity;
	
	@Column(nullable = false)
	private boolean isWithdrawPartOfFundsBeforeMaturity;
	
	@Column(nullable = false)
	private boolean isWithdrawInterest;
	
	@Column(nullable = false)
	private boolean isWithdrawAllOfFundsBeforeMaturity;

	@OneToMany(mappedBy = "depositType", orphanRemoval = true)
	private Set<PeriodAndInterestRate> periodAndInterestRateSet = new java.util.LinkedHashSet<>();
	
}
