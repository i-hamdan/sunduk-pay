package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.request.PhotoRequest;
import com.bxb.sunduk_pay.util.PhotoRequestType;
import org.springframework.web.multipart.MultipartFile;

public interface PhotoMapper {
PhotoRequest toRequest(String uuid, MultipartFile photoData,
                       PhotoRequestType type);

}
