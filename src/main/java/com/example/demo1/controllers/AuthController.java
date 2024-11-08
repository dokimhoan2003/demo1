package com.example.demo1.controllers;

import com.example.demo1.models.User;
import com.example.demo1.repository.UserRepository;
import com.example.demo1.request.LoginRequest;
import com.example.demo1.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;



    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/auth/login";
    }

    @GetMapping("/login")
    public String login(Model model) {
        LoginRequest loginRequest = new LoginRequest();
        model.addAttribute("loginRequest",loginRequest);

        return "users/login";
    }


    @PostMapping("/login")
    public String handleLogin(@ModelAttribute("loginRequest") LoginRequest loginRequest,
                              Model model,
                              HttpServletRequest request) {

        try {
            UserDetails userDetails = userService.loadUserByUsername(loginRequest.getEmail());
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword(),userDetails.getAuthorities());
            Authentication authentication = authenticationManager.authenticate(authRequest);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            model.addAttribute("loginRequest",loginRequest);
            return "redirect:/";
        } catch (BadCredentialsException e) {
            model.addAttribute("loginErrorMessage", "Invalid username or password");
            return "users/login";
        }

    }


    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "users/register";
    }

    @PostMapping("/register")
    public String handleRegister(@ModelAttribute("user") User user,
                                 BindingResult bindingResult,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            return "users/register";
        }
        try {
            userService.register(user);
            return "redirect:/auth/login";
        } catch (Exception e) {
            model.addAttribute("registerErrorMessage", e.getMessage());
            return "users/register";
        }
    }
}
