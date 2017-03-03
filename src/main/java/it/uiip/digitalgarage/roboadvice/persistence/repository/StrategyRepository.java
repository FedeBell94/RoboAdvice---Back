package it.uiip.digitalgarage.roboadvice.persistence.repository;

import it.uiip.digitalgarage.roboadvice.persistence.model.Strategy;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface StrategyRepository  extends PagingAndSortingRepository<Strategy, Integer> {

    List<Strategy> findByUserAndActiveTrue(User user);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Strategy s SET s.active = false WHERE s.id = ?1")
    void disactiveStrategy(int strategyId);
}
