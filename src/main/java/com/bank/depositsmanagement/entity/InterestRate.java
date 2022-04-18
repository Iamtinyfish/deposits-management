package com.bank.depositsmanagement.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.PositiveOrZero;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "interest_rate")
@Getter
@Setter
public class InterestRate {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private float interestRate;

    @PositiveOrZero
    @Column(nullable = false)
    private int period;
}
