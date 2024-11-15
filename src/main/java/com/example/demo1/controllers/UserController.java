package com.example.demo1.controllers;

import com.example.demo1.models.CustomerUserDetails;
import com.example.demo1.models.User;
import com.example.demo1.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/profile")
    public String userProfile (Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomerUserDetails customerUserDetails = (CustomerUserDetails) authentication.getPrincipal();
        model.addAttribute("fullName",customerUserDetails.getFullName());
        model.addAttribute("email",customerUserDetails.getUsername());
        model.addAttribute("phone",customerUserDetails.getPhone());
        return "users/profile";
    }

    @GetMapping("/createAdmin")
    public String createAccountAdmin(Model model) {
        User user = new User();
        model.addAttribute("user",user);
        return "users/create_admin";
    }

    @PostMapping("/createAdmin")
    public String handleCreateAccountAdmin(Model model, @ModelAttribute("user") User user) {
        try {
            userService.createAccountAdmin(user);
            return "redirect:/admin/users";
        }catch (Exception e) {
            model.addAttribute("createAdminErrorMessage",e.getMessage());
            return "users/create_admin";
        }
    }
}
