package it.uiip.digitalgarage.roboadvice.persistence.repository;

import it.uiip.digitalgarage.roboadvice.persistence.model.Strategy;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface StrategyRepository  extends PagingAndSortingRepository<Strategy, Integer> {
}
