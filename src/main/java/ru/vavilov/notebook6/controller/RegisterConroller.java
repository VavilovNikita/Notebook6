package ru.vavilov.notebook6.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.vavilov.notebook6.entity.User;
import ru.vavilov.notebook6.service.UserService;

@Controller
public class RegisterConroller {

    UserService userService;

    @Autowired
    public RegisterConroller(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user",new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registration(@ModelAttribute("user") User user) {
        userService.saveUser(user);
        return "redirect:/notebook";
    }
}
