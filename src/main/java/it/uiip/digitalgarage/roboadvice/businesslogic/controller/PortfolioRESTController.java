package it.uiip.digitalgarage.roboadvice.businesslogic.controller;

import it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate.dateProvider.DateProvider;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.DailyWorthDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.GraphPortfolioHistoryDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.GraphSettingsDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.PortfolioHistoryDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.AbstractResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.SuccessResponse;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import it.uiip.digitalgarage.roboadvice.persistence.repository.PortfolioRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "securedApi")
public class PortfolioRESTController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    private static final Log LOGGER = LogFactory.getLog(PortfolioRepository.class);

    /**
     * Returns the worth history for the {@link User} in the required format.
     *
     * @param authentication
     *         Represents the token for an authentication request or for an authenticated {@link User}.
     *
     * @return The history of the worth in the format required.
     */
    @RequestMapping(value = "/worthHistory", method = RequestMethod.GET)
    public @ResponseBody AbstractResponse worthHistory(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName());

        DateProvider dateProvider = new DateProvider();
        List<Map<Object, Object>> worthPerDay = portfolioRepository
                .findWorthPerDay(user, dateProvider.getDayFromToday(-365), dateProvider.getToday());

        Map<String, Object> returnResponse = new HashMap<>();
        returnResponse.put("data", worthPerDay);
        List<Object> graphsConfig = new LinkedList<>();
        graphsConfig.add(GraphSettingsDTO.builder().valueField("value").title("Daily worth").build());
        returnResponse.put("graphs", graphsConfig);

        LOGGER.debug("User: " + user.getUsername() + " - Worth history called.");
        return new SuccessResponse<>(returnResponse);
    }

    /**
     * Returns the worth for today for the {@link User} in the required format.
     *
     * @param authentication
     *         Represents the token for an authentication request or for an authenticated {@link User}.
     *
     * @return The worth of today for the caller user in the format required.
     */
    @RequestMapping(value = "/worthDay", method = RequestMethod.GET)
    public @ResponseBody AbstractResponse worthDay(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName());

        DateProvider dateProvider = new DateProvider();
        List<Map<Object, Object>> todayWorth =
                portfolioRepository.findWorthDayPerAssetClass(user, dateProvider.getToday());
        List<Map<Object, Object>> yesterdayWorth =
                portfolioRepository.findWorthDayPerAssetClass(user, dateProvider.getYesterday());

        List<DailyWorthDTO> returnResponse = new LinkedList<>();
        for (Map<Object, Object> curr : todayWorth) {
            Map<Object, Object> yesterday = null;
            for (Map<Object, Object> currYesterday : yesterdayWorth) {
                if (currYesterday.get("assetClass").equals(curr.get("assetClass"))) {
                    yesterday = currYesterday;
                    break;
                }
            }

            BigDecimal percentage = null;
            BigDecimal todayValue = (BigDecimal) curr.get("value");
            BigDecimal yesterdayValue = (BigDecimal) yesterday.get("value");
            if (yesterdayValue.compareTo(BigDecimal.ZERO) != 0) {
                percentage = todayValue.divide(yesterdayValue, 4).subtract(BigDecimal.valueOf(1));
            }
            BigDecimal profLoss = todayValue.subtract(yesterdayValue);

            returnResponse.add(DailyWorthDTO.builder()
                    .assetClass((String) curr.get("assetClass"))
                    .value((BigDecimal) curr.get("value"))
                    .percentage(percentage)
                    .profLoss(profLoss).build());
        }

        LOGGER.debug("User: " + user.getUsername() + " - Worth day called.");
        return new SuccessResponse<>(returnResponse);
    }

    /**
     * This method returns the history of the portfolio of the caller {@link User}.
     *
     * @param authentication
     *         Represents the token for an authentication request or for an authenticated {@link User}.
     *
     * @return The history of the portfolio of the caller {@link User}
     */
    @RequestMapping(value = "/portfolio", method = RequestMethod.GET)
    public @ResponseBody AbstractResponse requestMyData(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName());

        DateProvider dateProvider = new DateProvider();
        List<Map<Object, Object>> portfolioHistory = portfolioRepository
                .findPortfolioHistory(user, dateProvider.getDayFromToday(-91), dateProvider.getToday());

        // Compute the portfolio part
        List<GraphPortfolioHistoryDTO> portfolioList = new ArrayList<>();
        Iterator<Map<Object, Object>> portfolioIt = portfolioHistory.iterator();
        while (portfolioIt.hasNext()) {
            Map<Object, Object> first = portfolioIt.next();
            GraphPortfolioHistoryDTO graph = GraphPortfolioHistoryDTO.builder()
                    .date((Date) first.get("date"))
                    .bonds((BigDecimal) first.get("value"))
                    .forex((BigDecimal) portfolioIt.next().get("value"))
                    .stocks((BigDecimal) portfolioIt.next().get("value"))
                    .commodities((BigDecimal) portfolioIt.next().get("value")).build();
            portfolioList.add(graph);
        }

        // Set the graph settings
        List<GraphSettingsDTO> graphSettings = new ArrayList<>();
        graphSettings.add(GraphSettingsDTO.builder().title("Bonds").valueField("bonds").build());
        graphSettings.add(GraphSettingsDTO.builder().title("Forex").valueField("forex").build());
        graphSettings.add(GraphSettingsDTO.builder().title("Stocks").valueField("stocks").build());
        graphSettings.add(GraphSettingsDTO.builder().title("Commodities").valueField("commodities").build());

        PortfolioHistoryDTO returnData = PortfolioHistoryDTO.builder()
                .data(portfolioList).graphs(graphSettings).build();
        LOGGER.debug("User: " + user.getUsername() + " - Worth history called.");
        return new SuccessResponse<>(returnData);
    }
}
