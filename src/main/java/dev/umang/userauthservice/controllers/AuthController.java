package dev.umang.userauthservice.controllers;

import dev.umang.userauthservice.dtos.LoginRequestDTO;
import dev.umang.userauthservice.dtos.SignupRequestDTO;
import dev.umang.userauthservice.dtos.UserDTO;
import dev.umang.userauthservice.models.User;
import dev.umang.userauthservice.services.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    /*
    bunch of APIs
    1. /signup
        -Type: POST
        -Request type
            -name
            -email
            -password
        -Return type
            -ResponseEntity
            -body: UserDTO
                -UserDTO
                    -name, email
            -200 if registration is success
            -error code if it fails
         -why ResponseEntity<UserDTO> instead of ResponseEntity<User>?
    2. login
        -Type: POST
        -Return type
            -In response, headers
            -ResponseEntity<UserDTO>
                -headers(JWT)
            -name, email, roles
         -Request type
            -email, password

    /login GET
    -you're generating a token as a result of this call and the name
    suggests that you are only feteching some info
    -{
        "username":
        "password":
      }
      Request body / payload
     */

    @Autowired
    private IAuthService authService;


    @PostMapping("/signup")
    ResponseEntity<UserDTO> signup(@RequestBody SignupRequestDTO signupRequestDTO){
        try {
            User user = authService.signup(signupRequestDTO.getName(),
                    signupRequestDTO.getEmail(),
                    signupRequestDTO.getPassword());

            UserDTO userDTO = user.convertToUserDTO();

            return new ResponseEntity<>(userDTO, HttpStatus.CREATED);

        }catch (Exception e){
            return null;
        }

    }

    @PostMapping("/login")
    ResponseEntity<UserDTO> login(@RequestBody LoginRequestDTO loginRequestDTO){
        try{
            User user = authService.login(loginRequestDTO.getEmail(),
                    loginRequestDTO.getPassword());

            return new ResponseEntity<>(user.convertToUserDTO(), HttpStatus.OK);
        } catch (Exception e){
            return null;
        }
    }
}
