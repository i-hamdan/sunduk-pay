package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.exception.CannotCreateAnonymousUserException;
import com.bxb.sunduk_pay.model.AnonymousUser;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.AnonymousUserRepository;
import com.bxb.sunduk_pay.response.AnonymousIdentityDTO;
import com.bxb.sunduk_pay.util.AnonymousColorCreator;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnonymousUserServiceImplTest {

    @InjectMocks
    private AnonymousUserServiceImpl anonymousUserService;

    @Mock
    private AnonymousUserRepository anonymousUserRepository;

    @Mock
    private AnonymousColorCreator anonymousColorCreator;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private EntityManager entityManager;


    @BeforeEach
    void setUp() {

    }

    /* -------------------- Helper Methods -------------------- */

    private User getUser() {
        return User.builder()
                .uuid("user_id")
                .fullName("Test User")
                .build();
    }

    private GlobalPot getGlobalPot() {
        return GlobalPot.builder()
                .globalPotId("global_pot_id")
                .caseTitle("Test Pot")
                .build();
    }

    private AnonymousUser getAnonymousUser() {
        return AnonymousUser.builder()
                .anonymousId("anon_123")
                .colorCode("#FF5733")
                .user(getUser())
                .globalPot(getGlobalPot())
                .build();
    }

    /* -------------------- Test Cases -------------------- */

    @Test
    void shouldReturnExistingAnonymousIdentity_whenUserAlreadyExists() {
        User user = getUser();
        GlobalPot globalPot = getGlobalPot();
        AnonymousUser anonymousUser = getAnonymousUser();

        when(anonymousUserRepository
                .findByGlobalPotGlobalPotIdAndUserUuid(
                        globalPot.getGlobalPotId(),
                        user.getUuid()))
                .thenReturn(Optional.of(anonymousUser));

        AnonymousIdentityDTO response =
                anonymousUserService.getOrCreateAnonymousColor(user, globalPot);

        assertNotNull(response);

        verify(anonymousUserRepository, times(1))
                .findByGlobalPotGlobalPotIdAndUserUuid(
                        globalPot.getGlobalPotId(),
                        user.getUuid());

        verify(anonymousUserRepository, never()).save(any());
    }

//    @Test
//    void shouldCreateAnonymousIdentity_whenUserDoesNotExist() {
//        User user = getUser();
//        GlobalPot globalPot = getGlobalPot();
//
//        when(anonymousUserRepository
//                .findByGlobalPotGlobalPotIdAndUserUuid(
//                        globalPot.getGlobalPotId(),
//                        user.getUuid()))
//                .thenReturn(Optional.empty());
//
//        when(anonymousColorCreator.generateColor(anyLong()))
//                .thenReturn("#00FF00");
//
//        when(anonymousUserRepository.save(any(AnonymousUser.class)))
//                .thenAnswer(invocation -> invocation.getArgument(0));
//
//        AnonymousIdentityDTO response =
//                anonymousUserService.getOrCreateAnonymousColor(user, globalPot);
//
//        assertNotNull(response);
//        assertNotNull(response.getAnonymousId());
//        assertEquals("#00FF00", response.getAnonymousColor());
//
//        verify(anonymousUserRepository, times(1))
//                .save(any(AnonymousUser.class));
//    }

    @Test
    void shouldCreateAnonymousIdentity_whenUserDoesNotExist() {
        User user = getUser();
        GlobalPot globalPot = getGlobalPot();

        when(anonymousUserRepository
                .findByGlobalPotGlobalPotIdAndUserUuid(
                        globalPot.getGlobalPotId(),
                        user.getUuid()))
                .thenReturn(Optional.empty());

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(1L);

        // Redis index simulation
        when(valueOperations.increment(anyString()))
                .thenReturn(1L);

        // Color generation using index
        when(anonymousColorCreator.generateColor(anyLong()))
                .thenReturn("#00FF00");

        // IMPORTANT: mock saveAndFlush (not save)
        when(anonymousUserRepository.saveAndFlush(any(AnonymousUser.class)))
                .thenAnswer(invocation -> {
                    AnonymousUser au = invocation.getArgument(0);
                    au.setAnonymousId("anon_generated_1");
                    return au;
                });

        AnonymousIdentityDTO response =
                anonymousUserService.getOrCreateAnonymousColor(user, globalPot);

        assertNotNull(response);
        assertEquals("anon_generated_1", response.getAnonymousId());
        assertEquals("#00FF00", response.getAnonymousColor());

        verify(redisTemplate).opsForValue();
        verify(valueOperations).increment(anyString());
        verify(anonymousColorCreator).generateColor(anyLong());
        verify(anonymousUserRepository).saveAndFlush(any(AnonymousUser.class));
    }


//    @Test
//    void shouldRetryWhenDataIntegrityViolationOccurs_andThenSucceed() {
//        User user = getUser();
//        GlobalPot globalPot = getGlobalPot();
//
//        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
//
//        // First attempt → index 1, second attempt → index 2
//        when(valueOperations.increment(anyString()))
//                .thenReturn(1L, 2L);
//
//        when(anonymousColorCreator.generateColor(1L))
//                .thenReturn("#RED");
//        when(anonymousColorCreator.generateColor(2L))
//                .thenReturn("#GREEN");
//
//        // First save fails, second succeeds
//        when(anonymousUserRepository.saveAndFlush(any(AnonymousUser.class)))
//                .thenThrow(new org.springframework.dao.DataIntegrityViolationException("collision"))
//                .thenAnswer(invocation -> {
//                    AnonymousUser au = invocation.getArgument(0);
//                    au.setAnonymousId("anon_success");
//                    return au;
//                });
//
//        AnonymousIdentityDTO response =
//                anonymousUserService.getOrCreateAnonymousColor(user, globalPot);
//
//        assertNotNull(response);
//        assertEquals("anon_success", response.getAnonymousId());
//        assertEquals("#GREEN", response.getAnonymousColor());
//
//        verify(entityManager, times(1)).clear();
//        verify(anonymousUserRepository, times(2)).saveAndFlush(any());
//    }

    @Test
    void shouldThrowCannotCreateAnonymousUserException_whenUnexpectedExceptionOccurs() {
        User user = getUser();
        GlobalPot globalPot = getGlobalPot();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(1L);
        when(anonymousColorCreator.generateColor(anyLong()))
                .thenReturn("#RED");

        when(anonymousUserRepository.saveAndFlush(any(AnonymousUser.class)))
                .thenThrow(new RuntimeException("DB down"));

        CannotCreateAnonymousUserException exception =
                assertThrows(
                        CannotCreateAnonymousUserException.class,
                        () -> anonymousUserService.getOrCreateAnonymousColor(
                                user, globalPot)
                );

        assertTrue(exception.getMessage().contains("DB down"));

        verify(entityManager, never()).clear();
        verify(anonymousUserRepository, times(1))
                .saveAndFlush(any());
    }




}
