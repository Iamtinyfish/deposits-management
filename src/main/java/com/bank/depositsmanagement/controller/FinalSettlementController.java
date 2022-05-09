package com.bank.depositsmanagement.controller;

import com.bank.depositsmanagement.dao.DepositAccountRepository;
import com.bank.depositsmanagement.dao.TransactionRepository;
import com.bank.depositsmanagement.dao.UserRepository;
import com.bank.depositsmanagement.entity.*;
import com.bank.depositsmanagement.utils.CurrencyConstant;
import com.bank.depositsmanagement.utils.Interest;
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
import java.util.HashMap;

@Controller
public class FinalSettlementController {
    private final DepositAccountRepository depositAccountRepository;
    private final TransactionRepository transactionRepository;
    private final Interest interest;
    private final UserRepository userRepository;

    public FinalSettlementController(DepositAccountRepository depositAccountRepository, TransactionRepository transactionRepository, Interest interest, UserRepository userRepository) {
        this.depositAccountRepository = depositAccountRepository;
        this.transactionRepository = transactionRepository;
        this.interest = interest;
        this.userRepository = userRepository;
    }

    @GetMapping("user/deposit-account/final-settlement")
    public String finalSettlementPage(Model model, @RequestParam(value = "depositAccountID") Long depositAccountID) {
        DepositAccount depositAccount = depositAccountRepository.findById(depositAccountID).orElse(null);
        if (depositAccount == null) {
            model.addAttribute("message", "Không tìm thấy tài khoản tiền gửi với mã là " + depositAccountID);
            return "404";
        }

        if (depositAccount.isFinalSettlement()) {
            return "redirect:/user/deposit-account/detail?depositAccountID="+depositAccountID;
        }

        model.addAttribute("depositAccount", depositAccount);
        HashMap<String, Object> result = interest.calculateInterest(depositAccount);

        if (depositAccount.getPeriod() > 0) {
            model.addAttribute("timeDepositDays", result.get("timeDepositDays"));
            model.addAttribute("timeDepositInterestRate", depositAccount.getInterestRate());
            model.addAttribute("timeDepositInterest", depositAccount.getInterest());
            model.addAttribute("balance1", result.get("balance1"));

            model.addAttribute("demandDepositDays", result.get("demandDepositDays"));
            model.addAttribute("demandDepositInterestRate", result.get("demandDepositInterestRate"));
            model.addAttribute("demandDepositInterest", result.get("demandDepositInterest"));
        } else {
            model.addAttribute("demandDepositDays", depositAccount.getNumberOfDay());
            model.addAttribute("demandDepositInterestRate", depositAccount.getInterestRate());
            model.addAttribute("demandDepositInterest", depositAccount.getInterest());
        }

        model.addAttribute("finalBalance", result.get("finalBalance"));
        //round final balance with VND
        BigDecimal amount = (BigDecimal) result.get("finalBalance");
        if (depositAccount.getCurrency() == CurrencyType.VND) {
            final BigDecimal ONE_THOUSAND = BigDecimal.valueOf(1000);
            amount = amount.divide(ONE_THOUSAND,0, RoundingMode.FLOOR).multiply(ONE_THOUSAND);
        }
        model.addAttribute("amount", amount.setScale(0,RoundingMode.FLOOR));
        model.addAttribute("currencyFormatter", (depositAccount.getCurrency() == CurrencyType.VND) ? CurrencyConstant.VND_FORMATTER : CurrencyConstant.USD_FORMATTER);
        return "final-settlement";
    }

    @PostMapping("user/deposit-account/final-settlement")
    @Transactional
    public String finalSettlment(Model model, @RequestParam(value = "depositAccountID") Long depositAccountID, Principal principal) {
        DepositAccount depositAccount = depositAccountRepository.findById(depositAccountID).orElse(null);
        if (depositAccount == null) {
            model.addAttribute("message", "Không tìm thấy tài khoản tiền gửi với mã là " + depositAccountID);
            return "404";
        }

        if (depositAccount.isFinalSettlement()) {
            return "redirect:/user/deposit-account/detail?depositAccountID="+depositAccountID;
        }

        //round final balance with VND
        BigDecimal amount = ((BigDecimal) interest.calculateInterest(depositAccount).get("finalBalance")).setScale(0, RoundingMode.FLOOR);
        if (depositAccount.getCurrency() == CurrencyType.VND) {
            final BigDecimal ONE_THOUSAND = BigDecimal.valueOf(1000);
            amount = amount.divide(ONE_THOUSAND,0,RoundingMode.FLOOR).multiply(ONE_THOUSAND);
        }

        User user = userRepository.findByUsername(principal.getName()).orElse(null);

        if (user == null || user.getEmployee() == null) {
            model.addAttribute("message", "Không xác định được thông tin của bạn");
            return "404";
        }

        Employee employee = user.getEmployee();

        Transaction transaction = transactionRepository.save(
                Transaction.builder()
                        .depositAccount(depositAccount)
                        .amount(amount)
                        .description("Tất toán tài khoản")
                        .employee(employee)
                        .build()
        );

        depositAccount.setFinalSettlement(true);
        depositAccount.setOriginalAmount(BigDecimal.ZERO);
        depositAccount.setInterest(BigDecimal.ZERO);
        depositAccountRepository.save(depositAccount);

        model.addAttribute("transaction", transaction);
        model.addAttribute("timeFormatter", TimeConstant.DATE_TIME_FORMATTER);
        model.addAttribute("currencyFormatter", (depositAccount.getCurrency() == CurrencyType.VND) ? CurrencyConstant.VND_FORMATTER : CurrencyConstant.USD_FORMATTER);
        return "transaction-result";
    }
}
