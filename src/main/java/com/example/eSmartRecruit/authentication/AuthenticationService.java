package com.example.eSmartRecruit.authentication;



import com.example.eSmartRecruit.authentication.request_reponse.AuthenticationRequest;
import com.example.eSmartRecruit.authentication.request_reponse.AuthenticationResponse;
import com.example.eSmartRecruit.authentication.request_reponse.RegisterRequest;

import com.example.eSmartRecruit.config.JwtService;
import com.example.eSmartRecruit.controllers.request_reponse.ResponseObject;
import com.example.eSmartRecruit.exception.UserException;
import com.example.eSmartRecruit.models.enumModel.Role;
import com.example.eSmartRecruit.models.User;
import com.example.eSmartRecruit.models.enumModel.UserStatus;
import com.example.eSmartRecruit.models.redis.Token;
import com.example.eSmartRecruit.repositories.UserRepos;
import com.example.eSmartRecruit.repositories.redis.TokenRepos;
import com.example.eSmartRecruit.services.impl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.yaml.snakeyaml.util.EnumUtils;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepos userRepo;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepos tokenRepos;
    public ResponseObject register(RegisterRequest request) throws UserException {
        //Role role = Role.valueOf(request.getRoleName());

        try{
            Role.valueOf(request.getRoleName());
        }catch (Exception e){
            throw new UserException("Wrong Role name");
        }

        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .roleName(Role.valueOf(request.getRoleName()))
                .status(UserStatus.Active)
                .password(passwordEncoder.encode(request.getPassword()))
                .createDate(Date.valueOf(LocalDate.now()))
                .updateDate(Date.valueOf(LocalDate.now()))
                .build();
        String checkDuplication = userService.checkDuplicate(user);
        if(checkDuplication!=null){
            throw new UserException(checkDuplication);
        }
        try {
            userRepo.save(user);
        }catch (Exception e){
            return ResponseObject.builder().status(ResponseObject.ERROR_STATUS).message(e.getMessage()).build();
        }
        var jwtToken = jwtService.generateToken(user);
        Token token = new Token(user.getUsername(),jwtToken);

        tokenRepos.save(token);

        return ResponseObject.builder()
                .status(ResponseObject.SUCCESS_STATUS)
                .data(jwtToken)
                .message(ResponseObject.REGISTER_SUCCESS)
                .build();
    }

    public ResponseObject authenticate(AuthenticationRequest request) throws UserException {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword())

            );

            var user = userRepo.findByUsername(request.getUsername()).orElseThrow(()->new UserException("User not found!"));

            var jwtToken = jwtService.generateToken(user);

            try{
                String tokenS = tokenRepos.findTokenByUsername(user.getUsername());
            if(tokenS!=null){
                System.out.println(tokenS);
                tokenRepos.deleteToken(user.getUsername());
            }
            Token token = new Token(user.getUsername(),jwtToken);
            tokenRepos.save(token);}
            catch (Exception e){
                throw new UserException(e.toString());
            }

            return ResponseObject.builder()
                    .data(jwtToken)
                    .status(ResponseObject.SUCCESS_STATUS)
                    .message(ResponseObject.SIGN_IN_SUCCESS)
                    .build();
        }catch (Exception exception){
            //throw new UserException("User name or password is wrong");
            throw new UserException(exception.toString());

        }


    }
    public ResponseObject logout(String token){
        String username = jwtService.extractUserName(token);
        String message = tokenRepos.deleteToken(username);
        return ResponseObject.builder().status(ResponseObject.SUCCESS_STATUS).message(message).build();
    }

}
