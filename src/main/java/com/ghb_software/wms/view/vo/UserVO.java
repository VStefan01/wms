package com.ghb_software.wms.view.vo;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class UserVO {

    private String id;

    private String name;

    private String email;

    private Date lastActive;

    private List<String> groups;
}
