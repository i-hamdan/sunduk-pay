package com.bxb.sunduk_pay.validations;

import com.bxb.sunduk_pay.model.GlobalPot;

public interface GlobalPotValidations {

    GlobalPot getGlobalPot(String globalPotId);

    int getContributorsCount(String globalPotId);

    int getFollowersCount(String globalPotId);
}
