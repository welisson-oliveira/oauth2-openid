package br.com.googleclient.domain.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends CrudRepository<User, Long> {

    @Query("select u from User u join u.authOpenid a where a.id.value = :authn_id")
    Optional<User> findAuthenticatedUser(@Param("authn_id") String authn_id);


    User save(User user);
}
