package com.example.walletApp.controller;

import com.example.walletApp.request.UserRequest;
import com.example.walletApp.response.UserResponse;
import com.example.walletApp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user-create")
    public ResponseEntity<String> createUser(@RequestBody UserRequest request){
       return new ResponseEntity<>(userService.createUser(request), HttpStatus.CREATED);
    }

    @GetMapping("/user-getAll")
    public ResponseEntity<List<UserResponse>> getAll(){
       return new ResponseEntity<>(userService.getAll(),HttpStatus.OK);
    }

    @GetMapping("/user-getInfoById/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable String id){
        return new ResponseEntity<>( userService.getById(id),HttpStatus.OK);
    }

    @PutMapping("/user-update/{id}")
    public ResponseEntity<String > update(@PathVariable String id,@RequestBody UserRequest request){
        return new ResponseEntity<>(userService.updateUser(id,request),HttpStatus.OK);
    }
    }

