package com.neocortex.taskmanager.domain;

import com.neocortex.taskmanager.exceptions.WrongDateException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id()
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotBlank
    @Size(min = 2, max = 25, message = "Name should be between 2 and 25 characters")
    private String username;

    @Column
    @Size(min = 5, message = "Password should be between 5 and 25 characters")
    private String password;

    @Email(message = "Email is invalid")
    private String email;

    @Column
    private String activationCode;
    @Column
    public boolean active;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;


    @OneToMany(mappedBy = "user",fetch = FetchType.EAGER,cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;


    public User() {
    }

    public User(@NotBlank @Size(min = 2, max = 25, message = "Name should be between 2 and 25 characters") String username,
                @Size(min = 5, max = 25, message = "Password should be between 5 and 25 characters") String password,
                Set<Role> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }
    @Override
    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }


    public void addTask(Task task) throws WrongDateException {

        if(task.getDueDate() == null || task.getDueDate().isBefore(LocalDate.now())) {
            throw  new WrongDateException("Please enter a correct Date");}

        if( tasks == null){
            tasks= new ArrayList<>();
        }
        task.setTaskStatus(TaskStatus.IN_PROGRESS);
        task.setUser(this);
        tasks.add(task);
    }

    public void deleteTask(int id){
        Optional<Task> taskOptional = tasks.stream().filter(task1 -> task1.getId() == id).findFirst();

        if (taskOptional.isPresent()){
            Task task = taskOptional.get();
            task.setUser(null);
            tasks.remove(task);
        }

    }

    public boolean isAdmin(){
        return roles.contains(Role.ADMIN);
    }

    public void activate() {
        activationCode = null;
        active = true;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    @Override
    public String toString() {
        return "User{" +
                ", username='" + username + '\'' +
                ", roles=" + roles +
                ", tasks=" + tasks +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id) && username.equals(user.username) && password.equals(user.password) && Objects.equals(roles, user.roles) && Objects.equals(tasks, user.tasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, roles, tasks);
    }

}
