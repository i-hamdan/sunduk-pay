package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.request.PhotoRequest;
import com.bxb.sunduk_pay.util.PhotoRequestType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * PhotoMapperImpl is responsible for mapping photo
 * data to PhotoRequest objects.
 */
@Component
@RequiredArgsConstructor
public class PhotoMapperImpl implements PhotoMapper {
    /**
     *
     * @param uuid
     * @param photoData
     * @param type
     * @return
     */

    @Override
    public PhotoRequest toRequest(String uuid, MultipartFile photoData,
                                  PhotoRequestType type) {
        return null;
    }
}
