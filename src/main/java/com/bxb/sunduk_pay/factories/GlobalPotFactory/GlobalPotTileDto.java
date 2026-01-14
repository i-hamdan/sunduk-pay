package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GlobalPotTileDto {
    /** Unique identifier for the global pot. */
    private String globalPotId;
    /** Title of the case associated with the global pot. */
    private String caseTitle;
    /** Category of the case associated with the global pot. */
    private String caseCategory;
    /** Short description of the global pot. */

    private String description;

    private String caseRequirementType;

    private Boolean isVerified;
    /** Indicates if the global pot is currently active. */
    private Boolean isActive;

    /** Primary image for the global pot. */
    private String primaryImage;
    /** Secondary image for the global pot. */
    private String secondaryImage;
    /** Tertiary image for the global pot. */
    private String tertiaryImage;

    /** City where the global pot is located. */
    private String city;
    /** Country where the global pot is located. */
    private String country;

    /** Current balance of the global pot. */
    private Double currentBalance;
    /** Goal amount for the global pot. */
    private Double goalAmount;
    /** Number of contributors to the global pot. */
    private int contributorCount;
    /** Number of followers for the global pot. */
    private int followerCount;
}
