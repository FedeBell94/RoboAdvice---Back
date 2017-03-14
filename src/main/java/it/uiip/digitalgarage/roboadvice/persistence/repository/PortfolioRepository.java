package it.uiip.digitalgarage.roboadvice.persistence.repository;

import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import it.uiip.digitalgarage.roboadvice.persistence.model.Portfolio;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.util.List;
import java.util.Map;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    List<Portfolio> findByUserAndDate(User user, Date date);

    @Query("SELECT SUM(p.value) AS value, p.date AS date " +
                   "FROM Portfolio p " +
                   "WHERE p.user = ?1 " +
                   "AND p.date BETWEEN ?2 AND ?3 " +
                   "GROUP BY p.date, p.assetClass")
    List<Map<Object, Object>> findPortfolioHistory(User user, Date from, Date to);

    @Query("SELECT SUM(p.value) AS value, p.date AS date " +
                   "FROM Portfolio p " +
                   "WHERE p.user = ?1 " +
                   "AND p.date BETWEEN ?2 AND ?3 " +
                   "GROUP BY date")
    List<Map<Object, Object>> findWorthPerDay(User user, Date from, Date to);

    @Query("SELECT ac.name AS assetClass, SUM(p.value) AS value " +
                   "FROM Portfolio p, AssetClass ac " +
                   "WHERE p.assetClass = ac " +
                   "AND p.user = ?1 " +
                   "AND p.date = ?2 " +
                   "GROUP BY p.assetClass ")
    List<Map<Object, Object>> findWorthDayPerAssetClass(User user, Date date);
}
