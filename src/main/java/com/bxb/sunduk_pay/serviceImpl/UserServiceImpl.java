package com.bxb.sunduk_pay.serviceImpl;


import com.bxb.sunduk_pay.Mappers.UserMapperImpl;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.repository.WalletRepository;
import com.bxb.sunduk_pay.response.UserLoginResponse;
import com.bxb.sunduk_pay.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
@Log4j2
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapperImpl userMapper;


    public UserServiceImpl(UserRepository repository,  UserMapperImpl userMapper) {
        this.userRepository = repository;
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
}
