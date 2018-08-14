package com.ghb_software.wms.view.vo;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class GroupVO {

    private String id;

    private String name;

    private String description;

    private List<String> roles;

    private boolean applicationGroup;
}
