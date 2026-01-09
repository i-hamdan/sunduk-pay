package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.factories.GlobalPotFactory.GlobalPotOperationFactory;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class GlobalPotController {

   /** Factory for handling global pot operations. **/
    private final GlobalPotOperationFactory globalPotOperationFactory;

    /**
     * Handles global pot API requests.
     *
     * @param request the global pot request payload
     * @return ResponseEntity containing the global pot response
     */
    @PostMapping("/global-pot")
    public ResponseEntity<GlobalPotResponse> globalPotApi(
            @ModelAttribute final GlobalPotRequest request) throws IOException {
        return ResponseEntity.ok(globalPotOperationFactory
                .performOperation(request)
        );
    }
}

