package com.bxb.sunduk_pay.factories.userFactory;

import com.bxb.sunduk_pay.exception.InvalidLoginMethodException;
import com.bxb.sunduk_pay.model.AuthProvider;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.util.AuthProviderMethod;
import com.bxb.sunduk_pay.util.UserRequestType;
import com.bxb.sunduk_pay.util.UserStatus;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class responsible for checking if a user exists based on the provided
 * email or phone number. It validates the login method and checks the user's
 * authentication providers to determine if the user can proceed with login or
 * signup.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class CheckUserIfExists implements UserOperation {
    /**
     *
     */
    private final Validations validations;

    @Override
    public UserRequestType getUserRequestType() {
        return UserRequestType.CHECK_IF_EXISTS;
    }

    @Override
    public UserResponse perform(UserRequest request) {

        Optional<User> user =
                validations.getUserByPhoneNumberOrEmail(request.getPhoneNumber(),
                        request.getEmail());
        log.info("User check for email: {}, phone: {}. User found: {}",
                request.getEmail(), request.getPhoneNumber(), user.isPresent());

        if (user.isEmpty()) {
            log.info("User does not exist. Email: {}, Phone: {}",
                    request.getEmail(), request.getPhoneNumber());
            return UserResponse.builder().userExists(false).build();
        }
        User user1 = user.get();

        Set<AuthProviderMethod> authProviderMethods =
                user1.getAuthProviders()
                        .stream()
                        .map(AuthProvider::getAuthProviderMethod)
                        .collect(Collectors.toSet());

        boolean hasEmail = request.getEmail() != null;
        boolean hasPhone = request.getPhoneNumber() != null;

        if (hasEmail == hasPhone) {
            throw new InvalidLoginMethodException("Invalid login request");
        }

        if (hasEmail) {

            if (!authProviderMethods.contains(AuthProviderMethod.LOCAL_EMAIL) && !authProviderMethods.contains(AuthProviderMethod.GOOGLE)) {
                throw new InvalidLoginMethodException("Please login " +
                        "using the registered phone number");
            }

            if (!Objects.equals(user1.getEmail(), request.getEmail())) {
                throw new InvalidLoginMethodException("Email does not match " +
                        "our records");
            }

        } else {

            if (!authProviderMethods.contains(AuthProviderMethod.LOCAL_PHONE)) {
                throw new InvalidLoginMethodException("Please login using" +
                        " " + " the registered email address");
            }
        }

            boolean isGoogleUser =
                    user.get().getAuthProviders().stream()
                            .anyMatch(authProvider
                                    -> authProvider.getAuthProviderMethod()
                                    == AuthProviderMethod.GOOGLE);

            String password = user.get().getPassword();

            if (isGoogleUser) {
                log.info("User is a Google user. Email: {}, Phone: {}",
                        user.get().getEmail(), user.get().getPhoneNumber());


                if (password != null) {
                    return UserResponse
                            .builder()
                            .userExists(true)
                            .isGoogleUser(true)
                            .isVerified(true).build();
                } else {
                    return UserResponse.builder().userExists(true)
                            .isGoogleUser(true)
                            .isVerified(false)
                            .build();
                }


            } else if (UserStatus.ACTIVE.equals(user.get().getUserStatus())) {

                log.info("User is active. Email: {}, Phone: {}",
                        user.get().getEmail(), user.get().getPhoneNumber());

                return UserResponse.builder()
                        .userExists(true)
                        .isVerified(true)
                        .build();

            } else if (UserStatus.INACTIVE.equals(user.get().getUserStatus())) {
                log.info("User is inactive. Email: {}, Phone: {}",
                        user.get().getEmail(), user.get().getPhoneNumber());
                return UserResponse.builder().isVerified(true).build();
            }

        return UserResponse.builder().message("something went wrong while " +
                "checking user existence").build();

    }
}
