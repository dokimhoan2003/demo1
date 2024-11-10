package com.example.demo1.services;

import com.example.demo1.models.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    public void register(User user,String siteURL) throws Exception;
    public boolean verifyEmail(String verificationCode);

}
