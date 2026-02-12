package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.Mappers.GlobalPotMapper;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.GlobalPotMembersRepository;
import com.bxb.sunduk_pay.repository.GlobalPotRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.CaseCategory;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.util.PotScope;
import com.bxb.sunduk_pay.util.UserRoles;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service used to fetch Global Pots.
 * <p>
 * This class handles FETCH_GLOBAL_POT requests.
 * It fetches only PUBLIC global pots.
 * <p>
 * If case category is:
 * - null or ALL → fetch all public global pots
 * - specific value → fetch public global pots for that category
 * <p>
 * Data is fetched from database and converted into tile DTOs
 * before sending the response.
 */


@Service
@RequiredArgsConstructor
@Log4j2
public class GlobalPotFetchService implements GlobalPotOperation {
    /**
     * Repository to access GlobalPot data.
     */
    private final GlobalPotRepository globalPotRepository;
    /**
     * Mapper to convert GlobalPot entity to response DTO.
     */
    private final GlobalPotMapper globalPotMapper;
    /**
     * Validations utility.
     */
    private final Validations validations;

    /**
     * Repository to manage Global Pot members.
     */
    private final GlobalPotMembersRepository globalPotMembersRepository;

    /**
     * Returns the request type supported by this service.
     *
     * @return FETCH_GLOBAL_POT request type.
     */
    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {
        return GlobalPotRequestType.FETCH_GLOBAL_POT;
    }

    /**
     * Executes the fetch global pot operation.
     * <p>
     * Based on the request case category, this method:
     * - Fetches all public global pots
     * - Or fetches category wise public global pots
     *
     * @param request request containing filter details
     * @return response containing global pot tiles.
     */

    @Override
    public GlobalPotResponse perform(final GlobalPotRequest request) {
        log.info("started fetch global pot operation: {}"
                , System.currentTimeMillis());
        log.info(
 "Fetching GlobalPot feed | userId={} page={} size={} category={}",
                request.getUuid(),
                request.getPageNumber(),
                request.getPageSize(),
                request.getCaseCategory());

        User user = validations.getUserInfo(request.getUuid());
        log.debug("Resolved user for feed | uuid={}", user.getUuid());


        Pageable pageable = PageRequest.of(
                request.getPageNumber(),
                request.getPageSize()
        );

        Page<GlobalPot> pots;
        log.info(
       "Determining fetch criteria based on case category: {}"
                , System.currentTimeMillis());
        if (request.getCaseCategory() == null
                || request.getCaseCategory() == CaseCategory.ALL) {
            log.info(
       "Fetching behavior-sorted feed for ALL categories | scope={}",
                    PotScope.PUBLIC
            );

            boolean isSundukPayAdmin = user.getUserRole() == UserRoles.SUNDUK_PAY_ADMIN;

            pots = globalPotRepository
                    .findFeedSortedByBehavior(user.getUuid(),
                            isSundukPayAdmin, pageable);


        } else {
            log.info(
      "Fetching behavior-sorted feed for category={} | scope={}",
                    request.getCaseCategory(),
                    PotScope.PUBLIC
            );

            boolean isPotAdmin = user.getUserRole()== UserRoles.GLOBALPOT_ADMIN;

            pots = globalPotRepository
                    .findCategoryFeedSortedByBehavior(
                            user.getUuid(),
                            request.getCaseCategory(),
                            isPotAdmin,
                            pageable
                    );
        }
        log.info(
    "Feed fetch done | totalElements={} pageElements={}",
                pots.getTotalElements(),
                pots.getNumberOfElements()
        );

        List<GlobalPotTileDto> tileDtos =
                globalPotMapper.toTileDtos(pots.getContent());

        log.debug(
                "Mapped GlobalPot entities to Tile DTOs | count={}",
                tileDtos.size()
        );
        log.info("Fetch global pot operation completed: {}"
                , System.currentTimeMillis());
        return GlobalPotResponse.builder()
                .status("SUCCESS")
                .message("global pots fetched successfully")
                .globalPotsTiles(tileDtos)
                .build();
    }
}
