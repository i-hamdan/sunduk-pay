package com.bxb.sunduk_pay.factories.globalPotFactory;

import com.bxb.sunduk_pay.factories.GlobalPotFactory.AddFollowersService;
import com.bxb.sunduk_pay.model.Follower;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.FollowerRepository;
import com.bxb.sunduk_pay.repository.GlobalPotRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AddFollowersServiceTest {

    @Mock
    private Validations validations;

    @Mock
    private GlobalPotRepository globalPotRepository;

    @Mock
    private FollowerRepository followerRepository;

    @InjectMocks
    private AddFollowersService addFollowersService;

    @Test
    void shouldReturnGlobalPotRequestType(){

        GlobalPotRequestType type = addFollowersService.getGlobalPotRequestType();
        Assertions.assertEquals(addFollowersService.getGlobalPotRequestType(),type);
    }

    @Test
    void shouldReturnAlreadyFollowingMessage_whenFollowerExists() {

        // GIVEN
        User followerUser = new User();
        followerUser.setUuid("user-123");

        GlobalPotRequest globalPotRequest = new GlobalPotRequest();
        globalPotRequest.setFollowerUser("user-123");
        globalPotRequest.setGlobalPotId("globalPot-123");

        GlobalPot globalPot = new GlobalPot();
        globalPot.setGlobalPotId("globalPot-123");

        // Stubs
        when(validations.getUserInfo("user-123"))
                .thenReturn(followerUser);

        when(globalPotRepository.findById("globalPot-123"))
                .thenReturn(Optional.of(globalPot));

        when(followerRepository.existsByFollowerUserAndGlobalPot(followerUser,
                globalPot))
                .thenReturn(true);

        // WHEN
        GlobalPotResponse response = addFollowersService.perform(globalPotRequest);

        // THEN
        Assertions.assertEquals("Already following this Global Pot ",
                response.getMessage());
        verify(validations).getUserInfo("user-123");
        verify(globalPotRepository).findById("globalPot-123");
        verify(followerRepository).existsByFollowerUserAndGlobalPot(followerUser,
                globalPot);
    }

    @Test
    void shouldFollowPotSuccessfully_WhenFollowerNotExists() {

        // Given
        User followerUser = new User();
        followerUser.setUuid("user-123");

        GlobalPotRequest globalPotRequest = new GlobalPotRequest();
        globalPotRequest.setFollowerUser("user-123");
        globalPotRequest.setGlobalPotId("pot-123");

        GlobalPot globalPot = new GlobalPot();
        globalPot.setGlobalPotId("pot-123");

        Follower newfollower = Follower.builder()
                .followerUser(followerUser)
                .globalPot(globalPot)
                .build();

        //stubs
        when(validations.getUserInfo("user-123"))
                .thenReturn(followerUser);
        when(globalPotRepository.findById("pot-123"))
                .thenReturn(Optional.of(globalPot));
        when(followerRepository.save(any(Follower.class)))
                .thenReturn(newfollower);

        //When
        GlobalPotResponse globalPotResponse =
                addFollowersService.perform(globalPotRequest);

        //Then
        Assertions.assertEquals("Gloable Pot Followed Successfully",
                globalPotResponse.getMessage());
        verify(validations).getUserInfo("user-123");
        verify(globalPotRepository).findById("pot-123");
        verify(followerRepository).save(any(Follower.class));
    }
}
