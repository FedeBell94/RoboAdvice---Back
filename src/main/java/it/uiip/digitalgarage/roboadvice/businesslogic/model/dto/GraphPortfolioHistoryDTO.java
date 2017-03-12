package it.uiip.digitalgarage.roboadvice.businesslogic.model.dto;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;


/**
 * Created by Simone on 06/03/2017.
 */
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder @ToString
public class GraphPortfolioHistoryDTO {

    private Date date;
    private BigDecimal bonds;
    private BigDecimal forex;
    private BigDecimal stocks;
    private BigDecimal commodities;
}
