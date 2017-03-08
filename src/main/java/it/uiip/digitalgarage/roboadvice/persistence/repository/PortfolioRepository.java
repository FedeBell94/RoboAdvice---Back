package it.uiip.digitalgarage.roboadvice.persistence.repository;

import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import it.uiip.digitalgarage.roboadvice.persistence.model.Portfolio;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

/**
 * Created by feder on 01/03/2017.
 */
public interface PortfolioRepository extends JpaRepository<Portfolio, Integer> {

    List<Portfolio> findByUserAndDate(User user, Date date);
//    List<Portfolio> findByUser(User user);
    List<Portfolio> findFirst13ByUserOrderByDateDesc(User user);
//    List<Portfolio> findByUserAndStartDate(User user, Date startDate);
//    List<Portfolio> findByUserAndStartDateGroupByDateAndAssetClassId(User user, Date date);


//    @org.springframework.data.jpa.repository.Query("SELECT SUM(p.value) AS sum FROM Portfolio p WHERE p.user = ?1 AND p.date > ?2 group by p.assetClass")
//    List<BigDecimal> findSum(User user, Date date);
//
//    @org.springframework.data.jpa.repository.Query("SELECT p.date as date FROM Portfolio p WHERE p.user = ?1 AND p.date > ?2 group by p.assetClass")
//    List<Date> findDate(User user, Date date);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(p.value) AS sum,p.date as date FROM Portfolio p WHERE p.user = ?1 AND p.date > ?2 group by p.assetClass, date")
    List<Object[]> findData(User user, Date date);

}
