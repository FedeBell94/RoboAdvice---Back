package it.uiip.digitalgarage.roboadvice.persistence.repository;

import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * Created by Simone on 02/03/2017.
 */
public interface DataRepository extends JpaRepository<Data, Integer> {

    Data findByAssetAndDate(Asset asset, java.sql.Date date);

    Data findFirst1ByAssetOrderByDateDesc(Asset asset);

    List<Data> findFirst360ByAssetOrderByDateAsc(Asset asset);
}
