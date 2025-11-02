package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.request.MpinRequest;
import com.bxb.sunduk_pay.response.MpinResponse;
import com.bxb.sunduk_pay.service.MpinService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
/**
 * Controller for handling MPIN-related operations.
 */
public class MpinController {
    /**
     * Service for MPIN operations.
     */
    private final MpinService mpinService;
/**
 * * Constructor for MpinController.
 * @param mpinServices the MPIN service to be used
 */

    public MpinController(final MpinService mpinServices) {
        this.mpinService = mpinServices;
    }
    /**
     * Endpoint to set or update the MPIN for a user.
     *
     * @param request the request containing user UUID and new MPIN
     * @return response indicating success or failure of the operation
     */
    @PostMapping("/mpin")
    public ResponseEntity<MpinResponse> mpinApi(
            @RequestBody final MpinRequest request)
{
        log.info("Received MPIN set request for UUID: {}",
                request.getUuid() + "mpin " + request.getMpin());

            return ResponseEntity.ok(mpinService.mpinApi(request));
}



    }



