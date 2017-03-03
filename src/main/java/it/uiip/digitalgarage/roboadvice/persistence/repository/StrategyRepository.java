package it.uiip.digitalgarage.roboadvice.persistence.repository;

import it.uiip.digitalgarage.roboadvice.persistence.model.Strategy;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.ArrayList;

/**
 * Created by Simone on 03/03/2017.
 */
public interface StrategyRepository extends PagingAndSortingRepository<Strategy, Integer> {


    ArrayList<Strategy> findByUserAndActive(User user,Boolean bool);
}
