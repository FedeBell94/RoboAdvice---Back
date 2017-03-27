package it.uiip.digitalgarage.roboadvice.service.backTestingTask;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.PortfolioDTO;
import it.uiip.digitalgarage.roboadvice.persistence.model.Portfolio;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PortfolioConversionUtil {

    /**
     * This method convert from {@link List} of {@link Portfolio} (13 rows for each portfolio) to {@link List} of {@link
     * PortfolioDTO} (4 rows for each).
     * THIS METHOD WORKS ONLY FOR A SINGLE PORTFOLIO!!! DO NOT PASS A LIST OF
     * PORTFOLIO AS INPUT CONTAINING THE PORTFOLIOS FOR DIFFERENT DAYS, IT WON'T WORK.
     *
     * @param portfolio The {@link List} of {@link Portfolio} to be converted.
     *
     * @return The {@link List} of {@link PortfolioDTO} corresponding to the one passed as input.
     */
    public List<PortfolioDTO> convertPortfolio(List<Portfolio> portfolio) {
        Map<Long, BigDecimal> tempMap = new HashMap<>();
        for (Portfolio currPort : portfolio) {
            Long currAssetClassId = currPort.getAssetClass().getId();
            BigDecimal tempValue = tempMap.get(currAssetClassId);
            if (tempValue == null) {
                tempMap.put(currAssetClassId, currPort.getValue());
            } else {
                tempValue = tempValue.add(currPort.getValue());
                tempMap.put(currAssetClassId, tempValue);
            }
        }

        Date date = portfolio.get(0).getDate();
        List<PortfolioDTO> returnList = new ArrayList<>(tempMap.size());
        for (Map.Entry<Long, BigDecimal> currPortMap : tempMap.entrySet()) {
            PortfolioDTO lastPortfolio = PortfolioDTO.builder()
                    .assetClassId(currPortMap.getKey())
                    .value(currPortMap.getValue())
                    .date(date)
                    .build();
            returnList.add(lastPortfolio);
        }
        return returnList;
    }
}
