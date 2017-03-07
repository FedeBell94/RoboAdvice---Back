package it.uiip.digitalgarage.roboadvice.businesslogic.model.dto;

import it.uiip.digitalgarage.roboadvice.persistence.model.Portfolio;
import it.uiip.digitalgarage.roboadvice.persistence.model.Strategy;
import lombok.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simone on 06/03/2017.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PortfolioDTO {

    private List<GraphsDTO> graphs;
    private List<DataDTO> data;


}
