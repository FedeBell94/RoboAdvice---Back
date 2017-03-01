package it.uiip.digitalgarage.roboadvice.persistence.repository;

import org.springframework.data.repository.CrudRepository;

import it.uiip.digitalgarage.roboadvice.persistence.model.AssetClass;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AssetClassRepository extends PagingAndSortingRepository<AssetClass, Long> {

}
