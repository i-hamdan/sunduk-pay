package com.bxb.sunduk_pay.serviceImpl;


import com.bxb.sunduk_pay.Mappers.UserMapperImpl;
import com.bxb.sunduk_pay.exception.ResourceNotFoundException;
import com.bxb.sunduk_pay.exception.UserNotFoundException;
import com.bxb.sunduk_pay.factoryPattern.UserRoleService;
import com.bxb.sunduk_pay.factoryPattern.UserServiceFactory;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.repository.WalletRepository;
import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserLoginResponse;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Log4j2
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserServiceFactory serviceFactory;
    private final WalletRepository walletRepository;
    private final UserMapperImpl userMapper;


    public UserServiceImpl(UserRepository repository, UserServiceFactory serviceFactory, WalletRepository walletRepository, UserMapperImpl userMapper) {
        this.userRepository = repository;
        this.serviceFactory = serviceFactory;
        this.walletRepository = walletRepository;
        this.userMapper = userMapper;

    }


    //OAuthLogin method

    @Override
    public void login(UserLoginResponse response) {
        Optional<User> userOptional = userRepository.findByEmailAndIsDeletedFalse(response.getEmail());
if (userOptional.isEmpty()){
    log.info("User not found in DB. Creating new user for email: {}", response.getEmail());
    User user = userMapper.toUser(response);
    user.setUuid(UUID.randomUUID().toString());
    user.setIsDeleted(false);
    userRepository.save(user);
    log.info("New user saved with UUID: {}", user.getUuid());
}
        log.info("Login successful");

    }





    public String createUser(UserRequest request) {
        User user = userMapper.fromUserRequestToUser(request);
        user.setIsDeleted(false);
        userRepository.save(user);
        log.info("User {} successfully created and was saved in database", user.getFullName());
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
        log.info("All the active users and their details are retrieved from db");
        return userMapper.toUserResponseList(user);
    }


    public UserResponse getById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Please provide a valid user id."));
        if (user.getIsDeleted()) {
            log.error("User with uuid:{} is already deleted and does not exist", id);
            throw new UserNotFoundException("This user is already deleted");
        }
        log.info("User {} found by id {}", user.getFullName(), user.getUuid());
        return userMapper.toUserResponse(user);
    }


    public String updateUser(String id, UserRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Invalid user Id! Please provide a valid user Id."));
        if (user.getIsDeleted()) {
            log.error("Cannot update user with uuid:{} because user is already deleted and does not exist", id);
            throw new UserNotFoundException("This user is already deleted!.");
        }
        userMapper.updateUserFromRequest(request, user);
        userRepository.save(user);
        log.info("User with uuid:{} was successfully updated", user.getUuid());
        return "User updated successfully";
    }


    @Override
    public String deleteUser(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Invalid user Id."));
        if (user.getIsDeleted()) {
            log.error("Cannot delete user with uuid:{} because user is already deleted", id);
            throw new UserNotFoundException("This user has already been deleted.");
        }
        if (walletRepository.findByUser_Uuid(id)
                .stream().filter(user1 -> !user1.getIsDeleted())
                .toList().isEmpty()) {
            user.setIsDeleted(true);
            userRepository.save(user);
            log.info("user with uuid:{} was deleted successfully", user.getUuid());
            return "User deleted successfully.";
        }
        throw new ResourceNotFoundException("Cannot delete user because the user has active wallets.");
    }
}
