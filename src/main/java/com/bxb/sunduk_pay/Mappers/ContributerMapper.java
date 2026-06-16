package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.model.Contributor;
import com.bxb.sunduk_pay.request.ContributorRequest;

/**
 * Mapper interface for converting ContributorRequest
 * to Contributor entity.
 */
public interface ContributerMapper {
    /**
     * @param request ContributorRequest DTO
     * @return Contributor entity
     */
    Contributor toEntity(ContributorRequest request);
}
