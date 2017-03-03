package it.uiip.digitalgarage.roboadvice.persistence.repository;

import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.AssetClass;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.ArrayList;

/**
 * Created by Simone on 02/03/2017.
 */
public interface AssetRepository extends PagingAndSortingRepository<Asset, Integer> {

    ArrayList<Asset> findByAssetClass(AssetClass assetClass);
}
