package it.uiip.digitalgarage.roboadvice.businesslogic.model.dto;


import lombok.*;

import java.math.BigDecimal;

@Setter @Getter @AllArgsConstructor @NoArgsConstructor @Builder
public class DailyWorthDTO {

    private String assetClass;
    private BigDecimal value;
    private BigDecimal profLoss;
    private BigDecimal percentage;
}
