package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.model.Contributor;
import com.bxb.sunduk_pay.request.ContributorRequest;

public interface ContributerMapper {
    Contributor toEntity(ContributorRequest request);
}
