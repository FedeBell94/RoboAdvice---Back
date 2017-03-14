package it.uiip.digitalgarage.roboadvice.persistence.repository;

import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.AssetClass;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface AssetRepository extends PagingAndSortingRepository<Asset, Long> {

    List<Asset> findByAssetClass(AssetClass assetClass);

}
