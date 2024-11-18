package com.example.demo1.services;

import com.example.demo1.models.User;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    public void register(User user,String siteURL) throws Exception;
    public boolean verifyEmail(String verificationCode);
    public void forgotPassword(String email,String siteURL) throws Exception;
    public void setPassword(String password,String token) throws Exception;
    public Page<User> getAllUsers(int pageNumber);
    public User activeUser(Long id) throws Exception;
    public void createAccountAdmin(User user) throws Exception;
    public User getUserById(Long id) throws Exception;
    public void updateRole(Long id) throws Exception;
}
