package dev.umang.userauthservice.pojos;

import dev.umang.userauthservice.models.User;
import org.antlr.v4.runtime.Token;

public class UserToken {
    private User user;
    private String token;


    public UserToken(User user, String token){
        this.user = user;
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public String getToken() {
        return  token;
    }


}
