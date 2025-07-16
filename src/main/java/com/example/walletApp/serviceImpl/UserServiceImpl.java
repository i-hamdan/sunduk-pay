package com.example.walletApp.serviceImpl;

import com.example.walletApp.Mappers.UserMapper;
import com.example.walletApp.exceptionHandling.ResourceNotFoundException;
import com.example.walletApp.exceptionHandling.UserNotFoundException;
import com.example.walletApp.factoryPattern.UserRoleService;
import com.example.walletApp.factoryPattern.UserServiceFactory;
import com.example.walletApp.model.User;
import com.example.walletApp.repository.UserRepository;
import com.example.walletApp.request.UserRequest;
import com.example.walletApp.request.UserLoginRequest;
import com.example.walletApp.response.UserResponse;
import com.example.walletApp.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserServiceFactory serviceFactory;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository repository, UserServiceFactory serviceFactory, UserMapper userMapper) {
        this.userRepository = repository;
        this.serviceFactory = serviceFactory;
        this.userMapper = userMapper;
    }


    //OAuthLogin method
    @Override
    public UserResponse login(UserLoginRequest request) {
        Optional<User> userOptional = userRepository.findByEmailAndIsDeletedFalse(request.getEmail());
        User user;
        if (userOptional.isEmpty()) {
            user = userMapper.toUser(request);
            user.setUuid(UUID.randomUUID().toString());
            user.setIsDeleted(false);
            userRepository.save(user);
        } else {
            user = userOptional.get();
        }
        //POINT TO BE NOTED!!!
        //If all the fields in Response except email and fullName is null then its working fine,because google only provide email and full name.
        //IF EVERYTHING IS NULL THEN PLEASE REFRESH THE MAPPER CLASS (UserMapper) using ctrl+shift+f9
      return userMapper.toUserResponse(user);
    }


    public String createUser(UserRequest request) {
        User user = userMapper.fromUserRequestToUser(request);
        user.setIsDeleted(false);
        userRepository.save(user);

        UserRoleService serviceByType = serviceFactory.getServiceByType(request.getUserType());
        if (serviceByType != null) {
            return serviceByType.getUserInfo();
        } else {
            throw new ResourceNotFoundException("UserType is not valid!");
        }
    }



    public List<UserResponse> getAll() {
        List<User> user = userRepository.findAll()
                .stream()
                .filter(user1 -> !user1.getIsDeleted()).toList();
        return userMapper.toUserResponseList(user);
    }




    public UserResponse getById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Please provide a valid user id."));
        if(user.getIsDeleted()){
            throw new UserNotFoundException("This user is already deleted.");
        }
        return userMapper.toUserResponse(user);
    }



    public String updateUser(String id, UserRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Invalid user Id! Please provide a valid user Id."));
        if (user.getIsDeleted()) {
            throw new UserNotFoundException("This user is already deleted!.");
        }
        userMapper.updateUserFromRequest(request, user);
        userRepository.save(user);
        return "User updated successfully";
    }

}
