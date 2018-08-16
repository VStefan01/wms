package com.ghb_software.wms.model;


import com.ghb_software.wms.util.WmsConstants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "APP_USER")
public class User extends BaseUser {


    @Column(nullable = false, name = "password")
    private String password;

    @Column(name = "active")
    private boolean active;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "users")
    private Set<Group> groups = new HashSet<>();

    private String language;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private String serializedRoles() {
        String rolesString = WmsConstants.EMPTY_STRING;
        for (Group group : groups) {
            for (Role role : group.getRoles())
                if (!rolesString.contains(role.getName()))
                    rolesString += role.getName() + ",";
        }
        return rolesString.substring(0, rolesString.length());
    }

    public List<GrantedAuthority> authorities() {
        return !groups.isEmpty() ? AuthorityUtils.commaSeparatedStringToAuthorityList(serializedRoles()) : AuthorityUtils.NO_AUTHORITIES;
    }

}
