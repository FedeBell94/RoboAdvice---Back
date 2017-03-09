package it.uiip.digitalgarage.roboadvice.persistence.repository;

import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.AssetClass;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.ArrayList;

/**
 * Created by Simone on 02/03/2017.
 */
public interface AssetRepository extends JpaRepository<Asset, Integer> {

    Asset findById (int id);

    ArrayList<Asset> findByAssetClass(AssetClass assetClass);
}
