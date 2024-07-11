package com.example.demo.repository;

import com.example.demo.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByEmail(String username);

    @Query("SELECT u FROM Customer u WHERE " +
           "u.name IS NULL OR u.address IS NULL OR u.city IS NULL OR u.state IS NULL OR u.zip IS NULL OR u.phoneNumber IS NULL OR u.username IS NULL OR u.gender IS NULL")
    List<Customer> findCustomersWithIncompleteProfiles();
    
}
