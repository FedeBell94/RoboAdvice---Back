package it.uiip.digitalgarage.roboadvice.persistence.repository;

import it.uiip.digitalgarage.roboadvice.persistence.model.Portfolio;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.sql.Date;
import java.util.List;

/**
 * Created by feder on 01/03/2017.
 */
public interface PortfolioRepository extends PagingAndSortingRepository<Portfolio, Integer> {

    List<Portfolio> findByUserAndDate(User user, Date date);

}
