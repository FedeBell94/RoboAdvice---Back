package it.uiip.digitalgarage.roboadvice.businesslogic.controller;

import it.uiip.digitalgarage.roboadvice.businesslogic.exception.BadRequestException;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.DemoDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.PortfolioDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.AbstractResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.SuccessResponse;
import it.uiip.digitalgarage.roboadvice.service.demoTask.DemoTask;
import it.uiip.digitalgarage.roboadvice.utils.CustomDate;
import it.uiip.digitalgarage.roboadvice.utils.RoboAdviceConstant;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Class used to create all the API rest used to create the demo for the user.
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "securedApi")
public class DemoRESTController {

    private static final Log LOGGER = LogFactory.getLog(DemoRESTController.class);

    private final DemoTask demoTask;

    @Autowired
    public DemoRESTController(DemoTask demoTask) {
        this.demoTask = demoTask;
    }

    /**
     * This rest API calls the designed service in order to calculate the demo for the user. In case the value to of the
     * request is missing, only one day of demo is computed.
     *
     * @param demoDTO The demoDTO containing the data needed to create the demo.
     *
     * @return The list of portfolioDTO containing the demo required.
     *
     * @throws BadRequestException Exception thrown in case of missing input parameters.
     */
    @RequestMapping(value = "/demo", method = RequestMethod.POST)
    public AbstractResponse requestDemo(@RequestBody DemoDTO demoDTO) throws BadRequestException {
        if (demoDTO == null || demoDTO.getFrom() == null || demoDTO.getStrategy() == null ||
                demoDTO.getWorth() == null) {
            throw new BadRequestException("Bad request - parameters needed: from, strategy, worth.");
        }
        if (RoboAdviceConstant.STARTING_DATA.compareTo(demoDTO.getFrom()) > 0) {
            throw new BadRequestException(
                    "Bad request - invalid data: you could not go before " + RoboAdviceConstant.STARTING_DATA);
        }

        if (demoDTO.getTo() == null) {
            demoDTO.setTo(new CustomDate(demoDTO.getFrom()).getDayFromSql(1));
        }

        CustomDate fromDate = new CustomDate(new CustomDate(demoDTO.getFrom()).getYesterdayLocalDate());
        CustomDate toDate = new CustomDate(demoDTO.getTo());

        List<PortfolioDTO> returnList =
                demoTask.computeDemo(fromDate, toDate, demoDTO.getStrategy(), demoDTO.getWorth());

        LOGGER.debug("Back-test called from date " + demoDTO.getFrom());
        return new SuccessResponse<>(returnList);
    }


}
