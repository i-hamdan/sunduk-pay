package com.bxb.sunduk_pay.util;

public enum GlobalPotRequestType {
    CREATE_POT,
    UPDATE_POT,
    DELETE_POT,
    FETCH_POTS,
    FETCH_POT_DETAILS,
    VERIFY_POT,

    CREATE_WALLET,
    UPDATE_WALLET,
    DELETE_WALLET,
    FETCH_WALLETS,

    ADD_CONTRIBUTOR, // Handled by AddContributorService
    FOLLOW_POT,       // Handled by FollowPotService
    ADD_TESTIMONIAL,
    FETCH_GLOBAL_POT

}
