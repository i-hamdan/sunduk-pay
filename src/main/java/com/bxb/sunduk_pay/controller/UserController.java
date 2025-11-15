package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * UserController handles user-related API endpoints.
 */

@Log4j2
@RestController
@RequiredArgsConstructor
public class UserController {
    /**
     * UserService for handling user operations.
     */
    private final UserService userService;


    /**
     * Endpoint for user operations.
     *
     * @param request UserRequest containing operation details
     * @return ResponseEntity with UserResponse
     */
    @PostMapping("/user")
    public ResponseEntity<UserResponse> userOperations(
            @RequestBody final UserRequest request) {
        log.info("user api hit " + request.getUserRequestType());

        return ResponseEntity.ok(userService.userOperations(request));
    }

}
