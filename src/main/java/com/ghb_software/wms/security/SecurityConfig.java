package com.ghb_software.wms.security;

import com.ghb_software.wms.service.UserService;
import com.ghb_software.wms.util.WmsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final DatabaseAuthenticationProvider authenticationProvider;

    private final UserService userService;

    private final DataSource dataSource;

    private final WmsUserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(DatabaseAuthenticationProvider authenticationProvider, UserService userService, DataSource dataSource, WmsUserDetailsService userDetailsService) {
        this.authenticationProvider = authenticationProvider;
        this.userService = userService;
        this.dataSource = dataSource;
        this.userDetailsService = userDetailsService;
    }


    @Bean
    public RoleVoter roleVoter() {
        RoleVoter roleVoter = new RoleVoter();
        roleVoter.setRolePrefix(WmsConstants.EMPTY_STRING);
        return roleVoter;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**", "/css/**", "/img/**", "/vendors/**", "/javax.faces.resource/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/login**", "/register**", "/favicon.**", "/verification**", "/javax.faces.resource/**").permitAll()
                .antMatchers("/").authenticated()
                .antMatchers("/customers").hasAnyAuthority("SUPER_ADMIN")
                .antMatchers("/users").hasAnyAuthority("USERS_READ", "USERS_WRITE")
                .antMatchers("/groups/details", "/groups").hasAnyAuthority("GROUPS_READ", "GROUPS_WRITE")
                .antMatchers("/buildings").hasAnyAuthority("BUILDINGS_READ", "BUILDINGS_WRITE")
                .antMatchers("/users/add", "/users/modify").hasAnyAuthority("USERS_WRITE")
                .antMatchers("/groups/add").hasAnyAuthority("GROUPS_WRITE")
                .and().formLogin().loginPage("/login.html").failureHandler(new CustomAuthenticationFailureHandler()).defaultSuccessUrl("/")
                .and().logout().logoutSuccessUrl("/login.html?logout")
                .and().rememberMe().rememberMeParameter("remember-me").userDetailsService(userDetailsService)
                .tokenRepository(persistentTokenRepository()).tokenValiditySeconds(86400)
                .and().csrf().disable()
                .authenticationProvider(authenticationProvider).exceptionHandling().accessDeniedPage("/403.xhtml");
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepositoryImpl = new JdbcTokenRepositoryImpl();
        tokenRepositoryImpl.setDataSource(dataSource);
        return tokenRepositoryImpl;
    }
}
