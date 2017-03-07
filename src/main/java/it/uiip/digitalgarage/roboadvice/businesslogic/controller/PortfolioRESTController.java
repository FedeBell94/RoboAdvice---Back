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

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -91);
            Date ddate = new java.sql.Date(cal.getTimeInMillis());
            List<Date> dl = portfolioRepository.findDate(user,ddate);
            List<BigDecimal> sl = portfolioRepository.findSum(user,ddate);

            ArrayList<GraphsDTO> gdto = new ArrayList<>();
            gdto.add(GraphsDTO.builder().title("Bonds").valueField("column1").build());
            gdto.add(GraphsDTO.builder().title("Forex").valueField("column2").build());
            gdto.add(GraphsDTO.builder().title("Stocks").valueField("column3").build());
            gdto.add(GraphsDTO.builder().title("Commodities").valueField("column4").build());
            ArrayList<DataDTO> ddto = new ArrayList<>();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            for(int i = 0 ; i< dl.size(); i = i+4){

                ddto.add(DataDTO.builder()
                        .date(df.format(dl.get(i)))
                        .column1(sl.get(i))
                        .column2(sl.get(i+1))
                        .column3(sl.get(i+2))
                        .column4(sl.get(i+3))
                        .build()
                );

            }

            return new SuccessResponse<>(PortfolioDTO.builder().graphs(gdto).data(ddto).build());
        });

    }


}
