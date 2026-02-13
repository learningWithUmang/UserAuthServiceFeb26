package dev.umang.userauthservice.services;

import dev.umang.userauthservice.models.User;
import dev.umang.userauthservice.pojos.UserToken;
import org.antlr.v4.runtime.misc.Pair;

public interface IAuthService {
    User signup(String name, String email, String password);
    UserToken login(String email, String password);

    Boolean validateToken(String token);
}
