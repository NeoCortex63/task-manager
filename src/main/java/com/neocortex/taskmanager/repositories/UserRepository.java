package com.neocortex.taskmanager.repositories;

import com.neocortex.taskmanager.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User,Long>  {

     User findUserByUsername(String name);

     User findUserByEmail(String email);

     User findByActivationCode(String code);
}
