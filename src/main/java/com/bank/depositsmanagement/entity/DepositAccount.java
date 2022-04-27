package com.bank.depositsmanagement.entity;

import com.bank.depositsmanagement.utils.TimeConstant;
import lombok.*;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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
    @NumberFormat(pattern = "#,###,###,###.##", style = NumberFormat.Style.CURRENCY)
    private BigDecimal balance;

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

    @ManyToOne(optional = false)
    @JoinColumn(name = "create_by", nullable = false)
    private Employee createBy;

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
            int dayOfPeriod = this.period * TimeConstant.DAY_OF_MONTH;
            int numberOfPeriod = numberOfDay / dayOfPeriod;
            double interestRatePerPeriod = (this.interestRate * 0.01 / TimeConstant.MONTH_OF_YEAR) * this.period;
            this.interest = this.balance.multiply(
                    BigDecimal.valueOf(
                            Math.pow(1d + interestRatePerPeriod, numberOfPeriod) - 1d
                    )
            ).setScale(2, RoundingMode.HALF_UP);
            this.dateOfMaturity = LocalDate.now().plusDays(dayOfPeriod - (numberOfDay - ((long) numberOfPeriod * dayOfPeriod)));
        } else {
            this.interest = this.balance.multiply(
                    BigDecimal.valueOf(
                            this.numberOfDay * this.interestRate * 0.01 / TimeConstant.DAY_OF_YEAR
                    )
            ).setScale(2, RoundingMode.HALF_UP);
        }
    }
}
