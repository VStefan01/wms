package com.ghb_software.wms.view;

import com.ghb_software.wms.model.Customer;
import com.ghb_software.wms.model.User;
import com.ghb_software.wms.repository.CustomerRepository;
import com.ghb_software.wms.service.CustomerService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class Customers implements Serializable {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerService customerService;

    @Getter
    @Setter
    private Customer customer;

    @Getter
    @Setter
    private List<Customer> customerList;

    @Getter
    @Setter
    private User user;

    @PostConstruct
    public void init() {
        customer = new Customer();
        user = new User();
        customerList = customerRepository.findAll(new Sort(Sort.Direction.ASC, "name"));
    }

    public void addCustomer() {
        customerService.registerNewCustomer(customer, user);

        customerList = customerRepository.findAll(new Sort(Sort.Direction.ASC, "name"));
        customer = new Customer();
        user = new User();
    }


}
