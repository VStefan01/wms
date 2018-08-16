package com.ghb_software.wms.repository;

import com.ghb_software.wms.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
}
