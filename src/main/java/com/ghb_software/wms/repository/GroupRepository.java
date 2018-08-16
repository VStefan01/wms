package com.ghb_software.wms.repository;

import com.ghb_software.wms.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group,Long>{
    Group findOneByName(String s);

    List<Group> findByApplicationGroupTrueOrCustomerIdOrderByNameAsc(long customerId);

    List<Group> findByNameIn(List<String> groupNames);
}
