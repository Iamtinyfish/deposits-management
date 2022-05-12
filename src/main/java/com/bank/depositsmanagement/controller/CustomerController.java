package com.bank.depositsmanagement.controller;

import com.bank.depositsmanagement.dao.AccountRepository;
import com.bank.depositsmanagement.dao.CustomerRepository;
import com.bank.depositsmanagement.entity.*;
import com.bank.depositsmanagement.utils.CurrencyConstant;
import com.bank.depositsmanagement.utils.TimeConstant;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.text.DecimalFormat;

@Controller
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping({"employee/","employee/customer"})
    public String customerPage() {
        return "customer";
    }

    @GetMapping("employee/customer/add")
    public String addCustomerPage(Model model) {

        Customer customer = new Customer();
        model.addAttribute("customer", customer);

        return "add-customer";
    }

    @PostMapping("employee/customer/add")
    public String addCustomer(Model model, @ModelAttribute("customer") @Valid Customer customer, BindingResult bindingResult, Principal principal) {
        //check unique fields
        if (customerRepository.existsByIDCard(customer.getIDCard()))
            bindingResult.addError(new FieldError("customer","IDCard", "Số CCCD đã tồn tại"));
        if (customerRepository.existsByEmail(customer.getEmail()))
            bindingResult.addError(new FieldError("customer","email", "Email đã tồn tại"));

        if (bindingResult.hasErrors()) {
            model.addAttribute("customer",customer);
            return "add-customer";
        }

        Account account = (Account) ((Authentication) principal).getPrincipal();
        Employee employee = account.getEmployee();

        if (employee == null) {
            model.addAttribute("message", "Không tìm thấy thông tin của bạn");
            return "404";
        }

        customer.setLastModifiedBy(employee);
        customerRepository.save(customer);

        return "redirect:/employee/customer/detail?IDCard="+customer.getIDCard();
    }

    @GetMapping("employee/customer/detail")
    public String customerDetail(Model model, @RequestParam(value = "IDCard") String IDCard) {
        Customer customer = customerRepository.findByIDCard(IDCard).orElse(null);
        if (customer == null) {
            model.addAttribute("message", "Không tìm thấy khách hàng với số CCCD là " + IDCard);
            return "404";
        }
        model.addAttribute("customer", customer);
        model.addAttribute("readOnly", true);
        model.addAttribute("dateFormatter", TimeConstant.DATE_FORMATTER);
        model.addAttribute("vndFormatter", CurrencyConstant.VND_FORMATTER);
        model.addAttribute("usdFormatter", CurrencyConstant.USD_FORMATTER);
        return "customer-detail";
    }

    @PostMapping("employee/customer/update")
    public String updateCustomer(Model model, @ModelAttribute("customer") @Valid Customer customer, BindingResult bindingResult, Principal principal) {
        model.addAttribute("dateFormatter", TimeConstant.DATE_FORMATTER);
        Customer oldCustomer = customerRepository.findById(customer.getId()).orElse(null);

        if (oldCustomer == null) {
            model.addAttribute("message", "Không tìm thấy khách hàng với mã là " + customer.getId());
            return "404";
        }

        customer.setCreatedAt(oldCustomer.getCreatedAt());

        //check unique fields
        if (!customer.getIDCard().equals(oldCustomer.getIDCard())
                && customerRepository.existsByIDCard(customer.getIDCard()))
            bindingResult.addError(new FieldError("customer","IDCard", "Số CCCD đã tồn tại"));
        if (!customer.getEmail().equals(oldCustomer.getEmail())
                && customerRepository.existsByEmail(customer.getEmail()))
            bindingResult.addError(new FieldError("customer","email", "Email đã tồn tại"));

        if (bindingResult.hasErrors()) {
            customer.setLastModifiedAt(oldCustomer.getLastModifiedAt());
            customer.setLastModifiedBy(oldCustomer.getLastModifiedBy());
            model.addAttribute("customer",customer);
            model.addAttribute("readOnly",false);
            model.addAttribute("currencyFormatter",new DecimalFormat("#,###"));
            return "customer-detail";
        }

        Account account = (Account) ((Authentication) principal).getPrincipal();
        Employee employee = account.getEmployee();

        if (employee == null) {
            model.addAttribute("message", "Không tìm thấy thông tin của bạn");
            return "404";
        }

        customer.setLastModifiedBy(employee);

        customerRepository.save(customer);

        return "redirect:/employee/customer/detail?IDCard="+customer.getIDCard();
    }
}
