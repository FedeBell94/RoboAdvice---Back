package it.uiip.digitalgarage.roboadvice.persistence.repository;

import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.sql.Date;
import java.util.List;

public interface DataRepository extends PagingAndSortingRepository<Data, Long> {

    // This query is only for testing (to compute the correct value on demo creation of the portfolio)
    Data findTop1ByDateBeforeAndAssetOrderByDateDesc(Date date, Asset asset);

    List<Data> findByDateAfterAndAsset(Date date, Asset asset);

    // used only because of the portfolio demo creation
    List<Data> findByDate(Date date);

    Data findTop1ByAssetOrderByDateDesc(Asset asset);

    Data findTop1ByOrderByDateDesc();
}
