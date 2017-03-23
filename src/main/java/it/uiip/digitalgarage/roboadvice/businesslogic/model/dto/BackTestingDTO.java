package it.uiip.digitalgarage.roboadvice.businesslogic.model.dto;

import lombok.*;

import java.sql.Date;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BackTestingDTO {

    Date from;

    List<StrategyDTO> strategy;

}
