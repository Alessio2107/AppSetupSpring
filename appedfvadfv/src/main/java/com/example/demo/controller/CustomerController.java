package com.example.demo.controller;

import com.example.demo.model.Customer;
import com.example.demo.model.Login;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.LoginRepository;
import java.time.LocalDateTime;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@Controller
@RequestMapping("/customer")
public class CustomerController {
    private static final Logger logger = Logger.getLogger(CustomerController.class.getName());

    

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LoginRepository loginRepository;

    @PostMapping("/register")
    public ResponseEntity<String> registerCustomer(@RequestBody Customer customer) {
        logger.info("Registering new customer: " + customer.getEmail());

        if (customer.getEmail() == null || customer.getPwd() == null || customer.getRole() == null) {
            return ResponseEntity.badRequest().body("Email, password, and role are required!");
        }

        Optional<Customer> existingCustomer = customerRepository.findByEmail(customer.getEmail()).stream().findFirst();
        if (existingCustomer.isPresent()) {
            return ResponseEntity.badRequest().body("Email is already registered!");
        }

        String encodedPassword = passwordEncoder.encode(customer.getPwd());
        customer.setPwd(encodedPassword);
        customer.setRole(customer.getRole());
        customerRepository.save(customer);
        logger.info("Customer registered successfully with email: " + customer.getEmail() + " and encoded password: " + encodedPassword);
        System.out.println(encodedPassword);
        return ResponseEntity.ok("Customer registered successfully!");
    }

    @PostMapping("/perform_login")
    public ResponseEntity<?> performLogin(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        Optional<Customer> customerOpt = customerRepository.findByEmail(username).stream().findFirst();

        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();

            if (passwordEncoder.matches(password, customer.getPwd())) {
                logger.info("Login successful for user: " + username);
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("username", username);
                responseData.put("role", customer.getRole());
                return ResponseEntity.ok(responseData);
            } 
        } 
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody Map<String, String> passwordRequest) {
        String email = passwordRequest.get("email");
        String oldPwd = passwordRequest.get("oldPwd");
        String newPwd = passwordRequest.get("newPwd");

        Optional<Customer> customerOpt = customerRepository.findByEmail(email).stream().findFirst();
        if(oldPwd == null || newPwd == null){
            return ResponseEntity.badRequest().body("Old password and new password are required!");
        }else{
            if(oldPwd.equals(newPwd)){
                return ResponseEntity.badRequest().body("Old password and new password cannot be the same!");
            }else{
                if (customerOpt.isPresent() && passwordEncoder.matches(oldPwd, customerOpt.get().getPwd())) {
                    Customer customer = customerOpt.get();
                    customer.setPwd(passwordEncoder.encode(newPwd));
                    customerRepository.save(customer);
                    return ResponseEntity.ok("Password changed successfully!");
                }else{
                    return ResponseEntity.badRequest().body("Invalid email or old password!");
                }
            }
            
        }
        
        
    }


    @PostMapping("/recover-password")
    public ResponseEntity<String> recoverPassword(@RequestParam String email) {
        Optional<Customer> customerOpt = customerRepository.findByEmail(email).stream().findFirst();

        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            customer.setPwd(passwordEncoder.encode("password"));
            customerRepository.save(customer);
            return ResponseEntity.ok("Password recovery instructions sent to email!");
        }

        return ResponseEntity.badRequest().body("Email not found!");
    }

    @GetMapping("/getAll")
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @GetMapping("/admin/users")
    public String showAllUsers(Model model) {
        List<Customer> customers = customerRepository.findAll();
        model.addAttribute("customers", customers);

        return "users";
    }
    @GetMapping("/profile")
    public String showProfilePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = null;
    
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            email = ((UserDetails) authentication.getPrincipal()).getUsername();
            logger.info("User email from UserDetails: " + email);
        } else if (authentication != null && authentication.getPrincipal() instanceof String) {
            email = (String) authentication.getPrincipal();
            logger.info("User email from Principal: " + email);
        }
    
        if (email != null) {
            Optional<Customer> customerOpt = customerRepository.findByEmail(email).stream().findFirst();
    
            if (customerOpt.isPresent()) {
                Customer customer = customerOpt.get();
                model.addAttribute("customer", customer);
                logger.info("User found by email: " + email);
            } else {
                model.addAttribute("customer", null);
                model.addAttribute("error", "No customer found with the given email.");
                logger.warning("User not found by email: " + email);
            }
        } else {
            model.addAttribute("error", "Unable to retrieve user email.");
            logger.warning("Unable to retrieve user email from authentication object.");
        }
    
        return "customerProfile";
    }
    

    @GetMapping("/settings")
    public String showUserSetting(Model model) {
        return "customerSettings";
    }

}
