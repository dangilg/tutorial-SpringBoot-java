package com.ccsw.tutorial.userAuth;

import com.ccsw.tutorial.exceptions.NotFoundUserException;
import com.ccsw.tutorial.exceptions.NotValidUsernameException;
import com.ccsw.tutorial.exceptions.WrongPasswordException;
import com.ccsw.tutorial.userAuth.model.User;
import com.ccsw.tutorial.userAuth.model.UserDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class UserAuthServiceImpl implements UserAuthService {
    @Autowired
    UserAuthRepository userAuthRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public User save(UserDto dto) throws NotValidUsernameException {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        try {
            this.userAuthRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new NotValidUsernameException();
        }

        return user;
    }

    public User getUser(String username) throws NotFoundUserException {

        Optional<User> userBD = userAuthRepository.findByUsername(username);
        if (userBD.isEmpty()) {
            throw new NotFoundUserException();
        }

        return userBD.get();
    }

    public void checkPassword(String passwordDB, String password) throws WrongPasswordException {
        if (!passwordEncoder.matches(password, passwordDB)) {
            throw new WrongPasswordException();
        }
    }
}
