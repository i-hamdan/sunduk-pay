package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.request.MpinRequest;
import com.bxb.sunduk_pay.response.MpinResponse;

public interface MpinService {
    MpinResponse mpinApi(MpinRequest request);
}
