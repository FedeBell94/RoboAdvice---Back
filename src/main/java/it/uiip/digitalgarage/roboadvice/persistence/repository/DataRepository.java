package it.uiip.digitalgarage.roboadvice.persistence.repository;

import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.List;

public interface DataRepository extends JpaRepository<Data, Integer> {

    //Data findByAssetAndDate(Asset asset, Date date);

    Data findTop1ByDateBeforeAndAssetOrderByDateDesc(Date date, Asset asset);

    List<Data> findFirst360ByAssetOrderByDateAsc(Asset asset);
}
