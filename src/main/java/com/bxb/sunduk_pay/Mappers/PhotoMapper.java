package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.request.PhotoRequest;
import com.bxb.sunduk_pay.util.PhotoRequestType;
import org.springframework.web.multipart.MultipartFile;

/**
 * PhotoMapper interface for mapping photo data
 * to PhotoRequest objects.
 */
public interface PhotoMapper {
/**
     *
     * @param uuid
     * @param photoData
     * @param type
     * @return PhotoRequest
     */
PhotoRequest toRequest(String uuid, MultipartFile photoData,
                       PhotoRequestType type);

}
