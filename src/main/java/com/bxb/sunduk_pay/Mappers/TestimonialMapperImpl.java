package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.model.Testimonial;
import com.bxb.sunduk_pay.request.TestimonialRequest;
import org.springframework.stereotype.Component;

@Component
public class TestimonialMapperImpl implements TestimonialMapper {
    /**
     * @param request
     * @return
     */
    @Override
    public Testimonial toEntity(final TestimonialRequest request) {
        if (request == null) {
            return null;
        }
        Testimonial entity = new Testimonial();
        entity.setDetails(request.getDetails());
        entity.setAuthorName(request.getAuthorName());
        entity.setProfession(request.getProfession());
        entity.setProfileImage(request.getProfileImage());
        entity.setIsSundukAdmin(request.getIsSundukAdmin() != null
                ? request.getIsSundukAdmin() : false);

        return entity;
    }
}
