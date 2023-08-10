package ru.vavilov.notebook6.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.vavilov.notebook6.entity.User;
import ru.vavilov.notebook6.service.UserService;
import ru.vavilov.notebook6.util.UserValidator;

@Controller
public class RegisterConroller {

    UserService userService;
    UserValidator userValidator;
@Autowired
    public RegisterConroller(UserService userService, UserValidator userValidator) {
        this.userService = userService;
        this.userValidator = userValidator;
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user",new User());
        return "auth/register";
    }

    @PostMapping()
    public String create(@ModelAttribute("user")@Valid User user, BindingResult bindingResult) {
        userValidator.validate(user,bindingResult);
        if (bindingResult.hasErrors()){
            return "auth/register";
        }
        userService.saveUser(user);
        return "redirect:/user";
    }
}
