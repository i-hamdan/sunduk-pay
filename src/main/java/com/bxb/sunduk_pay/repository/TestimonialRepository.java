package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.Testimonial;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing Testimonial entities in the database.
 */
public interface TestimonialRepository extends JpaRepository<Testimonial,
        String> {
}
