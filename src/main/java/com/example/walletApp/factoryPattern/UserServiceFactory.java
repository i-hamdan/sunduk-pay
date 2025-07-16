package com.example.walletApp.factoryPattern;

import com.example.walletApp.util.UserType;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserServiceFactory {
    private final List<UserRoleService> userRoleService;


    public UserServiceFactory(List<UserRoleService> userRoleService) {
        this.userRoleService = userRoleService;
    }
    Map<UserType,UserRoleService> serviceMap = new HashMap<>();


    @PostConstruct
    public void getValues(){
        for(UserRoleService service:userRoleService){
            serviceMap.put(service.getUserType(),service);
        }
    }
    public UserRoleService getServiceByType(UserType userType){
        return serviceMap.get(userType);
    }
}
