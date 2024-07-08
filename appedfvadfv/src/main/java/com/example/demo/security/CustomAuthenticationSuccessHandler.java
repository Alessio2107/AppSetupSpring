package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.demo.model.Login;
import com.example.demo.repository.LoginRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private LoginRepository loginRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean isAdmin = false;
        boolean isUser = false;

        request.getSession().setAttribute("username", authentication.getName());
        String username = authentication.getName();
        Login login = new Login(username, LocalDateTime.now());
        

        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                isAdmin = true;
                break;
            } else if (authority.getAuthority().equals("ROLE_USER")) {
                isUser = true;
            }
        }

        if (isAdmin) {
            response.sendRedirect("/admin/home");
        } else if (isUser) {
            response.sendRedirect("/customer/home");
            loginRepository.save(login);
        } else {
            response.sendRedirect("/login?error=true");
        }
    }
}
