package com.ghb_software.wms.service;

import com.ghb_software.wms.model.Customer;
import com.ghb_software.wms.model.Group;
import com.ghb_software.wms.model.User;
import com.ghb_software.wms.repository.CustomerRepository;
import com.ghb_software.wms.repository.UserRepository;
import com.ghb_software.wms.security.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collections;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    private final UserRepository userRepository;

    private final GroupService groupService;


    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    @Autowired
    public CustomerService(CustomerRepository customerRepository, UserRepository userRepository, GroupService groupService) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.groupService = groupService;
    }

    @Transactional
    public void registerNewCustomer(Customer customer, User user) {
        customer = customerRepository.save(customer);

        user.setActive(false);
        user.setLanguage(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getLanguage());
        user.setCustomer(customer);
        String password = newRandomPassword();
        user.setPassword(passwordEncoder.encode(password));
        System.out.println("### New user " + user.getEmail() + " created with temp password: " + password);
        user = userRepository.save(user);

        Group adminGroup = groupService.getAdminGroup();
        groupService.addUserToGroups(user, Collections.singletonList(adminGroup.getName()));

    }

    private String newRandomPassword() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            double index = Math.random() * characters.length();
            buffer.append(characters.charAt((int) index));
        }
        return buffer.toString();
    }
}
