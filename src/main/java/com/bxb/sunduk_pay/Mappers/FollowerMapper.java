package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.model.Follower;
import com.bxb.sunduk_pay.request.FollowerRequest;

public interface FollowerMapper {
    Follower toEntity(FollowerRequest request);
}
