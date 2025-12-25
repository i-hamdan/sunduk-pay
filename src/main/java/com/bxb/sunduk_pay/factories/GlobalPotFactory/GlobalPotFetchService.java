package com.bxb.sunduk_pay.factories.GlobalPotFactory;
import com.bxb.sunduk_pay.Mappers.GlobalPotMapper;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.repository.GlobalPotRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.CaseCategory;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.util.PotScope;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Service used to fetch Global Pots.
 *
 * This class handles FETCH_GLOBAL_POT requests.
 * It fetches only PUBLIC global pots.
 *
 * If case category is:
 * - null or ALL → fetch all public global pots
 * - specific value → fetch public global pots for that category
 *
 * Data is fetched from database and converted into tile DTOs
 * before sending the response.
 */


@Service
@RequiredArgsConstructor
@Log4j2
public class GlobalPotFetchService implements GlobalPotOperation {
    /** Repository to access GlobalPot data */

    private final GlobalPotRepository globalPotRepository;
    /** Mapper to convert GlobalPot entity to response DTO */

    private final GlobalPotMapper globalPotMapper;
    /**
     * Returns the request type supported by this service.
     *
     * @return FETCH_GLOBAL_POT request type
     */
    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {
        return GlobalPotRequestType.FETCH_GLOBAL_POT;
    }
    /**
     * Executes the fetch global pot operation.
     *
     * Based on the request case category, this method:
     * - Fetches all public global pots
     * - Or fetches category wise public global pots
     *
     * @param request request containing filter details
     * @return response containing global pot tiles
     */

    @Override
    public GlobalPotResponse perform(GlobalPotRequest request) {
        log.info("Fetch global pot request received: {}", request);

        Pageable pageable = PageRequest.of(
                request.getPageNumber(),
                request.getPageSize()
        );

        Page<GlobalPot> pots;

        if (request.getCaseCategory() == null ||
                request.getCaseCategory() == CaseCategory.ALL){

            log.debug("Fetching all PUBLIC global pots");
            pots = globalPotRepository.findByPotScope(PotScope.PUBLIC, pageable);
        }
        else {
            log.debug("Fetching PUBLIC global pots for category: {}",
                    request.getCaseCategory());

            pots = globalPotRepository.findByCaseCategoryAndPotScope(
                    request.getCaseCategory(),
                    PotScope.PUBLIC,
                    pageable
            );
        }

        List<GlobalPotTileDto> tileDtos =
                globalPotMapper.toTileDtos(pots.getContent());

        log.info("Returning {} global pots",
                tileDtos.size());

        return GlobalPotResponse.builder()
                .status("SUCCESS")
                .message("global pots fetched successfully")
                .globalPotsTiles(tileDtos)
                .build();
    }
}
