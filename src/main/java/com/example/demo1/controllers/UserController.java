package com.example.demo1.controllers;

import com.example.demo1.models.User;
import com.example.demo1.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public String getAllUsers(Model model, @RequestParam(name = "page",defaultValue = "1") int page) {
        Page<User> users = userService.getAllUsers(page);
        model.addAttribute("users",users);
        model.addAttribute("totalPages",users.getTotalPages());
        model.addAttribute("currentPage",page);
        return "users/list";
    }

    @GetMapping("/{id}")
    public String activeUser(Model model,
                             @PathVariable Long id) {
        try {
            userService.activeUser(id);
            return "redirect:/admin/users";
        }catch (Exception e) {
            return "users/list";
        }

    }
}
