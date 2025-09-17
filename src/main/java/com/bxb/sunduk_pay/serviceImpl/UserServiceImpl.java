package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.UserMapper;
import com.bxb.sunduk_pay.exception.UserNotFoundException;
import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.MasterWallet;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.repository.MasterWalletRepository;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.request.ContactRequest;
import com.bxb.sunduk_pay.response.UserLoginResponse;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.service.UserService;
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
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KafkaTemplate<String, UserKafkaEvent> kafkaTemplate;
    private final MainWalletRepository mainWalletRepository;
    private final MasterWalletRepository masterWalletRepository;

    public UserServiceImpl(UserRepository repository, UserMapper userMapper, KafkaTemplate<String, UserKafkaEvent> kafkaTemplate,
                           MainWalletRepository mainWalletRepository, MasterWalletRepository masterWalletRepository) {
        this.userRepository = repository;
        this.userMapper = userMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.mainWalletRepository = mainWalletRepository;
        this.masterWalletRepository = masterWalletRepository;
    }

    /**
     * Handles OAuth login for a user. If the user does not exist in the database,
     * a new user is created along with main and master wallets.
     *
     * @param response The user login response containing user details.
     * @return The User object after login or creation.
     */
    @Override
    public User userLogin(UserLoginResponse response) {
        Optional<User> userOptional = userRepository.findByEmailAndIsDeletedFalse(response.getEmail());
        User user;

        if (userOptional.isEmpty()) {
            log.info("User not found in DB. Creating new user for email: {}", response.getEmail());
            user = userMapper.toUser(response);
            user.setUuid(UUID.randomUUID().toString());
            user.setIsDeleted(false);

            MainWallet mainWallet = MainWallet.builder()
                    .mainWalletId(UUID.randomUUID().toString())
                    .balance(0d)
                    .user(user)
                    .build();
            mainWalletRepository.save(mainWallet);

            MasterWallet masterWallet = MasterWallet.builder()
                    .masterWalletId(UUID.randomUUID().toString())
                    .balance(0d)
                    .user(user)
                    .mainWallet(mainWallet)
                    .createdAt(LocalDateTime.now())
                    .build();
            masterWalletRepository.save(masterWallet);

            user.setMasterWallet(masterWallet);
            user.setMainWallet(mainWallet);
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

        return user;
    }

    /**
     * Uploads contacts for a user.
     *
     * @param contactRequest The contact request containing userId and contacts list.
     * @return UserResponse containing updated user details and success message.
     * @throws UserNotFoundException If the userId does not exist in the database.
     */
    @Override
    public UserResponse uploadContacts(ContactRequest contactRequest) {
        log.info("Upload contacts request received for userId: {}", contactRequest.getUserId());

        User user = userRepository.findById(contactRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found for ID: " + contactRequest.getUserId()));
        log.debug("User fetched: {}", user.getUuid());

        user.setContacts(contactRequest.getContacts());
        log.info("Setting {} contacts for userId: {}", contactRequest.getContacts().size(), user.getUuid());

        userRepository.save(user);
        log.info("User with ID: {} successfully updated with contacts", user.getUuid());

        return UserResponse.builder()
                .uuid(user.getUuid())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .message("successfully saved")
                .build();
    }
}
