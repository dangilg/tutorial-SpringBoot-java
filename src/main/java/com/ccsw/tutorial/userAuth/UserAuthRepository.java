package com.ccsw.tutorial.userAuth;

import com.ccsw.tutorial.userAuth.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserAuthRepository extends CrudRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
