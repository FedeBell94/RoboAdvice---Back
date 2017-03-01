package it.uiip.digitalgarage.roboadvice.persistence.repository;

import it.uiip.digitalgarage.roboadvice.persistence.model.Portfolio;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by feder on 01/03/2017.
 */
public interface PortfolioRepository extends PagingAndSortingRepository<Portfolio, Long> {
}
