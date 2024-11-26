package com.example.demo1.controllers;

import com.example.demo1.models.CustomerUserDetails;
import com.example.demo1.models.Role;
import com.example.demo1.models.User;
import com.example.demo1.repository.UserRepository;
import com.example.demo1.services.NotificationService;
import com.example.demo1.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;



    @GetMapping("")
    public String getAllUsers(Model model, @RequestParam(name = "page",defaultValue = "1") int page) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomerUserDetails customerUserDetails = (CustomerUserDetails) authentication.getPrincipal();
        Page<User> users = userService.getAllUsers(page);
        model.addAttribute("currentUserEmail",customerUserDetails.getUsername());
        model.addAttribute("users",users);
        model.addAttribute("totalPages",users.getTotalPages());
        model.addAttribute("currentPage",page);

        model.addAttribute("notifications",notificationService.getAllNotification());

        return "users/list";
    }

    @GetMapping("/{id}")
    public String activeUser(Model model,
                             HttpServletRequest request,
                             @PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            userService.activeUser(id);
            return "redirect:/admin/users";
        }catch (Exception e) {
            return "users/list";
        }
    }

    @GetMapping("/changeRole/{id}")
    public String changeRole(Model model, @PathVariable Long id) {
        try {
            userService.updateRole(id);
            return "redirect:/admin/users";
        }catch (Exception e) {
            return "redirect:/admin/users";
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
        model.addAttribute("notifications",notificationService.getAllNotification());
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
