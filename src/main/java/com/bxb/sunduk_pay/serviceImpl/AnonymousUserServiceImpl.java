package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.exception.CannotCreateAnonymousUserException;
import com.bxb.sunduk_pay.model.AnonymousUser;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.AnonymousUserRepository;
import com.bxb.sunduk_pay.response.AnonymousIdentityDTO;
import com.bxb.sunduk_pay.service.AnonymousUserService;
import com.bxb.sunduk_pay.util.AnonymousColorCreator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Service for managing anonymous users within global pots.
 * Responsible for assigning and retrieving anonymous colors
 * for users in specific pots.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class AnonymousUserServiceImpl implements AnonymousUserService {

    /**
     * Entity manager for database operations.
     */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Repository for managing anonymous user data.
     */
    private final AnonymousUserRepository anonymousUserRepository;
    /**
     * Utility for generating anonymous user colors.
     */
    private final AnonymousColorCreator anonymousColorCreator;
    /**
     * Redis template for caching anonymous user colors.
     */
    private final RedisTemplate<String, String> redisTemplate;
    /**
     * Prefix for Redis keys related to anonymous user colors.
     */
    private static final String REDIS_PREFIX = "anonymous:pot:";


    /**
     * Retrieves the anonymous color for a user in a specific global pot.
     * If the user does not have an assigned color, a new one is created.
     *
     * @param user      the user for whom to retrieve or create the color
     * @param globalPot the global pot in which the user is participating
     * @return the anonymous color code assigned to the user
     */
    public AnonymousIdentityDTO  getOrCreateAnonymousColor(
            final User user,
            final GlobalPot globalPot) {

        log.info(
                "Fetching or creating anonymous color for user {} in pot {}",
                user.getUuid(),
                globalPot.getGlobalPotId()
        );
        return anonymousUserRepository.findByGlobalPotGlobalPotIdAndUserUuid(
                        globalPot.getGlobalPotId(), user.getUuid())
                .map(au -> new AnonymousIdentityDTO(
                        au.getAnonymousId(),
                        au.getColorCode()))
                .orElseGet(() -> createAnonymousUser(user, globalPot));
    }

    /**
     * Creates a new anonymous user entry with a unique color
     * for the specified user and global pot.
     *
     * @param user      the user for whom to create the anonymous entry
     * @param globalPot the global pot in which the user is participating
     * @return the newly assigned anonymous color code
     */
    private AnonymousIdentityDTO createAnonymousUser(
            final User user,
            final GlobalPot globalPot) {
        log.info(
                "Creating new anonymous user for user {} in pot {}",
                user.getUuid(),
                globalPot.getGlobalPotId()
        );
        String counterKey = REDIS_PREFIX
                + globalPot.getGlobalPotId() + ":colorCounter";

        while (true) {
            Long index = redisTemplate.opsForValue().increment(counterKey);

            String color = anonymousColorCreator.generateColor(index);
            log.info(
                    "Generated color {} for anonymous user {} in pot {}",
                    color,
                    user.getUuid(),
                    globalPot.getGlobalPotId()
            );

            try {
                AnonymousUser anonymousUser = AnonymousUser.builder()
                        .user(user)
                        .globalPot(globalPot)
                        .colorCode(color)
                        .build();

                anonymousUserRepository.saveAndFlush(anonymousUser);
                return new AnonymousIdentityDTO(anonymousUser.getAnonymousId(),
                        anonymousUser.getColorCode());
            } catch (
                    org.springframework.dao.DataIntegrityViolationException e) {
                log.warn(
                        "Anonymous color collision for pot {}, retrying",
                        globalPot.getGlobalPotId()
                );
                entityManager.clear();
            } catch (Exception e) {
                log.error(
                        "Failed to create anonymous user for pot {}: {}",
                        globalPot.getGlobalPotId(),
                        e.getMessage()
                );
                throw new CannotCreateAnonymousUserException(
                        "Cannot create anonymous user: " + e.getMessage());
            }
        }
    }

}
