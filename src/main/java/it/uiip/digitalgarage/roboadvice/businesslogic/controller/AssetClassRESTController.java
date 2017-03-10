package it.uiip.digitalgarage.roboadvice.businesslogic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Authenticator;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.AssetClassDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.AbstractResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.SuccessResponse;
import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.AssetClass;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetClassRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by Simone on 10/03/2017.
 */

@RestController
@RequestMapping(value = "securedApi")
public class AssetClassRESTController {

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssetClassRepository assetClassRepository;

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/assetClassHistory", method = RequestMethod.GET)
    public
    @ResponseBody
    AbstractResponse requestAssetClassData(@RequestParam int assetClassId/*, Authentication authentication*/) {

        AssetClass assetClass = assetClassRepository.findOne(assetClassId);

        List<Asset> assets = assetRepository.findByAssetClass(assetClass);


        HashMap<LocalDate, BigDecimal> hm = new HashMap<>();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -360);
        Date startDate = new java.sql.Date(cal.getTimeInMillis());


        for (int i = 0; i < assets.size(); i++) {

            List<Data> assetData = dataRepository.findByAssetAndDateAfter(assets.get(i), startDate);


            for (int j = 0; j < assetData.size(); j++) {

                if (hm.get(assetData.get(j).getDate()) == null) {
                    hm.put(assetData.get(j).getDate().toLocalDate(), assetData.get(j).getValue()
                            .multiply(assetData.get(j).getAsset().getFixedPercentage()).divide(new BigDecimal("100")));
                } else {
                    hm.put(assetData.get(j).getDate().toLocalDate(), hm.get(assetData.get(j).getDate())
                            .add(assetData.get(j).getValue()
                                    .multiply(assetData.get(j).getAsset().getFixedPercentage()).divide(new BigDecimal("100"))));
                }

            }

        }

        Iterator it = hm.entrySet().iterator();
        ArrayList<AssetClassDTO> result = new ArrayList();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            result.add(AssetClassDTO.builder()
                    .date(pair.getKey().toString())
                    .value((BigDecimal) pair.getValue()).build());
            it.remove();
        }
        result.sort(new Comparator<AssetClassDTO>() {
            @Override
            public int compare(AssetClassDTO o1, AssetClassDTO o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });
        return new SuccessResponse<>(result);
    }

    @PostConstruct
    public void func(){
        requestAssetClassData(1);
    }

}
