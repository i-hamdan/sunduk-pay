package com.bxb.sunduk_pay.factories.GlobalPotFactory;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to fetch admin details.
 */
@Service
public class FetchAdminService implements GlobalPotOperation {
    private final UserRepository userRepository;

    /**
     * Constructor for FetchAdminService.
     * @param userRepository the user repository
     */
    public FetchAdminService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Specifies the type of request this service handles.
     */
    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {
        return GlobalPotRequestType.FETCH_ADMINS;
    }

    /**
     * Performs the operation to fetch admin details.
     * @param request the global pot request
     * @return the global pot response containing admin details
     * @throws IOException if an I/O error occurs
     */
    @Override
    public GlobalPotResponse perform(
            final GlobalPotRequest request) throws IOException {

        List<UserResponse> adminResponses = new ArrayList<>();
        List<User> admins = userRepository.findAll();
        admins.forEach(admin -> {
            UserResponse userResponse = UserResponse.builder()
                    .uuid(admin.getUuid())
                    .fullName(admin.getFullName())
                    .build();
            adminResponses.add(userResponse);
        });
        return GlobalPotResponse.builder()
                .adminList(adminResponses)
                .build();
    }
}
