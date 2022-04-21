package com.bank.depositsmanagement.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "interest_rate_reference")
@Getter
@Setter
public class InterestRateReference {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private float interestRate;

    @PositiveOrZero
    @Column(nullable = false)
    private int period;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyType currency;
}
