package com.chitalebandhu.chitalebandhu.controller;

import com.chitalebandhu.chitalebandhu.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("user")
public class UserController {

    private PasswordEncoder passwordEncoder;

    private final UserService userService;
    public UserController(UserService userService){
        this.userService = userService;
    }

    @PutMapping("updatePassowrd/{id}")
    public void updatePassword(@PathVariable String id, @RequestBody String newPassword){
        newPassword = passwordEncoder.encode(newPassword);
        userService.updatePassword(id, newPassword);
    }
}
