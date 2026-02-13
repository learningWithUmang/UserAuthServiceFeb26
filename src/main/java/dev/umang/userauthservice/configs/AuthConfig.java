package dev.umang.userauthservice.configs;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class AuthConfig {
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //Disable csrf
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity.csrf().disable();
//        httpSecurity.authorizeHttpRequests(
//                authorize -> authorize.
//                        anyRequest().
//                        permitAll());
//        return httpSecurity.build();
//    }

}
/*
Extra

When you use Spring security, by default all requests
are blocked due to security filter
All requests should be authenticated to
pass this security filter


CSRF

Cross site request forgery

an attack where an authenticated user is tricked into
executing unwanted actions on a web application

We need protection against attacks like csrf:
Spring security by default might apply,

Spring security:
1. by default, csrf is enabled by
2. POST/PUT/DELETE - 403 response(Forbidden), send
a csrf token

1. REST APIs, it is completely safe to disable csrf
2. stateless apis, safe to disable csrf
3. using jwts / basic auth, it's safe to disable csrf
 */