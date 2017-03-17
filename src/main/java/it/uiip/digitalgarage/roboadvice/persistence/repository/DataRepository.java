package it.uiip.digitalgarage.roboadvice.persistence.repository;

import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.sql.Date;
import java.util.List;

public interface DataRepository extends PagingAndSortingRepository<Data, Long> {

    Data findTop1ByDateBeforeAndAssetOrderByDateDesc(Date date, Asset asset);

    Data findTop1ByDateLessThanEqualAndAssetOrderByDateDesc(Date date, Asset asset);

    List<Data> findByDateAfterAndAssetOrderByDateAsc(Date date, Asset asset);

    List<Data> findByDate(Date date);

    Data findTop1ByAssetOrderByDateDesc(Asset asset);

    Data findTop1ByOrderByDateDesc();
}
