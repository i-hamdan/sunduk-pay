package com.bxb.sunduk_pay.serviceImpl;


import com.bxb.sunduk_pay.Mappers.UserMapper;
import com.bxb.sunduk_pay.event.UserKafkaEvent;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.response.UserLoginResponse;
import com.bxb.sunduk_pay.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Log4j2
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KafkaTemplate<String, UserKafkaEvent> kafkaTemplate;


    public UserServiceImpl(UserRepository repository, UserMapper userMapper, KafkaTemplate<String, UserKafkaEvent> kafkaTemplate) {
        this.userRepository = repository;
        this.userMapper = userMapper;

        this.kafkaTemplate = kafkaTemplate;
    }


    //OAuthLogin method

    @Override
    public void userLogin(UserLoginResponse response) {
        Optional<User> userOptional = userRepository.findByEmailAndIsDeletedFalse(response.getEmail());
        User user;
        if (userOptional.isEmpty()) {
            log.info("User not found in DB. Creating new user for email: {}", response.getEmail());
            user = userMapper.toUser(response);
            user.setUuid(UUID.randomUUID().toString());
            user.setIsDeleted(false);
            userRepository.save(user);


            UserKafkaEvent userEvent = userMapper.toKafkaEvent(user, "SIGNUP");
            kafkaTemplate.send("user-topic", userEvent);
            log.info("New user saved with UUID: {}", user.getUuid());
        } else {
            user = userOptional.get();
            UserKafkaEvent userEvent = userMapper.toKafkaEvent(user, "LOGIN");
            kafkaTemplate.send("user-topic", userEvent);
            log.info("Login successful");
        }
    }
}
