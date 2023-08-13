package ru.vavilov.notebook6.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.vavilov.notebook6.entity.User;
import ru.vavilov.notebook6.service.RoleService;
import ru.vavilov.notebook6.service.UserService;
import ru.vavilov.notebook6.util.UserValidator;

@Controller
public class AuthController {

    private final UserService userService;
    private final UserValidator userValidator;
    private final BCryptPasswordEncoder encoder;
    private final RoleService roleService;

    @Autowired
    public AuthController(UserService userService, UserValidator userValidator, BCryptPasswordEncoder encoder, RoleService roleService) {
        this.userService = userService;
        this.userValidator = userValidator;
        this.encoder = encoder;
        this.roleService = roleService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            request.getSession().invalidate();
        }
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        user.setRole(roleService.getRoleById(1));
        user.setPassword(encoder.encode(user.getPassword()));
        userService.saveUser(user);
        return "redirect:/user";
    }
}
