package it.uiip.digitalgarage.roboadvice.businesslogic.model.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder @ToString
public class StrategyDTO  {

    private Integer assetClassId;

    private BigDecimal percentage;
}
