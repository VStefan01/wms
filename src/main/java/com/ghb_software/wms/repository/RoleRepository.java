package com.ghb_software.wms.repository;

import com.ghb_software.wms.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findByNameIn(List<String> roleNames);
}
