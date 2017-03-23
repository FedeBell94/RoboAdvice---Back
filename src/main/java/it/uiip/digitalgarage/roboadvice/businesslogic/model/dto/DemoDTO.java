package it.uiip.digitalgarage.roboadvice.businesslogic.model.dto;

import lombok.*;


import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder @ToString
public class DemoDTO {

    private List<StrategyDTO> strategy;

    private BigDecimal worth;

    private Date from;

    private Date to;
}
