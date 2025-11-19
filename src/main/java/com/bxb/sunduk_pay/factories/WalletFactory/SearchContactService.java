package com.bxb.sunduk_pay.factories.WalletFactory;

import com.bxb.sunduk_pay.model.UserContact;
import com.bxb.sunduk_pay.repository.ContactRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.RequestType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
/**
 * Service to handle searching contacts by phone number.
 */
public class SearchContactService implements WalletOperation {

    /** Repository for accessing contact data. */
    private final ContactRepository contactRepository;
    /**
     * Returns the type of request this service handles.
     * @return RequestType.SEARCH_CONTACTS
     */
    @Override
    public RequestType getRequestType() {
        return RequestType.SEARCH_CONTACTS;
    }

    /**
     * @param mainWalletRequest the request containing
     *                          necessary data for the operation.
     * @return MainWalletResponse containing the result of the operation.
     */
    @Override
    public MainWalletResponse perform(MainWalletRequest mainWalletRequest) {
        String phone = mainWalletRequest.getPhone();
        log.info("Searching contact for phone: {}", phone);

        Optional<UserContact> userContactOpt = contactRepository
                .findByPhone(phone);

        if (userContactOpt.isPresent()) {
            UserContact userContact = userContactOpt.get();
            log.info("Found contact: {} with UPI ID: {}",
                    userContact.getName(), userContact.getUpiId());

            return MainWalletResponse.builder()
                    .status("SUCCESS")
                    .message("Contact found")
                    .name(userContact.getName())
                    .phone(userContact.getPhone())
                    .recipientUpiId(userContact.getUpiId())
                    .build();
        } else {
            log.warn("No contact found for phone: {}", phone);
            return MainWalletResponse.builder()
                    .name("New Number")
                    .phone(phone)
                    .recipientUpiId("")
                    .build();
        }
    }
}
