package com.neocortex.taskmanager.controller;

import com.neocortex.taskmanager.domain.User;
import com.neocortex.taskmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    UserService userService;



    @GetMapping
    public String getAdminPanel(@AuthenticationPrincipal User user, Model model){
        List<User> allUsers = userService.getAllUsers();

        model.addAttribute("allUsers", allUsers);
        model.addAttribute("user", user);
        return "admin";
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable("id") Long id){
        userService.deleteUser(id);
        return "redirect:/admin";
    }

    @GetMapping("/{id}")
    public String getUpdate(@AuthenticationPrincipal User user,
                         @PathVariable("id") Long id, Model model){

        model.addAttribute("userToUpdate",userService.getUser(id));
        model.addAttribute("user",user);

        return "update";
    }

    @PatchMapping("/{id}")
    public String updateUser(@PathVariable("id") Long id,
                             @ModelAttribute("userToUpdate") User userToUpdate){

        User userFromDB = userService.getUser(id);
        userFromDB.setUsername(userToUpdate.getUsername());
        userFromDB.setEmail(userToUpdate.getEmail());

        userService.updateUser(userFromDB);

        return "redirect:/admin";
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
