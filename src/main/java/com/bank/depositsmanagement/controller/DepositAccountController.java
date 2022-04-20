package com.bank.depositsmanagement.controller;

import com.bank.depositsmanagement.dao.CustomerRepository;
import com.bank.depositsmanagement.dao.InterestRateRepository;
import com.bank.depositsmanagement.entity.Customer;
import com.bank.depositsmanagement.entity.DepositAccount;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityNotFoundException;

@Controller
public class DepositAccountController {

    private final CustomerRepository customerRepository;

    private final InterestRateRepository interestRateRepository;


    public DepositAccountController(CustomerRepository customerRepository, InterestRateRepository interestRateRepository) {
        this.customerRepository = customerRepository;
        this.interestRateRepository = interestRateRepository;
    }

    @GetMapping("user/deposit-account/add")
    public String addCustomerPage(Model model, @RequestParam(value = "customerID") Long customerID) {

        DepositAccount depositAccount = new DepositAccount();
        Customer customer = customerRepository.findById(customerID).orElseThrow(EntityNotFoundException::new);
        depositAccount.setHolder(customer);

        model.addAttribute("depositAccount", depositAccount);

        model.addAttribute("interestRate", interestRateRepository.findAll());

        return "add-deposit-account";
    }

//    @PostMapping("user/customer/add")
//    public String addCustomer(Model model, @ModelAttribute("customer") @Valid Customer customer, BindingResult bindingResult, Principal principal) {
//
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("customer", customer);
//            return "add-customer";
//        }
//
//        Employee employee = userRepository.findByUsername(principal.getName()).getEmployee();
//
//        customer.setLastModifiedBy(employee);
//        customerRepository.save(customer);
//
//        return "redirect:/user/customer/detail?IDCard="+customer.getIDCard();
//    }

//    @GetMapping("user/customer/detail")
//    public String customerDetail(Model model,@RequestParam(value = "IDCard") String IDCard) {
//        Customer customer = customerRepository.findByIDCard(IDCard);
//        model.addAttribute("customer", customer);
//        return "customer-detail";
//    }
}
