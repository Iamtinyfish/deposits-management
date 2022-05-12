package com.bank.depositsmanagement.controller;

import com.bank.depositsmanagement.dao.AccountRepository;
import com.bank.depositsmanagement.dao.EmployeeRepository;
import com.bank.depositsmanagement.entity.Account;
import com.bank.depositsmanagement.entity.Employee;
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
import java.security.Principal;

@Controller
public class MainController {

    private final EmployeeRepository employeeRepository;

    public MainController(AccountRepository accountRepository, EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping({"/","/login"})
    public String loginPage(Model model, Principal principal) {

        if (principal != null) {
            return "redirect:/my-profile";
        }

        return "login";
    }

    @GetMapping("/admin")
    public String adminPage(Model model) {

        return "admin";
    }

    @GetMapping("/my-profile")
    public String employeePage(Model model, Principal principal) {

        Account account = (Account) ((Authentication) principal).getPrincipal();
        Employee employee = account.getEmployee();

        if (employee == null) {
            model.addAttribute("message", "Không tìm thấy thông tin của bạn");
            return "404";
        }

        model.addAttribute("employee", employee);
        model.addAttribute("dateFormatter", TimeConstant.DATE_FORMATTER);
        model.addAttribute("readOnly", true);

        return "my-profile";
    }

    @PostMapping("/my-profile/update")
    public String updateMyProfile(Model model,@ModelAttribute("employee") @Valid Employee employee, BindingResult bindingResult, Principal principal) {
        model.addAttribute("dateFormatter", TimeConstant.DATE_FORMATTER);

        Account account = (Account) ((Authentication) principal).getPrincipal();
        Employee oldEmployee = account.getEmployee();

        if (oldEmployee == null) {
            model.addAttribute("message", "Không tìm thấy thông tin của bạn");
            return "404";
        }

        employee.setId(oldEmployee.getId());
        employee.setCreatedAt(oldEmployee.getCreatedAt());
        employee.setStatus(oldEmployee.getStatus());
        employee.setPosition(oldEmployee.getPosition());

        //check unique fields
        if (!employee.getIDCard().equals(oldEmployee.getIDCard())
                && employeeRepository.existsByIDCard(employee.getIDCard()))
            bindingResult.addError(new FieldError("employee","IDCard", "Số CCCD đã tồn tại"));
        if (!employee.getEmail().equals(oldEmployee.getEmail())
                && employeeRepository.existsByEmail(employee.getEmail()))
            bindingResult.addError(new FieldError("employee","email", "Email đã tồn tại"));

        if (bindingResult.hasErrors()) {
            employee.setLastModifiedAt(oldEmployee.getLastModifiedAt());
            model.addAttribute("employee", employee);
            model.addAttribute("readOnly", false);
            return "my-profile";
        }

        employee.setAccount(oldEmployee.getAccount());
        employeeRepository.save(employee);

        return "redirect:/my-profile";
    }

    @GetMapping("/403")
    public String accessDenied(Model model, Principal principal) {
        if (principal != null) {
            String message = "Hi " + principal.getName() //
                    + "<br> You do not have permission to access this page!";
            model.addAttribute("message", message);
            return "403";
        } else
            return "login";
    }
}
