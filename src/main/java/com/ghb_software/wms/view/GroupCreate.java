package com.ghb_software.wms.view;


import com.ghb_software.wms.model.Group;
import com.ghb_software.wms.model.Role;
import com.ghb_software.wms.service.GroupService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.context.annotation.RequestScope;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Named
@RequestScope
public class GroupCreate {

    @Getter
    @Setter
    private Group group = new Group();

    @Getter
    @Setter
    private List<SelectItem> roles = new ArrayList<>();

    @Getter
    @Setter
    private String[] selectedRoles;

    private final GroupService groupService;

    private ResourceBundle bundle;

    @Inject
    public GroupCreate(GroupService groupService) {
        this.groupService = groupService;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        bundle = facesContext.getApplication().getResourceBundle(facesContext, "labels");
    }

    @PostConstruct
    public void init() {
        groupService.getRoleCategories().forEach(roleCategory -> {
            SelectItemGroup roleCategoryItem = new SelectItemGroup(bundle.getString(roleCategory.getName()));
            List<Role> categoryRoles = roleCategory.getRoles();
            SelectItem[] items = new SelectItem[categoryRoles.size()];
            for (int i = 0; i < items.length; i++)
                items[i] = new SelectItem(categoryRoles.get(i).getName(), bundle.getString(categoryRoles.get(i).getLabel()) + " - " + bundle.getString(categoryRoles.get(i).getDescription()));
            roleCategoryItem.setSelectItems(items);
            roles.add(roleCategoryItem);
        });
    }

    public void createGroup() throws IOException {
        groupService.createGroup(group, selectedRoles);
        FacesContext.getCurrentInstance().getExternalContext().redirect("/groups");
    }

}
