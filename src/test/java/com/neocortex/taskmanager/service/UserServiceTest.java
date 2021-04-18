package com.neocortex.taskmanager.service;

import com.neocortex.taskmanager.config.UserServiceTestConfiguration;
import com.neocortex.taskmanager.domain.Role;
import com.neocortex.taskmanager.domain.User;
import com.neocortex.taskmanager.exceptions.UserAlreadyExistException;
import com.neocortex.taskmanager.exceptions.WrongPasswordException;
import com.neocortex.taskmanager.repositories.UserRepository;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(UserServiceTestConfiguration.class)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private MailSender mailSender;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    public void createNewUserTest() throws Exception {
        User user = new User();

        user.setPassword("password");

        user.setEmail("test@email.com");
        userService.createNewUser(user,"password");

        assertNotNull(user.getActivationCode());
        assertTrue(CoreMatchers.is(user.getRoles()).matches(Collections.singleton(Role.USER)));
        assertEquals(user.getPassword(),passwordEncoder.encode("password"));

        Mockito.verify(userRepository, Mockito.times(1)).save(user);
        Mockito.verify(mailSender, Mockito.times(1))
                .send(
                        ArgumentMatchers.eq(user.getEmail()),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString()
                );
    }

    @Test(expected = UserAlreadyExistException.class)
    public void addUserThatAlreadyExistTest() throws Exception {
        User user = new User();

        user.setUsername("user");

        user.setPassword("password");

        Mockito.doReturn(new User())
                .when(userRepository)
                .findUserByUsername("user");

        userService.createNewUser(user,"password");

        Mockito.verify(userRepository, Mockito.times(0)).save(ArgumentMatchers.any(User.class));
        Mockito.verify(mailSender, Mockito.times(0))
                .send(
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString()
                );
    }

    @Test(expected = WrongPasswordException.class)
    public void addUserWithDifferentPasswordTest() throws Exception {
        User user = new User();

        user.setUsername("user");

        user.setPassword("password");

        userService.createNewUser(user,"bad-password");

        Mockito.verify(userRepository, Mockito.times(0)).save(ArgumentMatchers.any(User.class));
        Mockito.verify(mailSender, Mockito.times(0))
                .send(
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString()
                );
    }

    @Test
    public void activateUser() {
        User user = new User();

        user.setActivationCode("activate");

        Mockito.doReturn(user)
                .when(userRepository)
                .findByActivationCode("activate");

        userService.activateUser("activate");

        assertTrue(user.isEnabled());
        assertNull(user.getActivationCode());

        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    public void activateUserFailTest() {
        userService.activateUser("activate me");

        Mockito.verify(userRepository, Mockito.times(0)).save(ArgumentMatchers.any(User.class));
    }
}

