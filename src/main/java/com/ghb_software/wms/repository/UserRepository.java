package com.ghb_software.wms.repository;

import com.ghb_software.wms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findOneByEmail(String userName);

    List<User> findByGroups_IdAndCustomerIdOrderByEmail(long groupId, long customerId);

    List<User> findAllByCustomerId(long customerId);

    List<User> findByIdIn(List<Long> usersToAdd);
}
