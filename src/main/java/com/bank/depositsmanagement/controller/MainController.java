package com.bank.depositsmanagement.controller;

import com.bank.depositsmanagement.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;
import java.util.Arrays;

@Controller
public class MainController {

    private final UserRepository userRepository;

    public MainController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping({"/","/login"})
    public String loginPage(Model model, Principal principal) {

        if (principal != null) return "my-profile";

        return "login";
    }

    @GetMapping("/admin")
    public String adminPage(Model model) {

        return "admin";
    }

    @GetMapping("/my-profile")
    public String myProfilePage(Model model) {

        // Sau khi user login thanh cong se co principal
//        User loggedUser = (User) ((Authentication) principal).getPrincipal();
//
//        model.addAttribute("user", loggedUser);

//        System.out.println(loggedUser.getAuthorities().toArray()[0]);

        return "my-profile";
    }

    @GetMapping("/user")
    public String userPage(Model model) {

        return "user";
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
