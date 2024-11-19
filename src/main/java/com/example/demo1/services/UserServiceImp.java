package com.example.demo1.services;

import com.example.demo1.models.CustomerUserDetails;
import com.example.demo1.models.Role;
import com.example.demo1.models.User;
import com.example.demo1.repository.RoleRepository;
import com.example.demo1.repository.UserRepository;
import com.example.demo1.utils.PasswordGenerator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImp implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public Page<User> getAllUsers(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, 5,Sort.by("id").descending());
        return userRepository.findAll(pageable);
    }

    @Override
    public User activeUser(Long id) throws Exception {
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isEmpty()) {
            throw new Exception("User not found");
        }
        User user = userOptional.get();
        user.setEnabled(!user.isEnabled());
        return userRepository.save(user);

    }

    @Override
    public void createAccountAdmin(User user) throws Exception {
        User existingUserAdmin = userRepository.findByEmail(user.getEmail());
        if(existingUserAdmin != null) {
            throw new Exception("Email already exists");
        }
        User newUserAdmin = new User();
        newUserAdmin.setEmail(user.getEmail());
        newUserAdmin.setPhone(user.getPhone());
        newUserAdmin.setFirstName(user.getFirstName());
        newUserAdmin.setLastName(user.getLastName());

        Role userRole = roleRepository.findByName("ROLE_ADMIN");
        Role role = null;
        if (userRole == null) {
            role = new Role("ROLE_ADMIN");
        }
        newUserAdmin.setRoles(Arrays.asList(userRole != null ? userRole : role));

        newUserAdmin.setVerificationCode(null);
        newUserAdmin.setToken(null);
        newUserAdmin.setEnabled(true);
        newUserAdmin.setVerificationExpiryDate(null);

//      create random password and send with email
        String randomPassword = PasswordGenerator.generateRandomPassword(PasswordGenerator.PASSWORD_LENGTH);

        String toAddress = newUserAdmin.getEmail();
        String fromAddress = "kimhoando2003@gmail.com";
        String senderName = "Demo";
        String subject = "Your password";
        String content = "Dear [[name]],<br>" +
                "Please enter password below to login :<br>"
                + "Password: [[pass]]";
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        content = content.replace("[[name]]", newUserAdmin.getFirstName() + ' ' + newUserAdmin.getLastName());
        content = content.replace("[[pass]]", randomPassword);
        helper.setText(content, true);
        javaMailSender.send(message);
        newUserAdmin.setPassword(passwordEncoder.encode(randomPassword));


        userRepository.save(newUserAdmin);
    }

    @Override
    public User getUserById(Long id) throws Exception {
        return userRepository.findById(id).orElseThrow(() -> new Exception("User not found"));
    }

    @Override
    public void updateRole(Long id) throws Exception {
        User user = getUserById(id);
        Collection<Role> roles = user.getRoles();
        List<String> roleNames = roles.stream().map(role -> role.getName()).collect(Collectors.toList());
        if(roleNames.contains("ROLE_ADMIN")) {
            roles.clear();
//            Role existUserRole = roleRepository.findByName("ROLE_USER");
//            Role newUserRole = null;
//            if(existUserRole == null) {
//                newUserRole = new Role("ROLE_USER");
//            }
            Role role = new Role("ROLE_USER");
            roles.add(role);
            userRepository.save(user);
        }else {
            roles.clear();
            Role existAdminRole = roleRepository.findByName("ROLE_ADMIN");
            Role newAdminRole = null;
            if(existAdminRole == null) {
                newAdminRole = new Role("ROLE_ADMIN");
            }
            roles.add(existAdminRole != null ? existAdminRole : newAdminRole);
            userRepository.save(user);
        }
    }


    @Override
    public void register(User user,String siteURL) throws Exception {
        User isExistUser = userRepository.findByEmail(user.getEmail());
        if(isExistUser != null) {
            throw new Exception("Email already exists");
        }

        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setPhone(user.getPhone());
        newUser.setLastName(user.getLastName());
        newUser.setFirstName(user.getFirstName());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));

//        Role userRole = roleRepository.findByName("ROLE_USER");
//        Role role = null;
//        if (userRole == null) {
//            role = new Role("ROLE_USER");
//        }
        Role role = new Role("ROLE_USER");

        newUser.setRoles(Arrays.asList(role));

        String randomCode = UUID.randomUUID().toString();
        newUser.setVerificationCode(randomCode);
        newUser.setEnabled(false);

        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(15);
        newUser.setVerificationExpiryDate(expiryDate);

        userRepository.save(newUser);
        sendVerificationEmail(newUser, siteURL);
    }



    @Override
    public boolean verifyEmail(String verificationCode) {
        User user = userRepository.findByVerificationCode(verificationCode);
        if(user == null) {
            return false;
        } else if(user.getVerificationExpiryDate().isBefore(LocalDateTime.now())) {
            userRepository.deleteById(user.getId());
            return false;
        } else if(user.isEnabled()) {
            return false;
        }else {
            user.setVerificationCode(null);
            user.setEnabled(true);
            user.setVerificationExpiryDate(null);
            userRepository.save(user);
            return true;
        }
    }

    @Override
    public void forgotPassword(String email,String siteURL) throws Exception {
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new  Exception("Invalid Email");
        }
        String randomToken = UUID.randomUUID().toString();
        user.setToken(randomToken);
        userRepository.save(user);
        sendToken(user, siteURL);
    }

    @Override
    public void setPassword(String password,String token) throws Exception {
        User existingUser = userRepository.findByToken(token);
        if(existingUser == null) {
            throw new Exception("Invalid Token");
        }
        existingUser.setToken(null);
        existingUser.setPassword(passwordEncoder.encode(password));
        userRepository.save(existingUser);
    }



    private void sendToken(User user, String siteURL) throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "kimhoando2003@gmail.com";
        String senderName = "Demo";
        String subject = "Setup password";
        String content = "Dear [[name]],<br>" +
                "Please enter code below to setup password:<br>"
                + "Code change password: [[code]]";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getFirstName() + ' ' + user.getLastName());
//        String resetPasswordURL = siteURL + "/auth/update_password";
//        content = content.replace("[[URL]]", resetPasswordURL);

        content = content.replace("[[code]]", user.getToken());

        helper.setText(content, true);

        javaMailSender.send(message);
    }


    private void sendVerificationEmail(User user, String siteURL) throws Exception {
        String toAddress = user.getEmail();
        String fromAddress = "kimhoando2003@gmail.com";
        String senderName = "Demo";
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Demo.";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getFirstName() + ' ' + user.getLastName());
        String verifyURL = siteURL + "/auth/verify?code=" + user.getVerificationCode();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        javaMailSender.send(message);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if(user == null) {
            throw new UsernameNotFoundException("Invalid email or password.");
        }
        return new CustomerUserDetails(user);
    }

}
