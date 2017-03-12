package it.uiip.digitalgarage.roboadvice.businesslogic.model.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * Created by Simone on 10/03/2017.
 */
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder @ToString
public class AssetClassDTO {

    private String date;
    private BigDecimal value;
}
