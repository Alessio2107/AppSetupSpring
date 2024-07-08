package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Login {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private LocalDateTime loginTime;

    public Login() {}

    public Login(String username, LocalDateTime loginTime) {
        this.username = username;
        this.loginTime = loginTime;
    }
    


    /**
     * @return Long return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return String return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return LocalDateTime return the loginTime
     */
    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    /**
     * @param loginTime the loginTime to set
     */
    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

}
