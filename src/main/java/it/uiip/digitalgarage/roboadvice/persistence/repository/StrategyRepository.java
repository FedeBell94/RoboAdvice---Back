package it.uiip.digitalgarage.roboadvice.persistence.repository;

import it.uiip.digitalgarage.roboadvice.persistence.model.Strategy;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StrategyRepository extends JpaRepository<Strategy, Integer> {

    List<Strategy> findByUserAndActiveTrue(User user);

}
