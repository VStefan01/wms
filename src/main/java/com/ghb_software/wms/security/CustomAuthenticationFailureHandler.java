package com.ghb_software.wms.security;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (exception.getClass().isAssignableFrom(BadCredentialsException.class))
            setDefaultFailureUrl("/login.html?failed");

        else if (exception.getClass().isAssignableFrom(AccountExpiredException.class))
            setDefaultFailureUrl("/login.html?inactive");

        else if(exception.getClass().isAssignableFrom(DisabledException.class))
            setDefaultFailureUrl("/changecredentials");

        super.onAuthenticationFailure(request, response, exception);
    }
}