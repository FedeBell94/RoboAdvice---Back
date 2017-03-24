package it.uiip.digitalgarage.roboadvice.businesslogic.controller;

import it.uiip.digitalgarage.roboadvice.businesslogic.exception.BadRequestException;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.BackTestingDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.PortfolioDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.StrategyDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.AbstractResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.ErrorResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.SuccessResponse;
import it.uiip.digitalgarage.roboadvice.core.CoreTask;
import it.uiip.digitalgarage.roboadvice.persistence.model.*;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.PortfolioRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.UserRepository;
import it.uiip.digitalgarage.roboadvice.utils.CustomDate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "securedApi")
public class PortfolioRESTController {

    private static final Log LOGGER = LogFactory.getLog(PortfolioRESTController.class);

    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final DataRepository dataRepository;
    private final AssetRepository assetRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public PortfolioRESTController(final UserRepository userRepository, final PortfolioRepository portfolioRepository,
                                   final DataRepository dataRepository, final AssetRepository assetRepository,
                                   final ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.portfolioRepository = portfolioRepository;
        this.dataRepository = dataRepository;
        this.assetRepository = assetRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * This method returns the portfolio history for the user which called the method. The parameter from is optional.
     * If it is not specified this method returns all the portfolio history of the user from the first day of
     * registration to the last one computed. Otherwise this method returns all the portfolio history from the 'from'
     * date to the last one computed.
     * The required format for the date is yyyy-MM-dd.
     *
     * @param authentication Represents the authentication token of an authenticated request.
     * @param from           Optional - The date from when the portfolio history is needed. The required format id
     *                       yyyy-MM-dd.
     * @return An {@link AbstractResponse} containing the list of {@link PortfolioDTO} requested.
     */
    @RequestMapping(value = "/portfolio", method = RequestMethod.GET)
    public AbstractResponse requestMyData(Authentication authentication,
                                          @RequestParam(required = false)
                                          @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from) {
        User user = userRepository.findByUsername(authentication.getName());

        Date fromDate = from == null ? user.getRegistration() : Date.valueOf(from);
        List<PortfolioDTO> portfolio = portfolioRepository.findPortfolioHistory(user, fromDate);
        LOGGER.debug("User: " + user.getUsername() + " - get portfolio called.");
        return new SuccessResponse<>(portfolio);
    }

    @RequestMapping(value = "/backTesting", method = RequestMethod.POST)
    public AbstractResponse backTesting(@RequestBody BackTestingDTO backtestingDTO) {

        if (backtestingDTO.getStrategy() == null) {
            return new ErrorResponse("Missing parameter: STRATEGY.");
        }

        Date fromDate = backtestingDTO.getFrom() == null ? CustomDate.getToday().getDayFromSql(-500) :
                backtestingDTO.getFrom();

        User user = User.builder()
                .registration(fromDate)
                .lastPortfolioComputation(fromDate)
                .build();

        List<Strategy> activeStrategy = new ArrayList<>(backtestingDTO.getStrategy().size());
        for (StrategyDTO currStrategy : backtestingDTO.getStrategy()) {
            Strategy insertStrategy = Strategy.builder()
                    .assetClass(AssetClass.builder().id(currStrategy.getAssetClassId()).build())
                    .percentage(currStrategy.getPercentage())
                    .startingDate(fromDate)
                    .user(user)
                    .build();
            activeStrategy.add(insertStrategy);
        }

        Iterable<Asset> assets = assetRepository.findAll();
        CustomDate customDate = new CustomDate(fromDate);

        CustomDate today = CustomDate.getToday();
        List<Portfolio> lastPortfolio = new ArrayList<>();
        List<Portfolio> portfolioList = new ArrayList<>();
        while (customDate.moveOneDayForward().compareTo(today) <= 0) {
            Map<Long, BigDecimal> latestAssetPrice = getLatestAssetPrices(assets, customDate.getDateSql());
            lastPortfolio = CoreTask.executeTask(user, lastPortfolio, activeStrategy, latestAssetPrice, assets, null);
            portfolioList.addAll(lastPortfolio);
        }

        List<PortfolioDTO> returnListDTO = new ArrayList<>(portfolioList.size());
        for (Portfolio p : portfolioList) {
            returnListDTO.add(modelMapper.map(p, PortfolioDTO.class));
        }

        long curAssetClassID = 0;
        java.util.Date curDate = new Date(Long.MIN_VALUE);
        List<PortfolioDTO> returnAggregatedListDTO = new ArrayList<>();

        for (int i = 0; i < returnListDTO.size(); i++) {
            if (returnListDTO.get(i).getDate().compareTo(curDate) == 0) {
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

        LOGGER.debug("Back-test called from date " + backtestingDTO.getFrom());
        return new SuccessResponse<>(returnAggregatedListDTO);
    }


    private Map<Long, BigDecimal> getLatestAssetPrices(final Iterable<Asset> assets, final Date date) {
        // TODO make it faster
        Map<Long, BigDecimal> latestPrices = new HashMap<>();
        for (Asset curr : assets) {
            Data data = dataRepository.findTop1ByDateLessThanEqualAndAssetOrderByDateDesc(date, curr);
            latestPrices.put(data.getAsset().getId(), data.getValue());
        }
        return latestPrices;
    }

}
