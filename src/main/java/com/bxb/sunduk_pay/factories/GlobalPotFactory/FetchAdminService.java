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

@Service
public class FetchAdminService implements GlobalPotOperation{
    private final UserRepository userRepository;

    public FetchAdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * @return
     */
    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {
        return GlobalPotRequestType.FETCH_ADMINS;
    }

    /**
     * @param request
     * @return
     * @throws IOException
     */
    @Override
    public GlobalPotResponse perform(GlobalPotRequest request) throws IOException {

        List<UserResponse> adminResponses = new ArrayList<>();
        List<User> admins = userRepository.findAll();
        admins.forEach(admin ->{
            UserResponse userResponse= UserResponse.builder()
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
