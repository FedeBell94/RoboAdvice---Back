package it.uiip.digitalgarage.roboadvice.persistence.repository;

import it.uiip.digitalgarage.roboadvice.persistence.model.Portfolio;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.util.List;

public interface PortfolioRepository extends JpaRepository<Portfolio, Integer> {

    List<Portfolio> findByUserAndDate(User user, Date date);

    List<Portfolio> findFirst13ByUserOrderByDateDesc(User user);

    @Query("SELECT SUM(p.value) AS sum,p.date as date FROM Portfolio p WHERE p.user = ?1 AND p.date > ?2 group by date, p.assetClass")
    List<Object[]> findData(User user, Date date);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(p.value) AS sum,p.date as date FROM Portfolio p WHERE p.user = ?1 AND p.date > ?2 group by date")
    List<Object[]> findWorth(User user, Date date);

}
