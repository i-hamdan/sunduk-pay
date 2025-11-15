package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.UserMapper;
import com.bxb.sunduk_pay.exception.ResourceNotFoundException;
import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.MasterWallet;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.ContactRepository;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.repository.MasterWalletRepository;
import com.bxb.sunduk_pay.repository.MpinRepository;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.service.UserService;
import com.bxb.sunduk_pay.userFactory.UserOperation;
import com.bxb.sunduk_pay.userFactory.UserOperationFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


/**
 * Service implementation for managing users.
 * Handles user login, registration, and contact upload.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserOperationFactory userOperation;
    /**
     * Repository for user data access.
     */
    private final UserRepository userRepository;
    /**
     * Mapper for converting between User entities and DTOs.
     */
    private final UserMapper userMapper;
    /**
     * Kafka template for sending user events.
     */
    private final KafkaTemplate<String, UserKafkaEvent> kafkaTemplate;
    /**
     * Repository for main wallet data access.
     */
    private final MainWalletRepository mainWalletRepository;
    /**
     * Repository for master wallet data access.
     */
    private final MasterWalletRepository masterWalletRepository;
    /**
     * Repository for MPIN data access.
     */
    private final MpinRepository mpinRepository;

    /**
     * Repository for contact data access.
     */

    private final ContactRepository contactRepository;


    /**
     * Handles OAuth login for a user.
     * If the user does not exist in the database,
     * a new user is created along with main and master wallets.
     * @param response The user login response containing user details.
     * @return The User object after login or creation.
     */
    @Override
    public User userLogin(final UserResponse response) {
        Optional<User> userOptional
            = userRepository.findByEmailAndIsDeletedFalse(
            response.getEmail());
        User user;
        if (userOptional.isEmpty()) {
            log.info(
             "User not found in DB. Creating new user for email: {}",
             response.getEmail());
            user = userMapper.toUser(response);
            user.setUuid(UUID.randomUUID().toString());
            user.setPhoneNumber(response.getPhoneNumber());
            user.setIsDeleted(false);

            user=userRepository.save(user);

            MainWallet mainWallet = MainWallet.builder()
                    .mainWalletId(UUID.randomUUID().toString())
                    .balance(0d)
                    .user(user)
                    .createdAt(LocalDateTime.now())
                    .build();
            mainWallet=mainWalletRepository.save(mainWallet);

            MasterWallet masterWallet = MasterWallet.builder()
                    .masterWalletId(UUID.randomUUID().toString())
                    .balance(0d)
                    .user(user)
                    .mainWallet(mainWallet)
                    .createdAt(LocalDateTime.now())
                    .build();
            masterWallet = masterWalletRepository.save(masterWallet);

            user.setMasterWallet(masterWallet);
            user.setMainWallet(mainWallet);
            userRepository.save(user);

            UserKafkaEvent userEvent = userMapper
                    .toKafkaEvent(user, "SIGNUP");
            log.info("Sending user signup event to Kafka for email: {}",
                    user.getEmail());
            kafkaTemplate.send("user-topic", userEvent);
            log.info("New user saved with UUID: {}", user.getUuid());

        } else {
            user = userOptional.get();
            if (user.getPhoneNumber()==null){
             log.error( "User found but phone number is null for email: {}",
                     user.getEmail());
            }
            UserKafkaEvent userEvent = userMapper
                    .toKafkaEvent(user,  "LOGIN");
            log.info("Sending user login event to Kafka for email: {}",
                    user.getEmail());
            kafkaTemplate.send("user-topic", userEvent);
            log.info("Login successful");

        }

        boolean present = mpinRepository
                .findByUser_Uuid(user.getUuid()).isPresent();
        user.setIsMpinCreated(present);
        return user;
    }

    @Override
    public UserResponse userOperations(UserRequest request) {
        UserOperation userOperations =
                userOperation.getUserOperations(request.getUserRequestType());
    return userOperations.perform(request);
    }

}
