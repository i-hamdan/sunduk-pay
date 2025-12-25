package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GlobalPotTileDto {

    private String globalPotId;
    private String caseTitle;
    private String caseCategory;

    private Boolean isVerified;
    private Boolean isActive;

    // Images (BLOB → Base64)
    private String primaryImage;
    private String secondaryImage;
    private String tertiaryImage;

    // Location
    private String city;
    private String country;

    // Financials
    private Double currentBalance;
    private Double goalAmount;

    private int contributorCount;
    private int followerCount;
}
