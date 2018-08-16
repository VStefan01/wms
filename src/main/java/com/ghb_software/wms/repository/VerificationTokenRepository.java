package com.ghb_software.wms.repository;

import com.ghb_software.wms.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findOneByToken(String token);
}
