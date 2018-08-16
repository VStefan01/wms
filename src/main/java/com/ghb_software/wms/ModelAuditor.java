package com.ghb_software.wms;

import com.ghb_software.wms.security.UserDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Created by alexandrugheboianu on 04.06.2017.
 */
public class ModelAuditor implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && !(auth.getPrincipal() instanceof String))
            return Optional.of(((UserDetails) auth.getPrincipal()).getUsername()); //get logged in username
        else
            return Optional.of("Anonymous");
    }
}