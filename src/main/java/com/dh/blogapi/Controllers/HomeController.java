package com.dh.blogapi.Controllers;

import com.dh.blogapi.DTOs.JWT.JwtRequest;
import com.dh.blogapi.DTOs.JWT.JwtResponse;
import com.dh.blogapi.Models.User;
import com.dh.blogapi.Security.CustomUserDetailsService;
import com.dh.blogapi.Services.UserService;
import com.dh.blogapi.Utility.JWTUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {


    @Autowired
    private UserService userService;

    @Autowired
    private JWTUtility jwtUtility;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/")
    public String home(){
        return "Welcome Security";
    }

    @PostMapping("/authenticate")
    public JwtResponse authenticate(@RequestBody JwtRequest jwtRequest) throws Exception{
        try{
            authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(
                    jwtRequest.getUsernameOrEmail(),
                    jwtRequest.getPassword()
            ));
        }catch (BadCredentialsException e){
            throw  new Exception("username password error" , e);
        }

        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(jwtRequest.getUsernameOrEmail());

        // add custom user Authentications
        Map<String, Object> claims = new HashMap<>();
        User u = userService.getUserByUserNameOrEmail(jwtRequest.getUsernameOrEmail());
        claims.put( "permissionLevel" , u.getPermissionLevel() );

        final String token = jwtUtility.generateToken( claims , userDetails);



        return  new JwtResponse(token);
    }

}
