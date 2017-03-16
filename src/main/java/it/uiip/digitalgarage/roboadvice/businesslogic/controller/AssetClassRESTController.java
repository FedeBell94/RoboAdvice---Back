package it.uiip.digitalgarage.roboadvice.businesslogic.controller;

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
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by Simone on 10/03/2017.
 */

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "securedApi")
public class AssetClassRESTController {

    private final DataRepository dataRepository;
    private final AssetRepository assetRepository;
    private final AssetClassRepository assetClassRepository;

    @Autowired
    public AssetClassRESTController(final DataRepository dataRepository, final AssetRepository assetRepository,
                                    final AssetClassRepository assetClassRepository) {
        this.dataRepository = dataRepository;
        this.assetRepository = assetRepository;
        this.assetClassRepository = assetClassRepository;
    }

    @RequestMapping(value = "/assetClassesName", method = RequestMethod.GET)
    public
    @ResponseBody
    AbstractResponse requestAssetClassData() {
        Iterable<AssetClass> assetClasses = assetClassRepository.findAll();
        return new SuccessResponse<>(assetClasses);
    }

    @RequestMapping(value = "/assetClassHistory", method = RequestMethod.GET)
    public
    @ResponseBody
    AbstractResponse requestAssetClassData(@RequestParam Long assetClassId) {

        AssetClass assetClass = assetClassRepository.findOne(assetClassId);

        List<Asset> assets = assetRepository.findByAssetClass(assetClass);

        Map<LocalDate, BigDecimal> hm = new HashMap<>();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -360);
        Date startDate = new java.sql.Date(cal.getTimeInMillis());

        for (Asset curAsset : assets) {

            List<Data> assetData = dataRepository.findByDateAfterAndAsset(startDate, curAsset);

            for (Data curData : assetData) {

                if (hm.get(curData.getDate()) == null) {
                    hm.put(curData.getDate().toLocalDate(), curData.getValue()
                            .multiply(curData.getAsset().getFixedPercentage()).divide(new BigDecimal("100")));
                } else {
                    hm.put(curData.getDate().toLocalDate(), hm.get(curData.getDate())
                            .add(curData.getValue()
                                    .multiply(curData.getAsset().getFixedPercentage())
                                    .divide(new BigDecimal("100"))));
                }

            }

        }

        Iterator it = hm.entrySet().iterator();
        List<AssetClassDTO> result = new ArrayList();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
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

}
