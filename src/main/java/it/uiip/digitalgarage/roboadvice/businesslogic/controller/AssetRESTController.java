package it.uiip.digitalgarage.roboadvice.businesslogic.controller;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.AbstractResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.SuccessResponse;
import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Simone on 10/03/2017.
 */

@RestController
@RequestMapping(value = "securedApi")
public class AssetRESTController {

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private AssetRepository assetRepository;

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/asset", method = RequestMethod.GET)
    public
    @ResponseBody
    AbstractResponse requestAssetData(@RequestParam int assetId,  Authentication authentication) {

        Asset asset = assetRepository.findOne(assetId);

        List<Data> assetData = dataRepository.findFirst360ByAssetOrderByDateAsc(asset);

        return new SuccessResponse<>(assetData);
    }

}
