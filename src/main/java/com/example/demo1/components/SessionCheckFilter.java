package com.example.demo1.components;

import com.example.demo1.models.CustomerUserDetails;
import com.example.demo1.models.User;
import com.example.demo1.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class SessionCheckFilter extends OncePerRequestFilter {

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private UserRepository userRepository;


    private boolean checkLocked(String username) {
        User user = userRepository.findByEmail(username);
        return user.isEnabled();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            String username = authentication.getName();
            List<Object> principals = sessionRegistry.getAllPrincipals();

            for (Object principal : principals) {
                if (principal instanceof CustomerUserDetails) {
                    CustomerUserDetails userDetails = (CustomerUserDetails) principal;

                    if (userDetails.getUsername().equals(username)) {
                        List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
                        for (SessionInformation session : sessions) {
                            if (!checkLocked(username)) {
                                session.expireNow();
                                sessionRegistry.removeSessionInformation(session.getSessionId());
                                Cookie cookie = new Cookie("JSESSIONID", null);
                                cookie.setPath("/");
                                cookie.setMaxAge(0);
                                response.addCookie(cookie);
                            }
                        }
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
