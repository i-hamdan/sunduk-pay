package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.response.AnonymousIdentityDTO;

/**
 * Service interface for managing anonymous user identities.
 */
public interface AnonymousUserService {

    /**
     * Retrieves or creates an anonymous color identity for
     * a user within a global pot.
     *
     * @param user      the user for whom to retrieve or create the anonymous
     *                 identity
     * @param globalPot the global pot in which the user is participating
     * @return the anonymous identity data transfer object
     */
    AnonymousIdentityDTO getOrCreateAnonymousColor(
            User user,
            GlobalPot globalPot);
}
