package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.service.UserService;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/**     * UserController handles user-related API endpoints
     */

@Log4j2
@RestController
@RequiredArgsConstructor
public class UserController {
/**     * UserService for handling user operations
     */
private final UserService userService;


private final Validations validations;


/**     * Endpoint for user operations
     * @param request UserRequest containing operation details
     * @return ResponseEntity with UserResponse
     */
@PostMapping("/user")
public ResponseEntity<UserResponse>userOperations(
        @RequestBody UserRequest request){
    log.info("user api hit " + request.getUserRequestType());

    return ResponseEntity.ok(userService.userOperations(request));
}


@GetMapping("/test/{phoneNumber}")
    public User findByPhoneNUmber(@PathVariable String phoneNumber){

    User user = validations.getUserByPhoneNumber(phoneNumber);

return user;
}
}
