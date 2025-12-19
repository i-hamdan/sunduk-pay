package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.model.Testimonial;
import com.bxb.sunduk_pay.request.TestimonialRequest;

public interface TestimonialMapper {
    Testimonial toEntity(TestimonialRequest request);
}
