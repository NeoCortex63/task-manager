package com.neocortex.taskmanager.config;

import com.neocortex.taskmanager.service.UserService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class UserServiceTestConfiguration {

    @Bean
    public UserService userService() { return new UserService(); }
}
