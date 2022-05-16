package com.bank.depositsmanagement.controller;

import com.bank.depositsmanagement.dao.*;
import com.bank.depositsmanagement.entity.*;
import com.bank.depositsmanagement.utils.CurrencyConstant;
import com.bank.depositsmanagement.utils.TimeConstant;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DepositAccountController {

    private final CustomerRepository customerRepository;
    private final InterestRateReferenceRepository interestRateReferenceRepository;
    private final DepositAccountRepository depositAccountRepository;

    public DepositAccountController(CustomerRepository customerRepository, InterestRateReferenceRepository interestRateReferenceRepository, DepositAccountRepository depositAccountRepository) {
        this.customerRepository = customerRepository;
        this.interestRateReferenceRepository = interestRateReferenceRepository;
        this.depositAccountRepository = depositAccountRepository;
    }

    @GetMapping("employee/deposit-account/add")
    public String addDepositAccountPage(Model model, @RequestParam(value = "customerID") Long customerID) {

        DepositAccount depositAccount = new DepositAccount();
        Customer customer = customerRepository.findById(customerID).orElse(null);

        if (customer == null) {
            model.addAttribute("message", "Không tìm thấy khách hàng với mã là " + customerID);
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

    @PostMapping("employee/deposit-account/add")
    public String addDepositAccount(Model model, @ModelAttribute("depositAccount") @Valid DepositAccount depositAccount,
                                    @RequestParam(value = "holderID") String holderID,BindingResult bindingResult, Principal principal) {

        Customer customer = customerRepository.findById(Long.parseLong(holderID)).orElse(null);
        if (customer == null) {
            model.addAttribute("message", "Không tìm thấy khách hàng với mã là " + holderID);
            return "404";
        }
        depositAccount.setHolder(customer);

        InterestRateReference interestRateReference = interestRateReferenceRepository.findByPeriodAndCurrency(depositAccount.getPeriod(), depositAccount.getCurrency()).orElse(null);
        if (interestRateReference == null)  {
            model.addAttribute("errorInterest","Không tìm thấy lãi suất phù hợp với kì hạn và loại tiền tệ đã chọn");
        }
        depositAccount.setInterestRate(interestRateReference.getInterestRate());

        BigDecimal originalAmount = depositAccount.getOriginalAmount();
        int period = depositAccount.getPeriod();
        String errorOriginalAmount = null;

        if (originalAmount != null) {
            switch (depositAccount.getCurrency()) {
                case VND:
                    if (period > 0 && originalAmount.compareTo(BigDecimal.valueOf(1000000)) < 0)
                        errorOriginalAmount = "Số tiền gửi có kì hạn với VND tối thiểu là 1,000,000đ";
                    else if (period == 0 && originalAmount.compareTo(BigDecimal.valueOf(500000)) < 0)
                        errorOriginalAmount = "Số tiền gửi không kì hạn với VND tối thiểu là 500,000đ";
                    break;
                case USD:
                    if (period > 0 && originalAmount.compareTo(BigDecimal.valueOf(100)) < 0)
                        errorOriginalAmount = "Số tiền gửi có kì hạn với USD tối thiểu là 100$";
                    else if (period == 0 && originalAmount.compareTo(BigDecimal.valueOf(50)) < 0)
                        errorOriginalAmount = "Số tiền gửi không kì hạn với USD tối thiểu là 50$";
                    break;
            }
        } else {
            errorOriginalAmount = "Không được bỏ trống trường này";
        }

        if (errorOriginalAmount != null)
            bindingResult.addError(new FieldError("depositAccount","originalAmount",errorOriginalAmount));

        if (bindingResult.hasErrors()) {
            model.addAttribute("depositAccount", depositAccount);

            List<InterestRateReference> interestRateReferences = new ArrayList<>() ;
            interestRateReferenceRepository.findAll().forEach(interestRateReferences::add);
            model.addAttribute("interestRateReferences", interestRateReferences);

            Set<Integer> periods = interestRateReferences.stream().map(InterestRateReference::getPeriod).sorted().collect(Collectors.toCollection(LinkedHashSet::new));
            model.addAttribute("periods", periods);
            return "add-deposit-account";
        }

        Account account = (Account) ((Authentication) principal).getPrincipal();
        Employee employee = account.getEmployee();

        if (employee == null) {
            model.addAttribute("message", "Không tìm thấy thông tin của bạn");
            return "404";
        }
        depositAccount.setCreateBy(employee);

        Long depositAccountID = depositAccountRepository.save(depositAccount).getId();

        return "redirect:/employee/deposit-account/detail?depositAccountID="+depositAccountID;
    }

    @GetMapping("employee/deposit-account/detail")
    public String depositAccountDetail(Model model,@RequestParam(value = "depositAccountID") Long depositAccountID) {
        DepositAccount depositAccount = depositAccountRepository.findById(depositAccountID).orElse(null);

        if (depositAccount == null) {
            model.addAttribute("message", "Không tìm thấy tài khoản tiền gửi với mã là " + depositAccountID);
            return "404";
        }

        model.addAttribute("depositAccount", depositAccount);
        model.addAttribute("dateFormatter", TimeConstant.DATE_FORMATTER);
        model.addAttribute("dateTimeFormatter", TimeConstant.DATE_TIME_FORMATTER);
        model.addAttribute("currencyFormatter", (depositAccount.getCurrency() == CurrencyType.VND) ? CurrencyConstant.VND_FORMATTER : CurrencyConstant.USD_FORMATTER);
        return "deposit-account-detail";
    }
}
