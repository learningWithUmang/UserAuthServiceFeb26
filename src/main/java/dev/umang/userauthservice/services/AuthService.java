package dev.umang.userauthservice.services;

import dev.umang.userauthservice.exceptions.IncorrectPasswordException;
import dev.umang.userauthservice.exceptions.UserAlreadyExistException;
import dev.umang.userauthservice.exceptions.UserNotRegisteredException;
import dev.umang.userauthservice.models.Role;
import dev.umang.userauthservice.models.Session;
import dev.umang.userauthservice.models.State;
import dev.umang.userauthservice.models.User;
import dev.umang.userauthservice.pojos.UserToken;
import dev.umang.userauthservice.repositories.RoleRepo;
import dev.umang.userauthservice.repositories.SessionRepo;
import dev.umang.userauthservice.repositories.UserRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class AuthService implements IAuthService{
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private SessionRepo sessionRepo;

    @Autowired
    private SecretKey secretKey;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    /*
    encode(raw password) return encoded password
    128 bit random salt and cost factor
    cost factor = no of hashing rounds

    there's is no decode method

    In login(), will you need to compare the entered password
    with the hashed password

    matches(rawPassword, alreadyEncodedPassword) = true/false
    How does this work?
    1. Extracts the salt and cost from the encoded password.
    2. Re-hashes the input password with same salt and hash.
    3. Compared both the hashes
     */

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
        user.setPassword(bCryptPasswordEncoder.encode(password));
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

    /*
    Map
    multiple key value pairs
    <user1, token1>
    <user2, token2>

    Pair
    <key, value>
     */

    @Override
    public UserToken login(String email, String password) {
        Optional<User> optionalUser = userRepo.findByEmail(email);

        if(optionalUser.isEmpty()){
            throw new UserNotRegisteredException("Please register first");
        }

        User user = optionalUser.get();
        if(bCryptPasswordEncoder.matches(password, user.getPassword())){
            /*
            Generate the token
             */
            Map<String, Object> payload = new HashMap<>();
            Long nowInMills = System.currentTimeMillis(); // return timestamp in epoch
            payload.put("iat", nowInMills);
            payload.put("exp", nowInMills + 10000000); //100k milli-seconds as expiry time period
            payload.put("userId" ,user.getId());
            payload.put("iss", "scaler");
            payload.put("scope" , user.getRoles());
            /*
            Payload is generated
             */

            String token = Jwts.builder().claims(payload).
                    signWith(secretKey).
                    compact();

            System.out.println(token.length());
            System.out.println(token);

            /*
            Create a new logged in session for the user
             */
            Session session = new Session();
            session.setToken(token);
            session.setUser(user);
            session.setState(State.ACTIVE);
            sessionRepo.save(session);
            /*
            We also want to return this generated token back to the client?

             */
            /*
            When you send this token to resources sever, it should
            be able to self validate the token
            I want to persist all the token that i am generating

            Ideally, for storing tokens, we should create a
            new table called as "Session"


            Diff between Sessions and cookies
            Session is used to store token in the backend
            Cookies are used to store token in the browser

            Auth service is generating so many tokens for every user
            there should be some source of truth / db where all these
            tokens should persist
             */

            return new UserToken(user, token);
        }else{
            throw new IncorrectPasswordException("Incorrect password entered");
        }
    }

    @Override
    public Boolean validateToken(String token) {
        /*
        We want to check if this token is in my db or not?
        in Sessions table
         */
        Optional<Session> optionalSession = sessionRepo.findByToken(token);

        if(optionalSession.isEmpty()){
            return false;
        }

        JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();

        Claims claims = jwtParser.parseSignedClaims(token).getPayload();

        System.out.println(claims);

        /*
        Extracting the payload from the JWT using a parser which contains the secret key

         */

        Long expiryTime = (Long) claims.get("exp");
        Long nowInMills = System.currentTimeMillis();

        if(nowInMills > expiryTime){
            Session session = optionalSession.get();
            session.setState(State.INACTIVE);
            sessionRepo.save(session);
            return false;
        }

        return true;
    }
    /*
    login should generate a JWT
    JWT - most important part of JWT?
    Payload - contains user info, client info and info about token
    Payload is also referred to as claims

    Which DS can be used to represent claims/payload?
    Map<String, Object> where key represents strings data type and
    value represents any data type
    1. createdAt (iat) = issued at
    2. expiry (exp)  = expiry
    3. userId (userId)
    4. creator (iss) iss = issued by
    5. scope (scope)

    1 s = 1000 ms
    1000 ms = 1 s
    10,000 ms = 10 s
    100,00 ms = 100 s

     */
}

/*
store password in db in encrypted format

HW: BCryptPassword encoder in Java spring

Agenda for next class
Encode password and generate JWT and return

What do you need for generating signature?
(Header + payload, secret key)
Header = algorithm
secret key = need to figure out
 */
