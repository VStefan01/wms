package com.ghb_software.wms.service;

import com.ghb_software.wms.model.*;
import com.ghb_software.wms.repository.CustomerRepository;
import com.ghb_software.wms.repository.UserRepository;
import com.ghb_software.wms.repository.VerificationTokenRepository;
import com.ghb_software.wms.security.UserDetails;
import com.ghb_software.wms.view.vo.UserVO;
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    private final VerificationTokenRepository verificationTokenRepository;

    private final CustomerRepository customerRepository;

    private final EmailService emailService;

    private final GroupService groupService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private Hashids hashids;

    @Value("${hashid.salt}")
    private String salt;

    @Value("${user.activation.token-availability-hours}")
    private int tokenAvailabilityHours;


    @PostConstruct
    public void init() {
        hashids = new Hashids(salt);
    }

    @Autowired
    public UserService(UserRepository userRepository, VerificationTokenRepository verificationTokenRepository, CustomerRepository customerRepository, EmailService emailService, GroupService groupService) {
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.customerRepository = customerRepository;
        this.emailService = emailService;
        this.groupService = groupService;
    }

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findOneByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
        com.ghb_software.wms.security.UserDetails userDetails = new com.ghb_software.wms.security.UserDetails(user.getEmail(), user.getPassword(), user.authorities());
        userDetails.setId(user.getId());
        return userDetails;
    }

    public void delete(long id) {
        User user = userRepository.getOne(id);
        user.setActive(false);
        user.setDeleted(true);
        userRepository.save(user);
    }

    public boolean toggleActiveUserById(final long userId) {
        User user = userRepository.getOne(userId);
        user.setActive(!user.isActive());
        return userRepository.save(user).isActive();
    }

    public VerificationToken createVerificationTokenForUser(final User user) {
        VerificationToken token = new VerificationToken(user, tokenAvailabilityHours);
        return verificationTokenRepository.saveAndFlush(token);
    }

    public boolean emailAlreadyExists(String email) {
        Optional<User> incomingUser = userRepository.findOneByEmail(email);

        return incomingUser.isPresent();
    }

    @Transactional
    public void registerUser(User user, List<String> groups) {
        UserDetails userDetails = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Customer customer = customerRepository.getOne(userDetails.getCustomerId());
        String plainPassword = user.getPassword();

        user.setActive(false);
        user.setDeleted(true);
        user.setLanguage("en");
        user.setCustomer(customer);
        user.setPassword(passwordEncoder.encode(plainPassword));
        user = userRepository.save(user);

        groupService.addUserToGroups(user, groups);

        VerificationToken verificationTokenForUser = createVerificationTokenForUser(user);
        emailService.sendNewCustomerNotification(verificationTokenForUser, plainPassword);
    }

    @Transactional
    public List<UserVO> getAllCustomerUsers() {
        UserDetails userDetails = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        long customerId = userDetails.getCustomerId();
        return userRepository.findAllByCustomerId(customerId).stream().map(this::getUserVO).collect(Collectors.toList());
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
    public UserVO findUser(String id) {
        long userId = hashids.decode(id)[0];

        return getUserVO(userRepository.getOne(userId));
    }
}

