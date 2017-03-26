package it.uiip.digitalgarage.roboadvice.businesslogic.controller;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.DemoDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.PortfolioDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.AbstractResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.ErrorResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.SuccessResponse;
import it.uiip.digitalgarage.roboadvice.core.demoTask.DemoTask;
import it.uiip.digitalgarage.roboadvice.utils.CustomDate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @RequestMapping(value = "/demo", method = RequestMethod.POST)
    public AbstractResponse requestDemo(@RequestBody DemoDTO demoDTO) {

        if (demoDTO.getFrom() == null || demoDTO.getStrategy() == null || demoDTO.getWorth() == null) {
            return new ErrorResponse("Missing parameter/s.");
        }
        if (demoDTO.getTo() == null) {
            demoDTO.setTo(new CustomDate(demoDTO.getFrom()).getDayFromSql(1));
        }

        // TODO implement in custom date getYesterdayCustomDate
        CustomDate fromDate = new CustomDate(new CustomDate(demoDTO.getFrom()).getYesterdayLocalDate());
        CustomDate toDate = new CustomDate(demoDTO.getTo());

        List<PortfolioDTO> returnList =
                demoTask.computeDemo(fromDate, toDate, demoDTO.getStrategy(), demoDTO.getWorth());

        LOGGER.debug("Back-test called from date " + demoDTO.getFrom());
        return new SuccessResponse<>(returnList);
    }


}
