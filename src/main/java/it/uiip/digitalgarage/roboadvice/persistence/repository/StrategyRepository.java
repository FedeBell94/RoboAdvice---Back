package it.uiip.digitalgarage.roboadvice.persistence.repository;

import it.uiip.digitalgarage.roboadvice.persistence.model.Strategy;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.sql.Date;
import java.util.List;

public interface StrategyRepository extends PagingAndSortingRepository<Strategy, Long> {

    List<Strategy> findByUserAndActiveTrue(User user);

    List<Strategy> findByUserAndStartingDate(User user, Date date);

}
