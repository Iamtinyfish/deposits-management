package com.bank.depositsmanagement.controller;

import com.bank.depositsmanagement.dao.CustomerRepository;
import com.bank.depositsmanagement.dao.DepositAccountRepository;
import com.bank.depositsmanagement.dao.InterestRateReferenceRepository;
import com.bank.depositsmanagement.dao.UserRepository;
import com.bank.depositsmanagement.entity.Customer;
import com.bank.depositsmanagement.entity.DepositAccount;
import com.bank.depositsmanagement.entity.Employee;
import com.bank.depositsmanagement.entity.InterestRateReference;
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

    public DepositAccountController(CustomerRepository customerRepository, InterestRateReferenceRepository interestRateReferenceRepository, UserRepository userRepository, DepositAccountRepository depositAccountRepository) {
        this.customerRepository = customerRepository;
        this.interestRateReferenceRepository = interestRateReferenceRepository;
        this.userRepository = userRepository;
        this.depositAccountRepository = depositAccountRepository;
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

        Employee employee = userRepository.findByUsername(principal.getName()).getEmployee();
        float interestRate = interestRateReferenceRepository.findByPeriodAndCurrency(depositAccount.getPeriod(), depositAccount.getCurrency()).getInterestRate();

        depositAccount.setCreateBy(employee);
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
        return "deposit-account-detail";
    }

    @GetMapping("user/deposit-account/final-settlement")
    public String finalSettlementPage(Model model,@RequestParam(value = "depositAccountID") Long depositAccountID) {
        DepositAccount depositAccount = depositAccountRepository.findById(depositAccountID).orElse(null);

        if (depositAccount == null) {
            model.addAttribute("message", "Not found deposit account with ID " + depositAccountID);
            return "404";
        }

        int period = depositAccount.getPeriod();
        model.addAttribute("period", period);

        if (period > 0) {
            int timeDepositDays = (depositAccount.getNumberOfDay() / (depositAccount.getPeriod() * 30)) * 30 * depositAccount.getPeriod();
            model.addAttribute("timeDepositDays", timeDepositDays);
            model.addAttribute("timeDepositInterestRate", depositAccount.getInterestRate());
            model.addAttribute("timeDepositInterestMoney", depositAccount.getInterestMoney().setScale(2, RoundingMode.HALF_UP));
            BigDecimal balance1 = depositAccount.getBalance().add(depositAccount.getInterestMoney()).setScale(2, RoundingMode.HALF_UP);
            model.addAttribute("balance1", balance1);

            int demandDepositDays = depositAccount.getNumberOfDay() - timeDepositDays;
            model.addAttribute("demandDepositDays", demandDepositDays);
            float demandDepositInterestRate = interestRateReferenceRepository.findByPeriodAndCurrency(0,depositAccount.getCurrency()).getInterestRate();
            model.addAttribute("demandDepositInterestRate", demandDepositInterestRate);
            BigDecimal demandDepositInterestMoney = balance1.multiply(BigDecimal.valueOf(demandDepositDays * demandDepositInterestRate * 0.01 / 360)).setScale(2, RoundingMode.HALF_UP);
            model.addAttribute("demandDepositInterestMoney", demandDepositInterestMoney);
            BigDecimal balance2 = balance1.add(demandDepositInterestMoney).setScale(2, RoundingMode.HALF_UP);
            model.addAttribute("balance2", balance2);

            model.addAttribute("amountOfFinalSettlement", balance2.divide(BigDecimal.valueOf(1000),0,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(1000)).toPlainString() + depositAccount.getCurrency());
        } else {
            model.addAttribute("demandDepositDays", depositAccount.getNumberOfDay());
            model.addAttribute("demandDepositInterestRate", depositAccount.getInterestRate());
            model.addAttribute("demandDepositInterestMoney", depositAccount.getInterestMoney());
            BigDecimal finalBalance = depositAccount.getBalance().add(depositAccount.getInterestMoney()).setScale(2,RoundingMode.HALF_UP);
            model.addAttribute("balance2", finalBalance);

            model.addAttribute("amountOfFinalSettlement", finalBalance.divide(BigDecimal.valueOf(1000),0,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(1000)).toPlainString() + depositAccount.getCurrency());
        }



        return "final-settlement";
    }
}
