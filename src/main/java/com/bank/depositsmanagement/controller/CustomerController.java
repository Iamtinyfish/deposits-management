package com.bank.depositsmanagement.controller;

import com.bank.depositsmanagement.dao.CustomerRepository;
import com.bank.depositsmanagement.entity.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping({"user/","user/customer"})
    public String customerPage() {
        return "customer";
    }

    @GetMapping("user/customer/add")
    public String addCustomerPage(Model model) {

        Customer customer = new Customer();
        model.addAttribute("customer", customer);

        return "add-customer";
    }

    @PostMapping("user/customer/add")
    public String addCustomer(Model model, @ModelAttribute("customer") @Valid Customer customer, BindingResult bindingResult, HttpSession session) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("customer", customer);
            return "add-customer";
        }

        customer.setLastModifiedBy((Employee) session.getAttribute("myProfile"));

        customerRepository.save(customer);

        return "redirect:/user/customer/detail?IDCard="+customer.getIDCard();
    }

    @GetMapping("user/customer/detail")
    public String customerDetail(Model model, @RequestParam(value = "IDCard") String IDCard) {
        Customer customer = customerRepository.findByIDCard(IDCard);
        model.addAttribute("customer", customer);
        model.addAttribute("readOnly", true);
        return "customer-detail";
    }
}
