package com.bank.depositsmanagement.controller;

import com.bank.depositsmanagement.dao.EmployeeRepository;
import com.bank.depositsmanagement.dao.UserRepository;
import com.bank.depositsmanagement.entity.Employee;
import com.bank.depositsmanagement.entity.User;
import com.bank.depositsmanagement.utils.TimeConstant;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class MainController {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;

    public MainController(UserRepository userRepository, EmployeeRepository employeeRepository) {
        this.userRepository = userRepository;
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

        User user = userRepository.findByUsername(principal.getName()).orElse(null);

        if (user == null || user.getEmployee() == null) {
            model.addAttribute("message", "Không tìm thấy thông tin của bạn");
            return "404";
        }

        Employee employee = user.getEmployee();

        model.addAttribute("employee", employee);
        model.addAttribute("dateFormatter", TimeConstant.DATE_FORMATTER);
        model.addAttribute("readOnly", true);

        return "my-profile";
    }

    @PostMapping("/my-profile/update")
    public String updateMyProfile(Model model,@ModelAttribute("employee") @Valid Employee employee, BindingResult bindingResult, Principal principal) {
        model.addAttribute("dateFormatter", TimeConstant.DATE_FORMATTER);
        User user = userRepository.findByUsername(principal.getName()).orElse(null);

        if (user == null || user.getEmployee() == null) {
            model.addAttribute("message", "Không tìm thấy thông tin của bạn");
            return "404";
        }

        Employee oldEmployee = user.getEmployee();

        employee.setId(oldEmployee.getId());
        employee.setCreatedAt(oldEmployee.getCreatedAt());
        employee.setStatus(oldEmployee.getStatus());
        employee.setPosition(oldEmployee.getPosition());

        //check unique fields
        boolean doesIDCardExist = !employee.getIDCard().equals(oldEmployee.getIDCard()) && employeeRepository.existsByIDCard(employee.getIDCard());
        if (doesIDCardExist) model.addAttribute("errorIDCard", "Số CCCD đã tồn tại");
        boolean doesEmailExist = !employee.getEmail().equals(oldEmployee.getEmail()) && employeeRepository.existsByEmail(employee.getEmail());
        if (doesEmailExist) model.addAttribute("errorEmail", "Email đã tồn tại");

        if (bindingResult.hasErrors() || doesEmailExist || doesIDCardExist) {
            employee.setLastModifiedAt(oldEmployee.getLastModifiedAt());
            model.addAttribute("employee", employee);
            model.addAttribute("readOnly", false);
            return "my-profile";
        }

        employee.setUser(oldEmployee.getUser());
        employeeRepository.save(employee);

        return "redirect:/my-profile";
    }

//    @GetMapping("/user")
//    public String userPage(Model model) {
//
//        return "user";
//    }

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
