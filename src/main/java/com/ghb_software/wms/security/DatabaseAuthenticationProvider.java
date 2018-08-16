package com.ghb_software.wms.security;

import com.ghb_software.wms.model.User;
import com.ghb_software.wms.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Optional;

/**
 * Created by Ghebo on 1/6/2016.
 */
@Service
public class DatabaseAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserRepository userRepository;


    @Override
    protected void additionalAuthenticationChecks(org.springframework.security.core.userdetails.UserDetails userDetails, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {

    }

    @Override
    protected UserDetails retrieveUser(String userName, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
        boolean valid = true;

        final String password = (String) usernamePasswordAuthenticationToken.getCredentials();
        if (!StringUtils.hasText(password)) {
            this.logger.warn("Username {}: no password provided", userName);
            valid = false;
        }

        Optional<User> user = userRepository.findOneByEmail(userName);

        if (!user.isPresent()) {
            this.logger.warn("Username {}: user not found", userName);
            valid = false;
        } else {
            // Check password
            if (new BCryptPasswordEncoder().matches(password, user.get().getPassword())) {
                if (user.get().isDeleted()) {
                    this.logger.warn("Username {}: locked", userName);
                    throw new AccountExpiredException("Account is locked for user " + userName);
                }
                if (!user.get().isActive()) {
                    this.logger.warn("Username {}: not active", userName);
                    throw new DisabledException("Account is not active for user " + userName);
                }
            } else {
                this.logger.warn("Username {}: bad password entered", userName);
                valid = false;
            }

        }

        if (!valid) {
            throw new BadCredentialsException("Invalid Username/Password for user " + userName);
        }

        User userEntity = user.get();
        userEntity.setLastLogin(new Date());
        userRepository.save(userEntity);
        // enabled, account not expired, credentials not expired, account not locked
        UserDetails userDetails = new UserDetails(userName, password, true, true, true, true, userEntity.authorities());
        userDetails.setId(userEntity.getId());
        userDetails.setLanguage(userEntity.getLanguage());
        if (userEntity.getCustomer() != null)
            userDetails.setCustomerId(userEntity.getCustomer().getId());
        return userDetails;
    }
}
