package com.dh.blogapi.Controllers;

import com.dh.blogapi.DTOs.User.UserCreateDto;
import com.dh.blogapi.DTOs.User.UserDto;
import com.dh.blogapi.Errors.ApiError;
import com.dh.blogapi.Models.User;
import com.dh.blogapi.Services.UserService;
import com.dh.blogapi.Utility.JwtDecodeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class UserController {
    @Autowired
    private JwtDecodeService jwtDecodeService;
    @Autowired
    private UserService service;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        if(Objects.equals(jwtDecodeService.decode().getPermissionLevel() , "admin")){
            ModelMapper modelMapper = new ModelMapper();

            return new ResponseEntity<>(service.getAll().stream().map(user ->{
                return modelMapper.map(user , UserDto.class);
            }).collect(Collectors.toList()) , HttpStatus.OK) ;

        }else{
            return new ResponseEntity<>( "Access Denied" , HttpStatus.UNAUTHORIZED );
        }
    }

    @GetMapping("user/{username}")
    public ResponseEntity<?> getUser(@PathVariable String username){
        if (Objects.equals(jwtDecodeService.decode().getUsername() , username)
                || Objects.equals(jwtDecodeService.decode().getPermissionLevel() , "admin") ){
            ModelMapper modelMapper = new ModelMapper();
            User u = service.getUser(username);
            return  new ResponseEntity<>(modelMapper.map(u , UserDto.class) , HttpStatus.OK);
        }else{
            return new ResponseEntity<>( "Access Denied" , HttpStatus.UNAUTHORIZED );
        }
    }

    @PostMapping("/user")
    public ResponseEntity<?> register(@RequestBody UserCreateDto userCreateDto ){

        boolean res = service.isUsernameOrEmailAlreadyTaken(userCreateDto.getUsername() , userCreateDto.getEmail());
        if(res == !true){
            ModelMapper modelMapper = new ModelMapper();

            User u = modelMapper.map(userCreateDto , User.class);

            u.setJoinedDate(new Date()) ;
            u.setPermissionLevel("user");
            u.setPasswordHash(passwordEncoder.encode(userCreateDto.getPassword()));

            User createdUser = service.save(u);

            return new ResponseEntity<>(modelMapper.map(createdUser , UserDto.class) , HttpStatus.OK ) ;
        }
         return new ResponseEntity<>( "username Or email Already Exist" , HttpStatus.CONFLICT );
    }

    @PostMapping( value = "user/{username}/setPermission")
    public ResponseEntity<?> setPermission( @PathVariable String username ){
        if(Objects.equals(jwtDecodeService.decode().getPermissionLevel() , "admin")){
            try {
                String setPermissionAs = null;
                User u = service.getUser(username);
                if(Objects.equals(u.getPermissionLevel() , "admin")){ setPermissionAs = "user"; }
                else{ setPermissionAs = "admin"; }

                try{
                    service.setPermission(u.getUsername() , setPermissionAs);
                    return new ResponseEntity<>( "User Updated" , HttpStatus.OK );
                }catch (Exception e){
                    System.out.println("error is "+e);
                    return new ResponseEntity<>( "User Something went wrong" , HttpStatus.INTERNAL_SERVER_ERROR );
                }

            }catch (Exception e){
                return new ResponseEntity<>( "User Not Found" , HttpStatus.NOT_FOUND );
            }
        }else{
            return new ResponseEntity<>( "Access Denied" , HttpStatus.UNAUTHORIZED );
        }
    }

    @DeleteMapping(value = "user/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username){
        try {
            if(Objects.equals(jwtDecodeService.decode().getPermissionLevel() , "admin")
                    || Objects.equals(service.getUser(username).getUsername() , jwtDecodeService.decode().getUsername())){
                service.deleteUser(username);

                return new ResponseEntity<>( "User successfully removed form DB" , HttpStatus.OK );
            }else{
                return new ResponseEntity<>( "Access Denied" , HttpStatus.UNAUTHORIZED );
            }
        }catch (Exception e){
            return new ResponseEntity<>( "User Not Found" , HttpStatus.NOT_FOUND );
        }
    }


    // -> Error Handling <- \\

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNoSuchElementException(NoSuchElementException exception , HttpServletRequest request){
        ApiError error = new ApiError(404 , "user not found" , new Date().getTime() , request.getServletPath());
        return error;
    }
}