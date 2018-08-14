package com.ghb_software.wms.view.vo;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupDetailVO {

    private String id;

    private String name;

    private String description;

    private int memberCount;

    private boolean applicationGroup;

    private List<UserVO> users;
}
