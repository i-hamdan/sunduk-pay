package com.bxb.sunduk_pay.model;

import com.bxb.sunduk_pay.util.AuthProviderMethod;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing an authentication provider linked to a user.
 */
@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class AuthProvider {

    /**
     * Unique identifier for the authentication provider.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String authProviderId;

    /**
     * The method of authentication (e.g., LOCAL_PHONE_EMAIL, GOOGLE).
     */
    @Enumerated(EnumType.STRING)
    private AuthProviderMethod authProviderMethod;

    /**
     * The unique identifier from the authentication provider (e.g., Google sub).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

}
