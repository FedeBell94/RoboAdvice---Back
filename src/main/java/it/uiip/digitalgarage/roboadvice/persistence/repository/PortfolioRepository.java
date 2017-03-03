package it.uiip.digitalgarage.roboadvice.persistence.repository;

import it.uiip.digitalgarage.roboadvice.persistence.model.Portfolio;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.ArrayList;

/**
 * Created by feder on 01/03/2017.
 */
public interface PortfolioRepository extends PagingAndSortingRepository<Portfolio, Integer> {

    ArrayList<Portfolio> findByUserAndDate(User user, java.sql.Date date);

}
