package com.neocortex.taskmanager.service;

import com.neocortex.taskmanager.domain.*;
import com.neocortex.taskmanager.exceptions.EmailAlreadyTakenException;
import com.neocortex.taskmanager.exceptions.UserAlreadyExistException;
import com.neocortex.taskmanager.exceptions.WrongPasswordException;
import com.neocortex.taskmanager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UserService implements  UserDetailsService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private MailSender mailSender;

    private TaskStatusManager statusManager;

    public User createNewUser(User user, String password) throws WrongPasswordException,
            UserAlreadyExistException,EmailAlreadyTakenException {


        if(!user.getPassword().equals(password)) throw new WrongPasswordException("Passwords are different");

        if(userRepository.findUserByUsername(user.getUsername()) != null) throw new UserAlreadyExistException("User with the same name already exist");

        if(userRepository.findUserByEmail(user.getEmail()) != null) throw new EmailAlreadyTakenException("Email has already been taken");

        user.setPassword(passwordEncoder.encode(password));

        user.setRoles(Collections.singleton(Role.USER));

        user.setActivationCode(UUID.randomUUID().toString());

        StringBuilder sb = new StringBuilder().append("Hello, ")
                .append(user.getUsername())
                .append("\n")
                .append("Welcome to Task Manager. Please visit the next link:\n")
                .append("http://localhost:8080/activate/")
                .append(user.getActivationCode());

        mailSender.send(user.getEmail(), "Activation code",sb.toString());

        return  userRepository.save(user);


    }

    public void deleteUser(long id){
        userRepository.deleteById(id);
    }

    public void updateUser(User user){
        userRepository.save(user);

        Optional<User> userFromDbOptional = userRepository.findById(user.getId());

        if(userFromDbOptional.isPresent()){
            User userFromDb = userFromDbOptional.get();

            user.setTasks(userFromDb.getTasks());
        }

    }
    public void activateUser(String code) {
        User user = userRepository.findByActivationCode(code);

        if(user != null) {
            user.activate();
            userRepository.save(user);
        }
    }
    public User getUser(long id) {
        Optional<User> userFromDbOptional = userRepository.findById(id);
        User user= new User();

        if(userFromDbOptional.isPresent()) user = userFromDbOptional.get();

        return user;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public List<Task> getCurrentTasks(User user) {
        return statusManager.getCurrentTasks(user);
    }

    public List<Task> getCompletedTasks(User user) {
        return statusManager.getCompletedTasks(user);
    }

    public List<Task> getExpiredTasks(User user) {
        return statusManager.getExpiredTasks(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username);
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    @Autowired
    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Autowired
    public void setStatusManager(TaskStatusManager statusManager) {
        this.statusManager = statusManager;
    }


}
