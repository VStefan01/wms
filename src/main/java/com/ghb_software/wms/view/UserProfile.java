package com.ghb_software.wms.view;


import com.ghb_software.wms.security.UserDetails;
import lombok.Setter;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.annotation.SessionScope;

import javax.inject.Named;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named(value = "userProfile")
@SessionScope
public class UserProfile implements Serializable {
    private static final long serialVersionUID = 1L;


    @Setter
    private String localeCode;

    private static Map<String, String> countries;

    static {
        countries = new LinkedHashMap<String, String>();
        countries.put("English", "en"); //label, value
        countries.put("Romana", "ro");
    }

    public String getLocaleCode() {
        if (isUserAuthenticated())
            return ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getLanguage();
        else
            return "en";
    }

    public Map<String, String> getCountriesInMap() {
        return countries;
    }

    public List<String> getRoles() {
        if (isUserAuthenticated())
            return ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getAuthorities().stream().map(grantedAuthority -> grantedAuthority.getAuthority()).collect(Collectors.toList());
        return Collections.emptyList();
    }

    private boolean isUserAuthenticated() {
        return !SecurityContextHolder.getContext().getAuthentication().getClass().equals(AnonymousAuthenticationToken.class);
    }

//    public String verifyToken(@RequestParam("token") Optional<String> token) {
//        if (!token.isPresent())
//            return VERIFICATION_TEMPLATE;
//
//        Optional<User> user = verificationTokenService.activateUserForToken(token.get());
//        return MessageFormat.format("redirect:/{0}?{1}", VERIFICATION_TEMPLATE, user.isPresent() ? "success" : "invalid");
//    }

}
