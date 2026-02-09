package com.bxb.sunduk_pay.factories.adminFactory;

import com.bxb.sunduk_pay.model.GlobalLandingPage;
import com.bxb.sunduk_pay.repository.GlobalLandingPageRepository;
import com.bxb.sunduk_pay.request.SundukPayAdminRequest;
import com.bxb.sunduk_pay.response.SundukPayAdminResponse;
import com.bxb.sunduk_pay.util.AdminRequestType;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Admin operation implementation for changing the default landing page
 * of the Sunduk Pay application. This operation allows administrators
 * to update the default page that users see when they access the app.
 */
@Service
@RequiredArgsConstructor
public class ChangeDefaultLandingPage implements SundukPayAdminOperation {

private static final String LANDING_PAGE_KEY = "DEFAULT_LANDING_PAGE";

    /** Validations for admin operations. */
    private final Validations validations;

    /** Repository for Global Landing Page operations. */
    private final GlobalLandingPageRepository globalLandingPageRepository;

    /**
     * Returns the admin request type that this operation handles.
     *
     * @return AdminRequestType.CHANGE_LANDING_PAGE
     */
    @Override
    public AdminRequestType getAdminRequestType() {
        return AdminRequestType.CHANGE_LANDING_PAGE;
    }

    /**
     * Performs the operation to change the default landing page based
     on the provided request.
     * @param request the admin request containing necessary data to change
    the landing page
     * @return SundukPayAdminResponse indicating the result of the operation
     */
    @Override
    public SundukPayAdminResponse perform(SundukPayAdminRequest request) {
        GlobalLandingPage globalLandingPage = globalLandingPageRepository
                .findByKey(LANDING_PAGE_KEY).orElseGet(() ->
                GlobalLandingPage.builder().key(LANDING_PAGE_KEY)
                        .value(request.getLandingPageUrl()).build()
        );
        globalLandingPageRepository.save(globalLandingPage);
        return SundukPayAdminResponse.builder().message(
                "Default landing page updated successfully").build();
    }
}
