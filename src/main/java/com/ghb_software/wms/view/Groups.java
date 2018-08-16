package com.ghb_software.wms.view;


import com.ghb_software.wms.service.GroupService;
import com.ghb_software.wms.view.vo.GroupVO;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named
@RequestScoped
public class Groups {

    private final GroupService groupService;

    @Getter
    @Setter
    private List<GroupVO> groupVOList;

    @Inject
    public Groups(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostConstruct
    public void init() {
        groupVOList = groupService.getGroupsForCurrentCustomer();
    }

    public void deleteGroup(String id) {
        groupService.deleteGroup(id);
        groupVOList = groupService.getGroupsForCurrentCustomer();
    }
}
