package ru.vavilov.notebook6.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.vavilov.notebook6.entity.User;
import ru.vavilov.notebook6.service.AuthService;
import ru.vavilov.notebook6.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    @Autowired
    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping("/profile")
    public String getAuthUserInfo(Model model) {
        User user = userService.findById(authService.getUser().getId());
        model.addAttribute("authUser", user);
        model.addAttribute("notes", user.getNotes());
        return "notebook/userInfoPage";
    }

    @GetMapping("/{id}/edit")
    public String updateAuthUser(Model model) {
        model.addAttribute("authUser", userService.findById(authService.getUser().getId()));
        return "notebook/updateUserPage";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "notebook/updateUserPage";
        }
        user.setRole(authService.getUser().getRole());
        userService.saveUser(user);
        return "redirect:/user/profile";
    }



}
