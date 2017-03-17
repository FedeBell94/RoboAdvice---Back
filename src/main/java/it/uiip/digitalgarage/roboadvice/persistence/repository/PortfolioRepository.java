package it.uiip.digitalgarage.roboadvice.persistence.repository;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.PortfolioDTO;
import it.uiip.digitalgarage.roboadvice.persistence.model.Portfolio;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.sql.Date;
import java.util.List;

public interface PortfolioRepository extends PagingAndSortingRepository<Portfolio, Long> {

    List<Portfolio> findByUserAndDate(User user, Date date);

    @Query("SELECT SUM(p.value) AS value, p.date AS date, p.assetClass.id AS assetClassId " +
                   "FROM Portfolio p " +
                   "WHERE p.user = ?1 " +
                   "AND p.date > ?2 " +
                   "GROUP BY p.date, p.assetClass.id " +
                   "ORDER BY p.date ASC ")
    List<PortfolioDTO> findPortfolioHistory(User user, Date from);
}
