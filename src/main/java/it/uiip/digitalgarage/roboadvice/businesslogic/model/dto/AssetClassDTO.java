package it.uiip.digitalgarage.roboadvice.businesslogic.model.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by Simone on 10/03/2017.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class AssetClassDTO {


    private LocalDate date;
    private BigDecimal value;
}
