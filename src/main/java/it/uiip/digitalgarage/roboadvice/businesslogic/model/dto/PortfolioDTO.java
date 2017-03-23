package it.uiip.digitalgarage.roboadvice.businesslogic.model.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PortfolioDTO implements Comparable<PortfolioDTO> {

    private BigDecimal value;

    private Date date;

    private Long assetClassId;

    @Override
    public int compareTo(PortfolioDTO o) {
        return getDate().compareTo(o.getDate());
    }

}
