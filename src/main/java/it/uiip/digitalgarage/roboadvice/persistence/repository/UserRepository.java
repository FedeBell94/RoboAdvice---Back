package it.uiip.digitalgarage.roboadvice.persistence.repository;

import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

/**
 * Created by feder on 28/02/2017.
 */
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {

    User findByEmail(String email);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE User u SET u.username = ?1 WHERE u.id = ?2")
    void setUserUsername(String username, Integer userId);
}
