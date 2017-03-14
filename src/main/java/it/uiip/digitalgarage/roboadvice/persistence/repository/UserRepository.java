package it.uiip.digitalgarage.roboadvice.persistence.repository;

import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    User findByUsername(String username);

}
