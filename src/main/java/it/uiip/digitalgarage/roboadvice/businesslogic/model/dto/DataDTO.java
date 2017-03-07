package it.uiip.digitalgarage.roboadvice.businesslogic.model.dto;

import lombok.*;

import java.math.BigDecimal;


/**
 * Created by Simone on 06/03/2017.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class DataDTO {

    private String date;
    private BigDecimal column1;
    private BigDecimal column2;
    private BigDecimal column3;
    private BigDecimal column4;
}
