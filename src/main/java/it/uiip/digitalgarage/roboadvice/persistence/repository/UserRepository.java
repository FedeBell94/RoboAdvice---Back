package it.uiip.digitalgarage.roboadvice.persistence.repository;

import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by feder on 28/02/2017.
 */
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {

    User findByEmail(String email);
}
