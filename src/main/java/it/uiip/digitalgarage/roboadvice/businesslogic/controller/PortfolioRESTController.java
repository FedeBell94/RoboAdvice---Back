package it.uiip.digitalgarage.roboadvice.businesslogic.controller;

import it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate.dateProvider.DateProvider;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.DailyWorthDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.DataDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.GraphsDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.PortfolioDTO;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

    @RequestMapping(value = "/portfolio", method = RequestMethod.GET)
    public @ResponseBody AbstractResponse requestMyData(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName());

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -91);
        Date ddate = new java.sql.Date(cal.getTimeInMillis());
        List<Object[]> ol = portfolioRepository.findData(user, ddate);
        ArrayList<GraphsDTO> gdto = new ArrayList<>();
        gdto.add(GraphsDTO.builder().title("Bonds").valueField("column1").build());
        gdto.add(GraphsDTO.builder().title("Forex").valueField("column2").build());
        gdto.add(GraphsDTO.builder().title("Stocks").valueField("column3").build());
        gdto.add(GraphsDTO.builder().title("Commodities").valueField("column4").build());
        ArrayList<DataDTO> ddto = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < ol.size(); i = i + 4) {

            ddto.add(DataDTO.builder()
                    .date(df.format(ol.get(i)[1]))
                    .column1((BigDecimal) ol.get(i)[0])
                    .column2((BigDecimal) ol.get(i + 1)[0])
                    .column3((BigDecimal) ol.get(i + 2)[0])
                    .column4((BigDecimal) ol.get(i + 3)[0])
                    .build()
            );


        }
        return new SuccessResponse<>(PortfolioDTO.builder().graphs(gdto).data(ddto).build());
    }

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
        List<Map<?, ?>> worthPerDay = portfolioRepository
                .findWorthPerDay(user, dateProvider.getDayFromToday(-365), dateProvider.getToday());

        Map<String, Object> returnResponse = new HashMap<>();
        returnResponse.put("data", worthPerDay);
        List<Object> graphsConfig = new LinkedList<>();
        graphsConfig.add(GraphsDTO.builder().valueField("value").title("Daily worth").build());
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
        List<Map<?, ?>> todayWorth = portfolioRepository.findWorthDayPerAssetClass(user, dateProvider.getToday());
        List<Map<?, ?>> yesterdayWorth =
                portfolioRepository.findWorthDayPerAssetClass(user, dateProvider.getYesterday());

        List<DailyWorthDTO> returnResponse = new LinkedList<>();
        for (Map<?, ?> curr : todayWorth) {
            Map<?, ?> yesterday = null;
            for (Map<?, ?> currYesterday : yesterdayWorth) {
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
}
