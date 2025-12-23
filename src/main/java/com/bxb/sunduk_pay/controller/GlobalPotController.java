package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.factories.GlobalPotFactory.CreateGlobalPotService;
import com.bxb.sunduk_pay.factories.GlobalPotFactory.GlobalPotOperation;
import com.bxb.sunduk_pay.factories.GlobalPotFactory.GlobalPotOperationFactory;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GlobalPotController {


    private final GlobalPotOperationFactory globalPotOperationFactory;

    public GlobalPotController(GlobalPotOperationFactory globalPotOperationFactory) {
        this.globalPotOperationFactory = globalPotOperationFactory;
    }


    @PostMapping("/global-pot")
    public ResponseEntity<GlobalPotResponse> globalPotApi(@ModelAttribute GlobalPotRequest request) {
        return ResponseEntity.ok(globalPotOperationFactory.performOperation(request)
);
    }
}

