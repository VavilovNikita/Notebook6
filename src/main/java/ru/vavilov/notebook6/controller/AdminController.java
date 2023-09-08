package ru.vavilov.notebook6.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.vavilov.notebook6.entity.User;
import ru.vavilov.notebook6.service.AdminService;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping()
    public String allUserPage(Model model) {
        model.addAttribute("user", adminService.findAll());
        return "admin/allUserPage";
    }
    @GetMapping("/{id}")
    public String userInfo(@PathVariable("id") int id, Model model) {
        User user = adminService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("notes", user.getNotes());
        return "admin/userInfoPage";
    }
    @GetMapping("/{id}/edit")
    public String updateUser(Model model, @PathVariable("id") int id) {
        model.addAttribute("user", adminService.findById(id));
        return "admin/updateUserPage";
    }
    @PatchMapping("/{id}")
    public String update(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/updateUserPage";
        }
        adminService.saveUser(user);
        return "redirect:/admin";
    }
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable("id") int id) {
        adminService.deleteUser(id);
        return "redirect:/admin";
    }
}
