package com.chitalebandhu.chitalebandhu.services;

import com.chitalebandhu.chitalebandhu.entity.User;
import com.chitalebandhu.chitalebandhu.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;

    public void UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public void updatePassword(String oldPassword, String newPassword){
        Optional<User> existingUser = userRepository.findByPassword(oldPassword);
        if(existingUser.isPresent()){
            existingUser.get().setPassword(newPassword);
        }
        else{
            throw new RuntimeException("User is not found");
        }
    }
}
