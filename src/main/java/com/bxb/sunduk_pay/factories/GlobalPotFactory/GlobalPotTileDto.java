package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalPotTileDto {

    private String globalPotId;
    private String caseTitle;
    private String caseCategory;

  //  private Boolean isUrgent;
    private Boolean isVerified;

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

    private Integer contributorCount;
    private Integer followerCount;
}
