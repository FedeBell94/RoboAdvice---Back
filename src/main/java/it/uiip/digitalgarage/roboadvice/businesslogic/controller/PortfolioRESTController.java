package it.uiip.digitalgarage.roboadvice.businesslogic.controller;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.DataDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.GraphsDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.PortfolioDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.AbstractResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.SuccessResponse;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import it.uiip.digitalgarage.roboadvice.persistence.repository.PortfolioRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Simone on 06/03/2017.
 */
@RestController
@RequestMapping(value = "securedApi")
public class PortfolioRESTController {


    @Autowired
    private UserRepository userRepository;


    @Autowired
    private PortfolioRepository portfolioRepository;

    @CrossOrigin(origins = "*")
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
        //System.out.println(PortfolioDTO.builder().graphs(gdto).data(ddto).build());
        return new SuccessResponse<>(PortfolioDTO.builder().graphs(gdto).data(ddto).build());
    }

}
