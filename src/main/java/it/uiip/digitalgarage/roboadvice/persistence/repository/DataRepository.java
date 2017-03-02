package it.uiip.digitalgarage.roboadvice.persistence.repository;

import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by Simone on 02/03/2017.
 */
public interface DataRepository  extends PagingAndSortingRepository<Data, Integer> {


}