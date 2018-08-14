package com.ghb_software.wms.view;


import com.ghb_software.wms.model.User;
import com.ghb_software.wms.service.GroupService;
import com.ghb_software.wms.service.UserService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.context.annotation.RequestScope;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Named
@RequestScope
public class UserRegistration {

    @Getter
    @Setter
    private User user = new User();

    @Getter
    @Setter
    private List<SelectItem> groups;

    @Getter
    @Setter
    private String[] selectedGroups;

    private final UserService userService;

    private final GroupService groupService;

    @Inject
    public UserRegistration(UserService userService, GroupService groupService) {
        this.userService = userService;
        this.groupService = groupService;
    }

    @PostConstruct
    public void init() {
        groups = groupService.getAvailableGroups().stream().map(group -> new SelectItem(group.getName(), group.getName())).collect(Collectors.toList());
    }

    public void registerUser() throws IOException {
        userService.registerUser(user, Arrays.asList(this.selectedGroups));
        FacesContext.getCurrentInstance().getExternalContext().redirect("/users");
    }
}
