package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.model.Follower;
import com.bxb.sunduk_pay.request.FollowerRequest;
/**
 * Mapper interface for converting FollowerRequest
 * to Follower entity.
 */
public interface FollowerMapper {
    /**
     * @param request
     * @return
     */
    Follower toEntity(FollowerRequest request);
}
