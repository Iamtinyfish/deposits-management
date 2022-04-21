package com.bank.depositsmanagement.controller;

import com.bank.depositsmanagement.dao.UserRepository;
import com.bank.depositsmanagement.entity.Employee;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
public class MainController {

    private final UserRepository userRepository;

    public MainController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping({"/","/login"})
    public String loginPage(Model model, Principal principal, HttpSession session) {

        if (principal != null) {
            return "my-profile";
        }

        return "login";
    }

    @GetMapping("/admin")
    public String adminPage(Model model) {

        return "admin";
    }

    @GetMapping("/my-profile")
    public String myProfilePage(Model model, Principal principal) {

        Employee employee = userRepository.findByUsername(principal.getName()).getEmployee();

        model.addAttribute("myProfile", employee.toString());

        return "my-profile";
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
