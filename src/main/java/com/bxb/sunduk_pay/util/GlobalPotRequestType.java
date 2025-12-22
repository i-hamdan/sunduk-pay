package com.bxb.sunduk_pay.util;

public enum GlobalPotRequestType {
    CREATE_POT,
    UPDATE_POT,
    DELETE_POT,
    FETCH_POTS,

    CREATE_WALLET,
    UPDATE_WALLET,
    DELETE_WALLET,
    FETCH_WALLETS,

    ADD_CONTRIBUTION, // Handled by AddContributorService
    FOLLOW_POT,       // Handled by FollowPotService
    ADD_TESTIMONIAL

}
