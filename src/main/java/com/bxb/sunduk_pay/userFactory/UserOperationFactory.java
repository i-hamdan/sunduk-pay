package com.bxb.sunduk_pay.userFactory;

import com.bxb.sunduk_pay.util.UserRequestType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory class to manage and provide UserOperation
 */
@Component
@RequiredArgsConstructor
public class UserOperationFactory {
/**
     * List of all available user operations.
     */
private final List<UserOperation> userOperations;
/**
     * Mapping of UserRequestType to UserOperation.
     */

private Map<UserRequestType,UserOperation> userServiceMap = new HashMap<>();
/**
     * Initializes the operation map after construction.
     */

@PostConstruct
    public void putValues(){
    for (UserOperation service: userOperations){
        userServiceMap.put(service.getUserRequestType(),service);
    }
}
/**
     * Retrieves the UserOperation for the given UserRequestType.
     * @param userRequestType the type of user request
     * @return UserOperation
     */
public UserOperation getUserOperations(UserRequestType userRequestType){
    return userServiceMap.get(userRequestType);
}
}
