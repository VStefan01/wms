package com.ghb_software.wms.security;

import com.ghb_software.wms.model.User;
import com.ghb_software.wms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class WmsUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public WmsUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userEntity = userRepository.findOneByEmail(username);
        if (!userEntity.isPresent())
            throw new UsernameNotFoundException("User " + username + " not found.");

        User user = userEntity.get();
        user.setLastLogin(new Date());

        com.ghb_software.wms.security.UserDetails userDetails = new com.ghb_software.wms.security.UserDetails(username, null, !user.isDeleted(), true, true, user.isActive(), user.authorities());
        userDetails.setId(user.getId());
        userDetails.setLanguage(user.getLanguage());
        if (user.getCustomer() != null)
            userDetails.setCustomerId(user.getCustomer().getId());

        return userDetails;
    }
}