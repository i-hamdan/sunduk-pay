//package com.bxb.sunduk_pay.Mappers;
//
//import com.bxb.sunduk_pay.exception.UserNotFoundException;
//import com.bxb.sunduk_pay.model.Follower;
//import com.bxb.sunduk_pay.model.User;
//import com.bxb.sunduk_pay.repository.UserRepository;
//import com.bxb.sunduk_pay.request.FollowerRequest;
//import org.springframework.stereotype.Component;
//
//@Component
//public class FollowerMapperImpl implements FollowerMapper{
//
//    private final UserRepository userRepository;
//
//    public FollowerMapperImpl(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    /**
//     * @param request
//     * @return
//     */
//    @Override
//    public Follower toEntity(FollowerRequest request) {
//        Follower follower = new Follower();
//        if (request.getUuid()==null) return null;
//        User user= userRepository.findById(
//                        request.getUuid()).orElseThrow(()->new UserNotFoundException("User with id "+request.getUuid()+" not found"));
//        follower.setFollowerUser(user);
//        return follower;
//    }
//}
