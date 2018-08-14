package com.ghb_software.wms.view;


import com.ghb_software.wms.service.UserService;
import com.ghb_software.wms.view.vo.UserVO;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named
@RequestScoped
public class Users {
    private final UserService userService;

    @Getter
    @Setter
    private List<UserVO> userVOList;

    @Inject
    public Users(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        userVOList = userService.getAllCustomerUsers();
    }
}
