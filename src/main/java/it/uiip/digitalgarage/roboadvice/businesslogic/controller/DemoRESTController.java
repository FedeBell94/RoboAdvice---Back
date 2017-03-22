package it.uiip.digitalgarage.roboadvice.businesslogic.controller;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.StrategyDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.AbstractResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.SuccessResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by Simone on 22/03/2017.
 */

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "securedApi")
public class DemoRESTController {

    @Autowired
    public DemoRESTController() {

    }

    @RequestMapping(value = "/demo", method = RequestMethod.POST)
    public AbstractResponse requestDemo(@RequestBody List<StrategyDTO> strategyInput, @RequestBody LocalDate date) {



        return new SuccessResponse<>(true);
    }



}
