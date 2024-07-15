package com.example.demo;

import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "admin@example.com";
        String adminPassword = "pwd";
        String adminRole = "ROLE_ADMIN";

        Optional<Customer> existingAdmin = customerRepository.findByEmail(adminEmail).stream().findFirst();
        if (!existingAdmin.isPresent()) {
            Customer admin = new Customer();
            admin.setEmail(adminEmail);
            admin.setPwd(passwordEncoder.encode(adminPassword));
            admin.setRole(adminRole);
            admin.setName("Admin");
            admin.setId(1L);
            
            customerRepository.save(admin);
            System.out.println("Admin user created! ");
            System.out.println("Try logging in with email: " + adminEmail + " and password: " + adminPassword);
        } else {
            
        }
    }
}
