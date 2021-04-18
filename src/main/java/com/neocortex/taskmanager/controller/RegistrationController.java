package com.neocortex.taskmanager.controller;

import com.neocortex.taskmanager.domain.User;
import com.neocortex.taskmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@Controller
public class RegistrationController {

    UserService userService;


    @GetMapping("/registration")
    public String newUserForm(Model model){

        model.addAttribute("user", new User());
        model.addAttribute("password",null);
        return "registration";
    }

    @PostMapping("/registration")
    public String createNewUser(@ModelAttribute("user") @Valid User user,
                                BindingResult bindingResult,
                                @RequestParam("confirmPassword") String confirmPassword,
                                Model model){

        if(bindingResult.hasErrors()) return "registration";
        try {
            userService.createNewUser(user,confirmPassword);
        }catch (Exception e){
            model.addAttribute("exceptionMessage", e.getMessage());
            return "registration";
        }

        return "redirect:/login";
    }

    @GetMapping("/activate/{code}")
    public String activate(@PathVariable("code") String code){

        userService.activateUser(code);

        return "login";
    }

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

}
