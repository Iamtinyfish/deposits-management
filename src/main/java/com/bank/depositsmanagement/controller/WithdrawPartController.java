package com.bank.depositsmanagement.controller;

import com.bank.depositsmanagement.dao.DepositAccountRepository;
import com.bank.depositsmanagement.dao.TransactionRepository;
import com.bank.depositsmanagement.dao.UserRepository;
import com.bank.depositsmanagement.entity.*;
import com.bank.depositsmanagement.utils.CurrencyConstant;
import com.bank.depositsmanagement.utils.TimeConstant;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.LocalDate;

@Controller
public class WithdrawPartController {
    private final DepositAccountRepository depositAccountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public WithdrawPartController(DepositAccountRepository depositAccountRepository, TransactionRepository transactionRepository, UserRepository userRepository) {
        this.depositAccountRepository = depositAccountRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("user/deposit-account/withdraw-part")
    public String withdrawPartPage(Model model, @RequestParam(value = "depositAccountID") Long depositAccountID) {
        DepositAccount depositAccount = depositAccountRepository.findById(depositAccountID).orElse(null);
        if (depositAccount == null) {
            model.addAttribute("message", "Không tìm thấy tài khoản tiền gửi với mã là " + depositAccountID);
            return "404";
        }

        if (depositAccount.getPeriod() != 0) {
            model.addAttribute("message", "Chức năng này không khả dụng với tài khoản tiền gửi có kì hạn");
            return "error";
        }

        model.addAttribute("depositAccountID",depositAccountID);
        model.addAttribute("currency", depositAccount.getCurrency().toString());
        model.addAttribute("balance", depositAccount.getOriginalAmount().add(depositAccount.getInterest()).setScale(2, RoundingMode.HALF_UP));
        model.addAttribute("currencyFormatter", (depositAccount.getCurrency() == CurrencyType.VND) ? CurrencyConstant.VND_FORMATTER : CurrencyConstant.USD_FORMATTER);
        return "withdraw-part";
    }

    @PostMapping("user/deposit-account/withdraw-part")
    @Transactional
    public String withdrawPart(Model model, @RequestParam(value = "depositAccountID") Long depositAccountID,
                               @RequestParam(value = "amount") BigDecimal amount, Principal principal) {
        DepositAccount depositAccount = depositAccountRepository.findById(depositAccountID).orElse(null);
        if (depositAccount == null) {
            model.addAttribute("message", "Không tìm thấy tài khoản tiền gửi với mã là " + depositAccountID);
            return "404";
        }

        if (depositAccount.getPeriod() != 0) {
            model.addAttribute("message", "Chức năng này không khả dụng với tài khoản tiền gửi có kì hạn");
            return "error";
        }

        BigDecimal interest = depositAccount.getInterest();
        BigDecimal balance = depositAccount.getOriginalAmount().add(interest).setScale(0,RoundingMode.HALF_UP);
        String error = null;

        //============validate============//
        if (amount == null) error = "Không được bỏ trống trường này";
        else {
            if (amount.compareTo(balance) == 1) {
                error = "Số tiền phải nhỏ hơn số dư khả dụng";
            } else {
                if (depositAccount.getCurrency() == CurrencyType.VND
                        && amount.remainder(BigDecimal.valueOf(100000)).compareTo(BigDecimal.ZERO) != 0) {
                    error = "Số tiền phải là bội số của 100,000 &#8363;";
                } else if (depositAccount.getCurrency() == CurrencyType.USD
                        && amount.remainder(BigDecimal.valueOf(5)).compareTo(BigDecimal.ZERO) != 0) {
                    error = "Số tiền phải là bội số của $5";
                }
            }
        }

        if (error != null) {
            model.addAttribute("error", error);
            model.addAttribute("depositAccountID",depositAccountID);
            model.addAttribute("currency", depositAccount.getCurrency().toString());
            model.addAttribute("balance", balance);
            model.addAttribute("currencyFormatter", (depositAccount.getCurrency() == CurrencyType.VND) ? CurrencyConstant.VND_FORMATTER : CurrencyConstant.USD_FORMATTER);
            return "withdraw-part";
        }
        //=================================//

        User user = userRepository.findByUsername(principal.getName()).orElse(null);

        if (user == null || user.getEmployee() == null) {
            model.addAttribute("message", "Không xác định được thông tin của bạn");
            return "404";
        }

        Employee employee = user.getEmployee();

        if (amount.compareTo(interest) < 0) {
        //amount > interest
            depositAccount.setOldInterest(interest.subtract(amount).setScale(2,RoundingMode.HALF_UP));
        } else {
        //amount < interest
            depositAccount.setOldInterest(BigDecimal.ZERO);
            //new origin amount = (origin + interest) - amount
            depositAccount.setOriginalAmount(depositAccount.getOriginalAmount()
                    .add(interest)
                    .subtract(amount)
                    .setScale(2,RoundingMode.HALF_UP)
            );
        }

        depositAccount.setInterest(BigDecimal.ZERO);
        depositAccount.setPeriodStartAt(LocalDate.now());
        depositAccountRepository.save(depositAccount);

        Transaction transaction = transactionRepository.save(
                Transaction.builder()
                        .depositAccount(depositAccount)
                        .amount(amount)
                        .description("Rút một phần")
                        .employee(employee)
                        .build()
        );

        model.addAttribute("transaction", transaction);
        model.addAttribute("timeFormatter", TimeConstant.DATE_TIME_FORMATTER);
        model.addAttribute("currencyFormatter", (depositAccount.getCurrency() == CurrencyType.VND) ? CurrencyConstant.VND_FORMATTER : CurrencyConstant.USD_FORMATTER);
        return "transaction-result";
    }
}
