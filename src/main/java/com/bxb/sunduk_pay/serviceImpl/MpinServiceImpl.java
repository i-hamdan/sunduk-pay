package com.bxb.sunduk_pay.serviceImpl;


import com.bxb.sunduk_pay.encryption.MpinEncryption;

import com.bxb.sunduk_pay.mpinFactory.MpinOperation;
import com.bxb.sunduk_pay.mpinFactory.MpinOperationsFactory;
import com.bxb.sunduk_pay.repository.MpinRepository;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.request.MpinRequest;
import com.bxb.sunduk_pay.response.MpinResponse;
import com.bxb.sunduk_pay.service.MpinService;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Service implementation for handling MPIN operations.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class MpinServiceImpl implements MpinService {

 private final MpinOperationsFactory mpinOperationsFactory;

    @Override
    public MpinResponse mpinApi(MpinRequest request) {
        MpinOperation mpinOperation = mpinOperationsFactory.getMpinOperation
                (request.getMpinRequestType());
        return mpinOperation.perform(request);
    }


}
