package it.uiip.digitalgarage.roboadvice.businesslogic.model.dto;

import lombok.*;


import java.sql.Date;
import java.util.List;

/**
 * Created by Simone on 22/03/2017.
 */
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder @ToString
public class DemoDTO {

    private List<StrategyDTO> strategyInput;

    private Integer worth;

    private Date from;

    private Date to;
}
