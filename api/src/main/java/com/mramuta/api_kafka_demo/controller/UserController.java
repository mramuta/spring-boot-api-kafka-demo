package com.mramuta.api_kafka_demo.controller;

import com.mramuta.api_kafka_demo.model.User;
import com.mramuta.api_kafka_demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = {"/users"}, params = {"country"})
    public Set<User> getUsersByCountry(@RequestParam("country") String country) {
        return userService.getUsersByCountry(country);
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
    }
}
