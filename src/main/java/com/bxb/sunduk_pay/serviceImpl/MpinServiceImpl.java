package com.bxb.sunduk_pay.serviceImpl;


import com.bxb.sunduk_pay.factories.mpinFactory.MpinOperation;
import com.bxb.sunduk_pay.factories.mpinFactory.MpinOperationsFactory;
import com.bxb.sunduk_pay.request.MpinRequest;
import com.bxb.sunduk_pay.response.MpinResponse;
import com.bxb.sunduk_pay.service.MpinService;
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

/** Factory for obtaining MPIN operations based on request type. */
 private final MpinOperationsFactory mpinOperationsFactory;

 /**
     * Processes the MPIN request by delegating to the appropriate
     * MPIN operation based on the request type.
     *
     * @param request the MPIN request containing necessary data
     * @return the MPIN response after processing
     */
    @Override
    public MpinResponse mpinApi(final MpinRequest request) {
        MpinOperation mpinOperation = mpinOperationsFactory.getMpinOperation(
                request.getMpinRequestType());
        return mpinOperation.perform(request);
    }


}
