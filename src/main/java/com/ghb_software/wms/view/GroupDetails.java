package com.ghb_software.wms.view;


import com.ghb_software.wms.service.GroupService;
import com.ghb_software.wms.service.UserService;
import com.ghb_software.wms.view.vo.GroupDetailVO;
import com.ghb_software.wms.view.vo.UserVO;
import lombok.Getter;
import lombok.Setter;
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

@Named
@ViewScoped
public class GroupDetails {

    private final GroupService groupService;

    private final UserService userService;

    @Getter
    @Setter
    private GroupDetailVO groupDetailVO;

    @Getter
    @Setter
    private List<UserVO> customerUsers = new ArrayList<>();

    private ResourceBundle bundle;

    @Value("${hashid.salt}")
    private String salt;

    private Hashids hashids;

    private String groupIdHash;

    private long groupId;

    @Getter
    @Setter
    private List<UserVO> users = Collections.emptyList();

    @Inject
    public GroupDetails(GroupService groupService, UserService userService) {
        this.groupService = groupService;
        this.userService = userService;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        bundle = facesContext.getApplication().getResourceBundle(facesContext, "labels");
    }

    @PostConstruct
    public void init() {
        hashids = new Hashids(salt);
        groupIdHash = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("group");
        long[] ids = hashids.decode(groupIdHash);
        groupId = ids[0];
        groupDetailVO = groupService.getGroupDetails(groupId);
        customerUsers = userService.getAllCustomerUsers();
        customerUsers.removeAll(groupDetailVO.getUsers());
    }

    public void removeUser(String userIdHash) {
        long[] ids = hashids.decode(userIdHash);
        long userId = ids[0];
        groupService.removeUser(groupId, userId);
        groupDetailVO = groupService.getGroupDetails(groupId);
        customerUsers = userService.getAllCustomerUsers();
        customerUsers.removeAll(groupDetailVO.getUsers());
    }

    public List<UserVO> completeUser(String query) {
        List<UserVO> filteredUsers = new ArrayList<UserVO>();

        for (UserVO user : customerUsers) {
            if (user.getName().toLowerCase().startsWith(query.toLowerCase())) {
                filteredUsers.add(user);
            }
        }
        return filteredUsers;
    }

    public void addUsersToGroup() {
        if (users != null && !users.isEmpty()) {
            groupService.addUsersToGroup(users, groupId);
            groupDetailVO = groupService.getGroupDetails(groupId);
            users = Collections.emptyList();
        }
    }

    public void editGroup() {
        groupService.editGroup(groupDetailVO);
    }

    public void deleteGroup() throws IOException {
        groupService.deleteGroup(groupDetailVO.getId());
        FacesContext.getCurrentInstance().getExternalContext().redirect("/groups");
    }
}
