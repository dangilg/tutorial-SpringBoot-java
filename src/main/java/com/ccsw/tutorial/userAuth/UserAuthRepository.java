package com.ccsw.tutorial.userAuth;

import com.ccsw.tutorial.userAuth.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserAuthRepository extends CrudRepository<User, Long> {

    /**
     * Busca un {@link User} según su username
     * @param username nombre del usuario
     * @return {@link Optional} de {@link User}
     */
    Optional<User> findByUsername(String username);
}
