package it.uiip.digitalgarage.roboadvice.businesslogic.controller;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.DemoDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.PortfolioDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.StrategyDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.AbstractResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.ErrorResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.SuccessResponse;
import it.uiip.digitalgarage.roboadvice.core.CoreTask;
import it.uiip.digitalgarage.roboadvice.persistence.model.*;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import it.uiip.digitalgarage.roboadvice.utils.CustomDate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "securedApi")
public class DemoRESTController {

    private static final Log LOGGER = LogFactory.getLog(DemoRESTController.class);

    private final AssetRepository assetRepository;
    private final DataRepository dataRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public DemoRESTController(final AssetRepository assetRepository, final DataRepository dataRepository,
                              final ModelMapper modelMapper) {
        this.assetRepository = assetRepository;
        this.dataRepository = dataRepository;
        this.modelMapper = modelMapper;
    }

    @RequestMapping(value = "/demo", method = RequestMethod.POST)
    public AbstractResponse requestDemo(@RequestBody DemoDTO demoDTO) {

        if (demoDTO.getFrom() == null || demoDTO.getStrategy() == null || demoDTO.getWorth() == null) {
            return new ErrorResponse("Missing parameter/s.");
        }
        if (demoDTO.getTo() == null) {
            demoDTO.setTo(new CustomDate(demoDTO.getFrom()).getDayFromSql(1));
        }

        CustomDate fromDate = new CustomDate(new CustomDate(demoDTO.getFrom()).getYesterdayLocalDate());

        User user = User.builder()
                .registration(fromDate.getYesterdaySql())
                .lastPortfolioComputation(fromDate.getDateSql())
                .build();

        List<Strategy> activeStrategy = new ArrayList<>(demoDTO.getStrategy().size());
        for (StrategyDTO currStrategy : demoDTO.getStrategy()) {
            Strategy insertStrategy = Strategy.builder()
                    .assetClass(AssetClass.builder().id(currStrategy.getAssetClassId()).build())
                    .percentage(currStrategy.getPercentage())
                    .startingDate(fromDate.getDateSql())
                    .user(user)
                    .build();
            activeStrategy.add(insertStrategy);
        }

        Iterable<Asset> assets = assetRepository.findAll();
        Map<Long, BigDecimal> latestAssetPrice = getLatestAssetPrices(assets, fromDate.getDateSql());

        List<Portfolio> lastPortfolio = new ArrayList<>();
        lastPortfolio =
                CoreTask.executeTask(user, lastPortfolio, activeStrategy, latestAssetPrice, assets, demoDTO.getWorth());

        List<Portfolio> returnList = new ArrayList<>();
        while (fromDate.moveOneDayForward().compareTo(demoDTO.getTo()) < 0) {
            latestAssetPrice = getLatestAssetPrices(assets, fromDate.getDateSql());
            lastPortfolio = CoreTask.executeTask(user, lastPortfolio, activeStrategy, latestAssetPrice, assets, null);
            returnList.addAll(lastPortfolio);
        }

        List<PortfolioDTO> returnListDTO = new ArrayList<>(returnList.size());
        for (Portfolio p : returnList) {
            returnListDTO.add(modelMapper.map(p, PortfolioDTO.class));
        }

        long curAssetClassID = 0;
        java.util.Date curDate = null;
        List<PortfolioDTO> returnAggregatedListDTO = new ArrayList<>();

        for (int i = 0; i < returnListDTO.size(); i++) {
            if (returnListDTO.get(i).getDate() == curDate) {
                if (returnListDTO.get(i).getAssetClassId() == curAssetClassID) {
                    returnAggregatedListDTO.get(returnAggregatedListDTO.size() - 1).setValue(returnAggregatedListDTO.get(returnAggregatedListDTO.size() - 1).getValue().add(returnListDTO.get(i).getValue()));
                } else {
                    curAssetClassID = returnListDTO.get(i).getAssetClassId();
                    returnAggregatedListDTO.add(PortfolioDTO.builder().assetClassId(curAssetClassID).date(curDate).value(returnListDTO.get(i).getValue()).build());
                }
            } else {
                curDate = returnListDTO.get(i).getDate();
                curAssetClassID = returnListDTO.get(i).getAssetClassId();
                returnAggregatedListDTO.add(PortfolioDTO.builder().assetClassId(curAssetClassID).date(curDate).value(returnListDTO.get(i).getValue()).build());
            }
        }

        LOGGER.debug("Back-test called from date " + demoDTO.getFrom());
        return new SuccessResponse<>(returnAggregatedListDTO);
    }

    private Map<Long, BigDecimal> getLatestAssetPrices(final Iterable<Asset> assets, final Date date) {
        // TODO make a class utility that returns this
        // TODO make it faster
        Map<Long, BigDecimal> latestPrices = new HashMap<>();
        for (Asset curr : assets) {
            Data data = dataRepository.findTop1ByDateLessThanEqualAndAssetOrderByDateDesc(date, curr);
            latestPrices.put(data.getAsset().getId(), data.getValue());
        }
        return latestPrices;
    }

}
