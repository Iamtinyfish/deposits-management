package com.bank.depositsmanagement.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;

@Controller
public class MainController {

    @RequestMapping(value = {"/","/login"}, method = RequestMethod.GET)
    public String loginPage(Model model, Principal principal) {

        if (principal != null) return "my-profile";

        return "login";
    }

    @RequestMapping(value = {"/admin"}, method = RequestMethod.GET)
    public String adminPage(Model model, Principal principal) {

        User loginedUser = (User) ((Authentication) principal).getPrincipal();

        model.addAttribute("user", loginedUser);

        return "admin";
    }

    @RequestMapping(value = "/my-profile", method = RequestMethod.GET)
    public String myProfilePage(Model model, Principal principal) {

        // Sau khi user login thanh cong se co principal
//        String username = principal.getName();
//
//        System.out.println("Username: " + username);

        User loginedUser = (User) ((Authentication) principal).getPrincipal();

        model.addAttribute("user", loginedUser.toString());

        return "my-profile";
    }

    @RequestMapping(value = "/403", method = RequestMethod.GET)
    public String accessDenied(Model model, Principal principal) {

        if (principal != null) {
            User loginedUser = (User) ((Authentication) principal).getPrincipal();

            model.addAttribute("user", loginedUser);

            String message = "Hi " + principal.getName() //
                    + "<br> You do not have permission to access this page!";
            model.addAttribute("message", message);
        }

        return "403";
    }
}
