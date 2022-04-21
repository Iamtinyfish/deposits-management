package com.bank.depositsmanagement.entity;

import lombok.*;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Entity
@Table(name = "deposit_account")
@Getter
@Setter
public class DepositAccount {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "holder_id", nullable = false)
    private Customer holder;

    @Column(nullable = false)
    private float interestRate;

    @PositiveOrZero
    @Column(nullable = false)
    private int period;

    @PositiveOrZero
    @NotNull
    @Column(nullable = false)
    @NumberFormat(pattern = "#,###,###,###.##", style = NumberFormat.Style.CURRENCY)
    private BigDecimal balance;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyType currency;

    @PositiveOrZero
    @Transient
    private BigDecimal interestMoney;

    @PositiveOrZero
    @Transient
    private int numberOfDay;

    @Column(nullable = false)
    @PastOrPresent
    private LocalDateTime createdAt;

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

    @PostLoad
    void calculateTransientProperties() {
//        this.numberOfDay = ((int) ChronoUnit.DAYS.between(this.createdAt, LocalDateTime.now()));
        this.numberOfDay = 100;
        if (period > 0) {
            int numberOfPeriod = numberOfDay / (this.period * 30);
//            int numberOfPeriod = 2;
            if (numberOfPeriod == 0) {
                this.interestMoney = BigDecimal.valueOf(0);
            } else {
                int numberOfPeriodPerYear = 12 / this.period;
                double interestRatePerPeriod = this.interestRate * 0.01 / numberOfPeriodPerYear;
                this.interestMoney = this.balance.multiply(
                        BigDecimal.valueOf(
                                Math.pow(1d + interestRatePerPeriod, numberOfPeriod) - 1d
                        )
                ).setScale(2, RoundingMode.HALF_UP);
            }
        } else {
            this.interestMoney = this.balance.multiply(BigDecimal.valueOf(this.numberOfDay * this.interestRate * 0.01 / 360)).setScale(2, RoundingMode.HALF_UP);;
        }
    }
}
