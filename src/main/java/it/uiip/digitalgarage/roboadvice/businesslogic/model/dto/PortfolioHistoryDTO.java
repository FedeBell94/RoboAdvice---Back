package it.uiip.digitalgarage.roboadvice.businesslogic.model.dto;

import lombok.*;

import java.util.List;

/**
 * Created by Simone on 06/03/2017.
 */

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder @ToString
public class PortfolioHistoryDTO {

    private List<GraphSettingsDTO> graphs;
    private List<GraphPortfolioHistoryDTO> data;
}
