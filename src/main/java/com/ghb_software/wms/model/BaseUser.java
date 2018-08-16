package com.ghb_software.wms.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;


@Getter
@Setter
@MappedSuperclass
public abstract class BaseUser extends BaseEntity {
    @NotNull
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phoneNumber;

    private boolean deleted;

    @Column(name = "last_login")
    private Date lastLogin;

}
