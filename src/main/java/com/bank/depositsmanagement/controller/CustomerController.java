package com.bank.depositsmanagement.controller;

import com.bank.depositsmanagement.dao.CustomerRepository;
import com.bank.depositsmanagement.dao.UserRepository;
import com.bank.depositsmanagement.entity.*;
import com.bank.depositsmanagement.utils.TimeConstant;
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

        return "add-customer";
    }

    @PostMapping("user/customer/add")
    public String addCustomer(Model model, @ModelAttribute("customer") @Valid Customer customer, BindingResult bindingResult, Principal principal) {
        //check unique fields
        boolean doesIDCardExist = customerRepository.existsByIDCard(customer.getIDCard());
        if (doesIDCardExist) model.addAttribute("errorIDCard", "Số CCCD đã tồn tại");
        boolean doesEmailExist = customerRepository.existsByEmail(customer.getEmail());
        if (doesEmailExist) model.addAttribute("errorEmail", "Email đã tồn tại");

        if (bindingResult.hasErrors() || doesEmailExist || doesIDCardExist) {
            model.addAttribute("customer", customer);
            return "add-customer";
        }

        User user = userRepository.findByUsername(principal.getName()).orElse(null);

        if (user == null || user.getEmployee() == null) {
            model.addAttribute("message", "Không xác định được thông tin của bạn");
            return "404";
        }

        Employee employee = user.getEmployee();

        customer.setLastModifiedBy(employee);

        customerRepository.save(customer);

        return "redirect:/user/customer/detail?IDCard="+customer.getIDCard();
    }

    @GetMapping("user/customer/detail")
    public String customerDetail(Model model, @RequestParam(value = "IDCard") String IDCard) {
        Customer customer = customerRepository.findByIDCard(IDCard).orElse(null);
        if (customer == null) {
            model.addAttribute("message", "Không tìm thấy khách hàng với số CCCD là " + IDCard);
            return "404";
        }
        model.addAttribute("customer", customer);
        model.addAttribute("readOnly", true);
        model.addAttribute("dateFormatter", TimeConstant.DATE_FORMATTER);
        return "customer-detail";
    }

    @PostMapping("user/customer/update")
    public String updateCustomer(Model model, @ModelAttribute("customer") @Valid Customer customer, BindingResult bindingResult, Principal principal) {
        model.addAttribute("dateFormatter", TimeConstant.DATE_FORMATTER);
        Customer oldCustomer = customerRepository.findById(customer.getId()).orElse(null);

        if (oldCustomer == null) {
            model.addAttribute("message", "Không tìm thấy khách hàng với mã là " + customer.getId());
            return "404";
        }

        customer.setCreatedAt(oldCustomer.getCreatedAt());

        //check unique fields
        boolean doesIDCardExist = !customer.getIDCard().equals(oldCustomer.getIDCard()) && customerRepository.existsByIDCard(customer.getIDCard());
        if (doesIDCardExist) model.addAttribute("errorIDCard", "Số CCCD đã tồn tại");
        boolean doesEmailExist = !customer.getEmail().equals(oldCustomer.getEmail()) && customerRepository.existsByEmail(customer.getEmail());
        if (doesEmailExist) model.addAttribute("errorEmail", "Email đã tồn tại");

        if (bindingResult.hasErrors() || doesEmailExist || doesIDCardExist) {
            customer.setLastModifiedAt(oldCustomer.getLastModifiedAt());
            customer.setLastModifiedBy(oldCustomer.getLastModifiedBy());
            model.addAttribute("customer", customer);
            model.addAttribute("readOnly", false);
            return "customer-detail";
        }

        User user = userRepository.findByUsername(principal.getName()).orElse(null);

        if (user == null || user.getEmployee() == null) {
            model.addAttribute("message", "Không xác định được thông tin của bạn");
            return "404";
        }

        Employee employee = user.getEmployee();

        customer.setLastModifiedBy(employee);

        customerRepository.save(customer);

        return "redirect:/user/customer/detail?IDCard="+customer.getIDCard();
    }
}
