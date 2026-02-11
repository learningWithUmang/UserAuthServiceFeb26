package dev.umang.userauthservice.services;

import dev.umang.userauthservice.models.User;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService{

    @Override
    public User signup(String name, String email, String password) {
        return null;
    }

    @Override
    public User login(String email, String password) {
        return null;
    }
}
