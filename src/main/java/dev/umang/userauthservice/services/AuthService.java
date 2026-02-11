package dev.umang.userauthservice.services;

import dev.umang.userauthservice.exceptions.IncorrectPasswordException;
import dev.umang.userauthservice.exceptions.UserAlreadyExistException;
import dev.umang.userauthservice.exceptions.UserNotRegisteredException;
import dev.umang.userauthservice.models.Role;
import dev.umang.userauthservice.models.State;
import dev.umang.userauthservice.models.User;
import dev.umang.userauthservice.repositories.RoleRepo;
import dev.umang.userauthservice.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService implements IAuthService{
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Override
    public User signup(String name, String email, String password) {
        /*
        every user should register with a unique email
         */
        Optional<User> optionalUser = userRepo.findByEmail(email);

        if(optionalUser.isPresent()){
            throw new UserAlreadyExistException("Please try different email id");
        }

        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(password);
        user.setCreatedAt(new Date());
        user.setLastUpdatedAt(new Date());
        user.setState(State.ACTIVE);

        /*
        what else to set?
        Be default, user role is DEFAULT
         */
        Role role;
        Optional<Role> optionalRole = roleRepo.findByName("DEFAULT");

        if(optionalRole.isEmpty()){
            role = new Role();
            role.setName("DEFAULT");
            role.setCreatedAt(new Date());
            role.setLastUpdatedAt(new Date());
            role.setState(State.ACTIVE);
            roleRepo.save(role);
        }else{
            role = optionalRole.get();
        }

        List<Role> roles = new ArrayList<>();
        roles.add(role);

        user.setRoles(roles);

        return userRepo.save(user);
    }

    @Override
    public User login(String email, String password) {
        Optional<User> optionalUser = userRepo.findByEmail(email);

        if(optionalUser.isEmpty()){
            throw new UserNotRegisteredException("Please register first");
        }

        User user = optionalUser.get();
        if(password.equals(user.getPassword())){
            return user;
        }else{
            throw new IncorrectPasswordException("Incorrect password entered");
        }
    }
}

/*
store password in db in encrypted format

HW: BCryptPassword encoder in Java spring

Agenda for next class
Encode password and generate JWT and return
 */
