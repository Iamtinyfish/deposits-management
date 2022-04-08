package com.bank.depositsmanagement.controller;

import com.bank.depositsmanagement.dao.CustomerRepository;
import com.bank.depositsmanagement.dao.UserRepository;
import com.bank.depositsmanagement.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class CustomerController {

    private final CustomerRepository customerRepository;

    private final UserRepository userRepository;

    public CustomerController(CustomerRepository customerRepository, UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    @GetMapping({"user/","user/customer"})
    public String customerPage() {
        return "customer";
    }

    @GetMapping("user/customer/add")
    public String addCustomerPage(Model model) {

        Customer customer = new Customer();
        model.addAttribute("customer", customer);

        GenderType[] genderType = GenderType.values();
        model.addAttribute("genderType", genderType);

        return "add-customer";
    }

    @PostMapping("user/customer/add")
    public String addCustomer(Model model, @ModelAttribute("customer") @Valid Customer customer, BindingResult bindingResult, Principal principal) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("customer", customer);
            GenderType[] genderType = GenderType.values();
            model.addAttribute("genderType", genderType);
            return "add-customer";
        }

        Employee employee = userRepository.findByUsername(principal.getName()).getEmployee();

        customer.setLastModifiedBy(employee);
        customerRepository.save(customer);

        return "redirect:/user/customer/detail?IDCard="+customer.getIDCard();
    }

    @GetMapping("user/customer/detail")
    public String customerDetail(Model model,@RequestParam(value = "IDCard") String IDCard) {
        Customer customer = customerRepository.findByIDCard(IDCard);
        model.addAttribute("customer", customer);
        return "customer-detail";
    }
}
