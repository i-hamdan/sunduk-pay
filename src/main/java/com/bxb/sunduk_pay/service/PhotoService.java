package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.request.PhotoRequest;
import com.bxb.sunduk_pay.response.PhotoResponse;

public interface PhotoService {
    PhotoResponse uploadPhoto(PhotoRequest request);
}
