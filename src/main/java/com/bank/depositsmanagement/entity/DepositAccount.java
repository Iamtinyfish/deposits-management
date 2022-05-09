package com.bank.depositsmanagement.entity;

import com.bank.depositsmanagement.utils.TimeConstant;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "deposit_account")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepositAccount {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "holder_id", nullable = false)
    private Customer holder;

    @Column(nullable = false)
    private float interestRate;

    @Column(nullable = false)
    private int period;

    @PositiveOrZero(message = "Không thể là số âm")
    @NotNull(message = "Không được bỏ trống")
    @Column(nullable = false)
//    @NumberFormat(pattern = "#,###", style = NumberFormat.Style.CURRENCY)
    private BigDecimal originalAmount;

    //demand deposit interest before withdraw part or deposit extra
    @Column(nullable = false)
    @NumberFormat(pattern = "#,###", style = NumberFormat.Style.CURRENCY)
    private BigDecimal oldInterest = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyType currency;

    @Transient
    private int numberOfDay;

    @Transient
    private BigDecimal interest;

    @Transient
    private LocalDate dateOfMaturity;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDate periodStartAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "create_by", nullable = false)
    private Employee createBy;

    @ColumnDefault("false")
    private boolean finalSettlement;

    @OneToMany(mappedBy = "depositAccount")
    private Set<Transaction> transactionSet = new java.util.LinkedHashSet<>();

    @PrePersist
    private void createdAt() {
        this.createdAt = LocalDateTime.now();
        this.periodStartAt = LocalDate.now();
    }

    @PostLoad
    private void postLoad() {
        calculateTransientProperties();
        sortTransaction();
    }

    private void sortTransaction() {
        this.transactionSet = this.transactionSet.stream()
                .sorted(Comparator.comparing(Transaction::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private void calculateTransientProperties() {
        this.numberOfDay = ((int) ChronoUnit.DAYS.between(this.periodStartAt, LocalDate.now()));
//        this.numberOfDay = 100;
        if (period > 0) {
            //===========Time deposit interest===========//
            int dayOfPeriod = this.period * TimeConstant.DAY_OF_MONTH;
            int numberOfPeriod = numberOfDay / dayOfPeriod;
            double interestRatePerPeriod = (this.interestRate * 0.01 / TimeConstant.MONTH_OF_YEAR) * this.period;
            this.interest = this.originalAmount
                    .multiply(BigDecimal.valueOf(Math.pow(1d + interestRatePerPeriod, numberOfPeriod) - 1d))
                    .setScale(2, RoundingMode.HALF_UP);
            this.dateOfMaturity = LocalDate.now().plusDays(dayOfPeriod - (numberOfDay - ((long) numberOfPeriod * dayOfPeriod)));
            //===========================================//
        } else {
            //===========Demand deposit interest===========//
            this.interest = this.originalAmount
                    .multiply(BigDecimal.valueOf(this.numberOfDay * this.interestRate * 0.01 / TimeConstant.DAY_OF_YEAR))
                    .add(this.oldInterest)
                    .setScale(2, RoundingMode.HALF_UP);
            //=============================================//
        }
    }
}
