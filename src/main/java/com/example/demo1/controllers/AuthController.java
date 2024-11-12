package com.example.demo1.controllers;

import com.example.demo1.models.User;
import com.example.demo1.repository.UserRepository;
import com.example.demo1.request.LoginRequest;
import com.example.demo1.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;


    @GetMapping("/verify")
    public String verifyUser(@RequestParam("code") String code) {
        if(userService.verifyEmail(code)) {
            return "users/verify_success";
        }else {
            return "users/verify_fail";
        }
    }

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
                              HttpSession session,
                              HttpServletRequest request) {

        try {

            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword());
            Authentication authentication = authenticationManager.authenticate(authRequest);


            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);

            HttpSession existingSession = request.getSession(false);
            if (existingSession != null) {
                existingSession.invalidate();
            }
            session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);


            model.addAttribute("loginRequest",loginRequest);
            return "redirect:/products";
        } catch (BadCredentialsException e) {
            model.addAttribute("loginErrorMessage", "Invalid email or password");
            return "users/login";
        }catch (DisabledException e) {
            model.addAttribute("loginErrorMessage", "Your account is not verified. Please check email.");
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
                                 HttpServletRequest request,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            return "users/register";
        }
        try {
            userService.register(user,getSiteURL(request));
            return "redirect:/auth/login";
        } catch (Exception e) {
            model.addAttribute("registerErrorMessage", e.getMessage());
            return "users/register";
        }
    }

    @GetMapping("/reset_password")
    public String resetPassword(Model model) {
        model.addAttribute("user",new User());
        return "users/forgot_password";
    }

    @PostMapping("/reset_password")
    public String handleResetPassword(Model model,
                                      @ModelAttribute("user") User user,
                                      HttpServletRequest request) {
        try{
            userService.forgotPassword(user.getEmail(),getSiteURL(request));
            return "redirect:/home";
        }catch (Exception e) {
            model.addAttribute("forgotPasswordErrorMessage",e.getMessage());
            return "users/forgot_password";
        }
    }

    @GetMapping("/update_password")
    public String updatePassword(Model model) {
        model.addAttribute("user",new User());
        return "users/update_password";
    }

    @PostMapping("/update_password")
    public String handleUpdatePassword(Model model,
                                       @ModelAttribute("user") User user,
                                       @RequestParam("token") String token) {
        try {
            userService.setPassword(user,token);
            return "redirect:/auth/login";
        }catch (Exception e) {
            model.addAttribute("updatePasswordErrorMessage",e.getMessage());
            return "users/update_password";
        }
    }



    private String getSiteURL(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();

        String siteURL = scheme + "://" + serverName;

        if ((scheme.equals("http") && serverPort != 80) ||
                (scheme.equals("https") && serverPort != 443)) {
            siteURL += ":" + serverPort;
        }

        return siteURL;
    }

}
