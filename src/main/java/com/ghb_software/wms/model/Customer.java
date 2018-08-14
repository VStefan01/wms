package com.ghb_software.wms.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class Customer extends BaseEntity{


    @Column(nullable = false)
    private String name;

    private String address;

    private String city;

    private String state;

    private String zipCode;

    private String country;

    private String vatCode;
}
