package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
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
        log.info("user-getAll api was hit");
       return new ResponseEntity<>(userService.getAll(),HttpStatus.OK);
    }

    @GetMapping("/user-getInfoById/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable String id){
        log.info("user-getInfoById api was hit");
        return new ResponseEntity<>( userService.getById(id),HttpStatus.OK);
    }

    @PutMapping("/user-update/{id}")
    public ResponseEntity<String > update(@PathVariable String id,@RequestBody UserRequest request){
        log.info("user-update api was hit");
        return new ResponseEntity<>(userService.updateUser(id,request),HttpStatus.OK);
    }
    @DeleteMapping("/user-delete/{id}")
    public ResponseEntity<String> delete(@PathVariable String id){
        log.info("user-delete api was hit");
        return new ResponseEntity<>(userService.deleteUser(id),HttpStatus.OK);
    }
    }

