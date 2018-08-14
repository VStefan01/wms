package com.ghb_software.wms.service;


import com.ghb_software.wms.model.*;
import com.ghb_software.wms.repository.*;
import com.ghb_software.wms.security.UserDetails;
import com.ghb_software.wms.util.WmsConstants;
import com.ghb_software.wms.view.vo.GroupDetailVO;
import com.ghb_software.wms.view.vo.GroupVO;
import com.ghb_software.wms.view.vo.UserVO;
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private final GroupRepository groupRepository;

    private final RoleRepository roleRepository;

    private final RoleCategoryRepository roleCategoryRepository;

    private final CustomerRepository customerRepository;

    private final UserRepository userRepository;

    private Hashids hashids;

    @Value("${hashid.salt}")
    private String salt;


    @Autowired
    public GroupService(GroupRepository groupRepository,
                        RoleRepository roleRepository,
                        RoleCategoryRepository roleCategoryRepository,
                        CustomerRepository customerRepository,
                        UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.roleRepository = roleRepository;
        this.roleCategoryRepository = roleCategoryRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        hashids = new Hashids(salt);
    }

    public Group getAdminGroup() {
        return groupRepository.findOneByName(WmsConstants.adminGroupName);
    }

    public List<Group> getAvailableGroups() {
        return groupRepository.findByApplicationGroupTrueOrCustomerIdOrderByNameAsc(getPrincipal().getCustomerId());
    }

    @Transactional
    public void addUserToGroups(User user, List<String> groupNames) {
        List<Group> userGroups = groupRepository.findByNameIn(groupNames);
        userGroups.forEach(group -> {
            group.getUsers().add(user);
            groupRepository.saveAndFlush(group);
        });
    }


    public List<RoleCategory> getRoleCategories() {
        return roleCategoryRepository.findAll();
    }

    @Transactional
    public void createGroup(Group group, String[] selectedRoles) {
        group.setCustomer(getCurrentCustomer());
        group.getRoles().addAll(roleRepository.findByNameIn(Arrays.asList(selectedRoles)));
        groupRepository.saveAndFlush(group);
    }

    private Customer getCurrentCustomer() {
        return customerRepository.findById(getPrincipal().getCustomerId()).get();
    }

    private UserDetails getPrincipal() {
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public List<GroupVO> getGroupsForCurrentCustomer() {
        return getGroupsForCustomer(getCurrentCustomer()).stream().map(this::getGroupVO).collect(Collectors.toList());
    }

    private GroupVO getGroupVO(Group group) {
        GroupVO groupVO = new GroupVO();
        groupVO.setId(hashids.encode(group.getId()));
        groupVO.setName(group.getName());
        groupVO.setDescription(group.getDescription());
        groupVO.setApplicationGroup(group.isApplicationGroup());
        groupVO.setRoles(group.getRoles().stream().map(Role::getLabel).collect(Collectors.toList()));
        return groupVO;
    }

    private List<Group> getGroupsForCustomer(Customer customer) {
        return groupRepository.findByApplicationGroupTrueOrCustomerIdOrderByNameAsc(customer.getId());
    }

    @Transactional
    public GroupDetailVO getGroupDetails(long groupId) {
        Group group = groupRepository.getOne(groupId);
        GroupDetailVO detailVO = new GroupDetailVO();

        detailVO.setId(hashids.encode(group.getId()));
        detailVO.setDescription(group.getDescription());
        detailVO.setName(group.getName());
        detailVO.setApplicationGroup(group.isApplicationGroup());

        List<User> users =
                userRepository.findByGroups_IdAndCustomerIdOrderByEmail(groupId, getPrincipal().getCustomerId());
        detailVO.setMemberCount(users.size());

        List<UserVO> userVOList = users.stream().map(this::getUserVO).sorted(Comparator.comparing(UserVO::getEmail)).collect(Collectors.toList());
        detailVO.setUsers(userVOList);
        return detailVO;
    }

    private UserVO getUserVO(User user) {
        UserVO userVO = new UserVO();
        userVO.setId(hashids.encode(user.getId()));
        userVO.setEmail(user.getEmail());
        userVO.setLastActive(user.getLastLogin());
        userVO.setName(user.getName());
        return userVO;
    }

    @Transactional
    public void removeUser(long groupId, long userId) {
        Group group = groupRepository.getOne(groupId);
        group.getUsers().removeIf(user -> user.getId() == userId);
        groupRepository.saveAndFlush(group);
    }

    @Transactional
    public void addUsersToGroup(List<UserVO> users, long groupId) {
        Group group = groupRepository.getOne(groupId);
        List<Long> usersToAdd = users.stream().map(userVO -> hashids.decode(userVO.getId())[0]).collect(Collectors.toList());
        List<User> usersEntities = userRepository.findByIdIn(usersToAdd);

        group.getUsers().addAll(usersEntities);
        groupRepository.saveAndFlush(group);
    }

    @Transactional
    public void editGroup(GroupDetailVO groupDetailVO) {
        Group group = groupRepository.getOne(hashids.decode(groupDetailVO.getId())[0]);
        group.setDescription(groupDetailVO.getDescription());
        groupRepository.save(group);
    }

    @Transactional
    public void deleteGroup(String id) {
        Group group = groupRepository.getOne(hashids.decode(id)[0]);
        group.getUsers().clear();
        group.getRoles().clear();
        groupRepository.saveAndFlush(group);
        groupRepository.delete(group);
    }
}
