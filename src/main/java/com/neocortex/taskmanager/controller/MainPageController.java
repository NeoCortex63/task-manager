package com.neocortex.taskmanager.controller;

import com.neocortex.taskmanager.domain.*;
import com.neocortex.taskmanager.exceptions.WrongDateException;
import com.neocortex.taskmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.validation.Valid;
import java.time.LocalDate;


@Controller
public class MainPageController {

    UserService userService;

    TaskStatusManager statusManager;

    @GetMapping("/")
    public String redirectToUserPage(){
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String userPage(@AuthenticationPrincipal User user, Model model){

        if(!model.containsAttribute("task")) model.addAttribute("task",new Task());

        model.addAttribute("user",user);
        model.addAttribute("currentTasks", userService.getCurrentTasks(user));
        model.addAttribute("completedTasks", userService.getCompletedTasks(user));
        model.addAttribute("expiredTasks", userService.getExpiredTasks(user));

        return "index";

    }

    @PostMapping("/index")
    public String addNewTask(@AuthenticationPrincipal User user,
                             @ModelAttribute @Valid Task task,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes){

        if(bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.task",bindingResult);
            redirectAttributes.addFlashAttribute("task", task);
        }else {
            try {
                user.addTask(task);
                userService.updateUser(user);
            } catch (WrongDateException e) {
                redirectAttributes.addFlashAttribute("exceptionMessage",e.getMessage());
            }
        }

        return "redirect:/index";
    }

    @DeleteMapping ("index/{id}")
    public String deleteTask(@AuthenticationPrincipal User user,
                             @PathVariable("id") int id){
        user.deleteTask(id);
        userService.updateUser(user);

        return "redirect:/index";
    }


    @PostMapping ("index/{id}")
    public String completeTask(@AuthenticationPrincipal User user,
                               @PathVariable("id") int id){
        user.getTasks().stream()
                .filter(task -> task.getId()== id)
                .forEach(task -> {
                    task.setTaskStatus(TaskStatus.COMPLETED);
                    task.setDueDate(LocalDate.now());
                });

        userService.updateUser(user);

        return "redirect:/index";
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setStatusManager(TaskStatusManager statusManager) {
        this.statusManager = statusManager;
    }
}
