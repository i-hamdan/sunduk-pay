package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.model.Contributer;
import com.bxb.sunduk_pay.request.ContributorRequest;

public interface ContributerMapper {
    Contributer toEntity(ContributorRequest request);
}
