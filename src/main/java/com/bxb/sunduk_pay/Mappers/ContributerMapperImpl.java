package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.model.Contributer;
import com.bxb.sunduk_pay.request.ContributorRequest;
import org.springframework.stereotype.Component;

@Component
public class ContributerMapperImpl implements ContributerMapper{
    /**
     * @param request
     * @return
     */
    @Override
    public Contributer toEntity(ContributorRequest request) {
        if (request == null) return null;

        Contributer entity = new Contributer();
        entity.setName(request.getName());
        entity.setAmountContributed(request.getAmountContributed() != null ? request.getAmountContributed() : 0.0);
        entity.setIsAnonymous(request.getIsAnonymous() != null ? request.getIsAnonymous() : false);
        entity.setIsUser(request.getIsUser() != null ? request.getIsUser() : true);
        entity.setProfileImage(request.getProfileImage());

        return entity;
    }
}
