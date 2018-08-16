package com.ghb_software.wms.service;

import com.ghb_software.wms.model.User;
import com.ghb_software.wms.model.VerificationToken;
import com.ghb_software.wms.repository.UserRepository;
import com.ghb_software.wms.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VerificationTokenService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    public Optional<User> activateUserForToken(final String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findOneByToken(token);
        if (verificationToken.isPresent()) {
            User user = verificationToken.get().getUser();
            user.setDeleted(false);
            userRepository.save(user);
            return Optional.of(user);
        }

        return Optional.empty();
    }

}