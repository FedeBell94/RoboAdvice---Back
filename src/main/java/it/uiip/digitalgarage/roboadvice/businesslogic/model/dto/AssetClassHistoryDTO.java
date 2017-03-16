package it.uiip.digitalgarage.roboadvice.businesslogic.model.dto;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;


@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class AssetClassHistoryDTO {

    private Date date;

    private BigDecimal value;
}
