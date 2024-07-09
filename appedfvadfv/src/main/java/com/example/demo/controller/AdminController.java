package com.example.demo.controller;

import com.example.demo.model.Customer;
import com.example.demo.model.Login;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.LoginRepository;
import com.example.demo.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/admin")
public class AdminController {
    private static final Logger logger = Logger.getLogger(AdminController.class.getName());

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private LoginRepository loginRepository;

    @GetMapping("/home")
    public String adminHome(Model model) {
        int userCount = countOfUsers(); 
        model.addAttribute("userCount", userCount);
        return "adminHome";
    }

    @GetMapping("/addUser")
    public String showAddUserForm() {
        return "addUser";
    }

    @PostMapping("/addUser")
    public String addUser(@RequestParam String email, @RequestParam String pwd, @RequestParam String role, Model model) {
        logger.info("Adding new user: " + email);
        boolean isAdded;

        Optional<Customer> existingCustomer = customerRepository.findByEmail(email).stream().findFirst();
        if (existingCustomer.isPresent()) {
            model.addAttribute("error", "Email is already registered!");
            return "addUser";
        }

        String encodedPassword = passwordEncoder.encode(pwd);
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setPwd(encodedPassword);
        customer.setRole(role);
        customerRepository.save(customer);

        if (customerRepository.findByEmail(email).stream().findFirst().isPresent()) {
            isAdded = true;
        } else {
            isAdded = false;
        }

        if (isAdded) {
            model.addAttribute("successMessage", "User added successfully!");
        } else {
            model.addAttribute("successMessage", "Failed to add user.");
        }

        return "addUser";
    }

    @GetMapping("/users")
    public String showAllUsers(Model model) {
        List<Customer> customers = customerRepository.findAll();
        model.addAttribute("customers", customers);
        return "showUsers";
    }

    @PostMapping("/deleteUser")
    public String deleteUser(@RequestParam Long id, Model model) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
            customerRepository.deleteById(id);
            model.addAttribute("successMessage", "User deleted successfully!");
        } else {
            model.addAttribute("errorMessage", "User not found!");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/settings")
    public String showSettingsPage() {
        return "settings";
    }

    @PostMapping("/updatePassword")
    public String updatePassword(@RequestParam String email, @RequestParam String oldPwd, @RequestParam String newPwd, Model model) {
        Optional<Customer> customerOpt = customerRepository.findByEmail(email).stream().findFirst();
        if (customerOpt.isPresent() && passwordEncoder.matches(oldPwd, customerOpt.get().getPwd())) {
            Customer customer = customerOpt.get();
            customer.setPwd(passwordEncoder.encode(newPwd));
            customerRepository.save(customer);
            model.addAttribute("successMessage", "Password updated successfully!");
        } else {
            model.addAttribute("errorMessage", "Invalid email or old password!");
        }
        return "settings";
    }

    @GetMapping("/countUsers")
    public int countOfUsers() {
        List<Customer> customers = customerRepository.findAll().stream()
        .filter(k->k.getRole().equalsIgnoreCase("ROLE_USER"))
        .collect(Collectors.toList());
        return customers.size();
    }

    @GetMapping("/recentLogins")
    public String showRecentLogins(Model model) {
        List<Login> recentLogins = loginRepository.findTop10ByOrderByLoginTimeDesc();
        model.addAttribute("recentLogins", recentLogins);
        return "recentLogins";
    }

    @DeleteMapping("/deleteLogin")
    @ResponseBody
    public ResponseEntity<String> deleteLogin(@RequestParam Long id) {
        loginRepository.deleteById(id);
        return ResponseEntity.ok("Login deleted");
    }

}
