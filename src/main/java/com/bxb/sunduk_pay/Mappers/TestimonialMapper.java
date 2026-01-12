package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.model.Testimonial;
import com.bxb.sunduk_pay.request.TestimonialRequest;
/**
 * Mapper interface for converting TestimonialRequest
 * to Testimonial entity.
 */
public interface TestimonialMapper {
    /**
     * @param request TestimonialRequest DTO
     * @return Testimonial entity
     */
    Testimonial toEntity(TestimonialRequest request);
}
