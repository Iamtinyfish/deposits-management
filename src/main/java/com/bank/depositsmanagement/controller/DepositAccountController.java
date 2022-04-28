package com.bank.depositsmanagement.controller;

import com.bank.depositsmanagement.dao.*;
import com.bank.depositsmanagement.entity.*;
import com.bank.depositsmanagement.utils.TimeConstant;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DepositAccountController {

    private final CustomerRepository customerRepository;
    private final InterestRateReferenceRepository interestRateReferenceRepository;
    private final UserRepository userRepository;
    private final DepositAccountRepository depositAccountRepository;
    private final TransactionRepository transactionRepository;

    public DepositAccountController(CustomerRepository customerRepository, InterestRateReferenceRepository interestRateReferenceRepository, UserRepository userRepository, DepositAccountRepository depositAccountRepository, TransactionRepository transactionRepository) {
        this.customerRepository = customerRepository;
        this.interestRateReferenceRepository = interestRateReferenceRepository;
        this.userRepository = userRepository;
        this.depositAccountRepository = depositAccountRepository;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("user/deposit-account/add")
    public String addDepositAccountPage(Model model, @RequestParam(value = "customerID") Long customerID) {

        DepositAccount depositAccount = new DepositAccount();
        Customer customer = customerRepository.findById(customerID).orElse(null);

        if (customer == null) {
            model.addAttribute("message", "Not found customer with ID " + customerID);
            return "404";
        }

        depositAccount.setHolder(customer);
        model.addAttribute("depositAccount", depositAccount);

        List<InterestRateReference> interestRateReferences = new ArrayList<>() ;
        interestRateReferenceRepository.findAll().forEach(interestRateReferences::add);
        model.addAttribute("interestRateReferences", interestRateReferences);

        Set<Integer> periods = interestRateReferences.stream().map(InterestRateReference::getPeriod).sorted().collect(Collectors.toCollection(LinkedHashSet::new));
        model.addAttribute("periods", periods);

        return "add-deposit-account";
    }

    @PostMapping("user/deposit-account/add")
    public String addDepositAccount(Model model, @ModelAttribute("depositAccount") @Valid DepositAccount depositAccount,
                                    @RequestParam(value = "holderID") String holderID,BindingResult bindingResult, Principal principal) {

        Customer customer = customerRepository.findById(Long.parseLong(holderID)).orElse(null);

        if (customer == null) {
            model.addAttribute("message", "Not found customer with ID " + holderID);
            return "404";
        }

        depositAccount.setHolder(customer);

        if (bindingResult.hasErrors()) {
            model.addAttribute("depositAccount", depositAccount);
            return "add-deposit-account";
        }

        float interestRate = interestRateReferenceRepository.findByPeriodAndCurrency(depositAccount.getPeriod(), depositAccount.getCurrency()).getInterestRate();

        depositAccount.setCreateBy(this.userRepository.findByUsername(principal.getName()).getEmployee());
        depositAccount.setInterestRate(interestRate);

        Long depositAccountID = depositAccountRepository.save(depositAccount).getId();

        return "redirect:/user/deposit-account/detail?depositAccountID="+depositAccountID;
    }

    @GetMapping("user/deposit-account/detail")
    public String depositAccountDetail(Model model,@RequestParam(value = "depositAccountID") Long depositAccountID) {
        DepositAccount depositAccount = depositAccountRepository.findById(depositAccountID).orElse(null);

        if (depositAccount == null) {
            model.addAttribute("message", "Not found deposit account with ID " + depositAccountID);
            return "404";
        }

        model.addAttribute("depositAccount", depositAccount);
        model.addAttribute("dateFormatter", TimeConstant.DATE_FORMATTER);
        model.addAttribute("dateTimeFormatter", TimeConstant.DATE_TIME_FORMATTER);
        return "deposit-account-detail";
    }

    @GetMapping("user/deposit-account/final-settlement")
    public String finalSettlementPage(Model model,@RequestParam(value = "depositAccountID") Long depositAccountID) {
        DepositAccount depositAccount = depositAccountRepository.findById(depositAccountID).orElse(null);
        if (depositAccount == null) {
            model.addAttribute("message", "Not found deposit account with ID " + depositAccountID);
            return "404";
        }

        if (depositAccount.isFinalSettlement()) {
            return "redirect:/user/deposit-account/detail?depositAccountID="+depositAccountID;
        }

        model.addAttribute("depositAccount", depositAccount);

        HashMap<String, Object> result = calculateInterest(depositAccount);

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
        model.addAttribute("amount", ((BigDecimal) result.get("finalBalance")).setScale(0, RoundingMode.HALF_UP).toPlainString() + " " + depositAccount.getCurrency());
        return "final-settlement";
    }

    @PostMapping("user/deposit-account/final-settlement")
    public String finalSettlment(Model model, @RequestParam(value = "depositAccountID") Long depositAccountID, Principal principal) {
        DepositAccount depositAccount = depositAccountRepository.findById(depositAccountID).orElse(null);
        if (depositAccount == null) {
            model.addAttribute("message", "Not found deposit account with ID " + depositAccountID);
            return "404";
        }

        if (depositAccount.isFinalSettlement()) {
            return "redirect:/user/deposit-account/detail?depositAccountID="+depositAccountID;
        }

        //round to decision = 0
        BigDecimal amount = ((BigDecimal) calculateInterest(depositAccount).get("finalBalance")).setScale(0, RoundingMode.HALF_UP);

        Transaction transaction = transactionRepository.save(
                Transaction.builder()
                        .depositAccount(depositAccount)
                        .amount(amount)
                        .description("Tất toán tài khoản")
                        .employee(this.userRepository.findByUsername(principal.getName()).getEmployee())
                        .build()
        );

        depositAccount.setFinalSettlement(true);
        depositAccount.setBalance(BigDecimal.valueOf(0));
        depositAccount.setInterest(BigDecimal.valueOf(0));
        depositAccountRepository.save(depositAccount);

        model.addAttribute("transaction", transaction);
        model.addAttribute("timeFormatter", TimeConstant.DATE_TIME_FORMATTER);

        return "transaction-result";
    }

    private HashMap<String, Object> calculateInterest(DepositAccount depositAccount) {
        HashMap<String,Object> result = new HashMap<>();

        int period = depositAccount.getPeriod();

        BigDecimal finalBalance;

        if (period > 0) {
            int dayOfPeriod = period * TimeConstant.DAY_OF_MONTH;
            int timeDepositDays = (depositAccount.getNumberOfDay() / dayOfPeriod) * dayOfPeriod;
            result.put("timeDepositDays", timeDepositDays);
            BigDecimal balance1 = depositAccount.getBalance().add(depositAccount.getInterest()).setScale(2, RoundingMode.HALF_UP);
            result.put("balance1", balance1);

            int demandDepositDays = depositAccount.getNumberOfDay() - timeDepositDays;
            result.put("demandDepositDays", demandDepositDays);
            float demandDepositInterestRate = this.interestRateReferenceRepository.findByPeriodAndCurrency(0,depositAccount.getCurrency()).getInterestRate();
            result.put("demandDepositInterestRate", demandDepositInterestRate);
            BigDecimal demandDepositInterest = balance1.multiply(
                    BigDecimal.valueOf(
                            demandDepositDays * demandDepositInterestRate * 0.01 / 360
                    )
            ).setScale(2, RoundingMode.HALF_UP);
            result.put("demandDepositInterest", demandDepositInterest);
            finalBalance = balance1.add(
                    demandDepositInterest
            ).setScale(2, RoundingMode.HALF_UP);
        } else {
            finalBalance = depositAccount.getBalance().add(
                    depositAccount.getInterest()
            ).setScale(2,RoundingMode.HALF_UP);
        }
        result.put("finalBalance", finalBalance);
        return result;
    }
}
