package it.uiip.digitalgarage.roboadvice.businesslogic.controller;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.*;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.AbstractResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.SuccessResponse;
import it.uiip.digitalgarage.roboadvice.persistence.model.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
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
public class PortfolioRESTController extends AbstractController {

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/portfolio", method = RequestMethod.POST)
    public
    @ResponseBody
    AbstractResponse requestMyData(HttpServletRequest request) {

        return super.executeSafeTask(request, (user) -> {

//            List<Date> dl = portfolioRepository.findDate(user,ddate);
//            List<BigDecimal> sl = portfolioRepository.findSum(user,ddate);

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
            System.out.println(PortfolioDTO.builder().graphs(gdto).data(ddto).build());

            return new SuccessResponse<>(PortfolioDTO.builder().graphs(gdto).data(ddto).build());
        });

    }


}
