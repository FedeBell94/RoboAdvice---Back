package it.uiip.digitalgarage.roboadvice.businesslogic.model.dto;

import it.uiip.digitalgarage.roboadvice.persistence.model.Strategy;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder @ToString
public class StrategyDTO  {

    private Long assetClassId;

    private BigDecimal percentage;

    public StrategyDTO(Strategy strategy){
        this.assetClassId = strategy.getAssetClass().getId();
        this.percentage = strategy.getPercentage();
    }
}
