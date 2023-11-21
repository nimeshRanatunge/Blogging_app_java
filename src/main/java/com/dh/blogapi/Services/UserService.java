package com.dh.blogapi.Services;

import com.dh.blogapi.Interfaces.IUserRepository;
import com.dh.blogapi.Models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private IUserRepository repo;

    public List<User> getAll(){ return repo.findAll(); }

    public User save(User person){ return repo.save(person); }

    public User getUser(String username){ return repo.findUserByUsername(username); }

    public void delete(int id){  repo.deleteById(id); }

    public User getUserByUserNameOrEmail( String usernameOrEmail ){
        return repo.findUserByUsernameOrEmail( usernameOrEmail );
    }

    public boolean isUsernameOrEmailAlreadyTaken( String username , String email ) {
        if( repo.findUsernameOrEmailAlreadyTaken(username , email) == 1 ){
            return true;
        }else{
            return false;
        }

    }

    public void setPermission(String username, String setPermissionAs) {
        repo.updatePermission(username , setPermissionAs);
    }

    public void deleteUser(String username) { repo.removeUser(username); }
}
