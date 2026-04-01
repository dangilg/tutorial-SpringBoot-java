package com.ccsw.tutorial.userAuth;

import com.ccsw.tutorial.exceptions.NotValidUsernameException;
import com.ccsw.tutorial.userAuth.model.User;
import com.ccsw.tutorial.userAuth.model.UserDto;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserAuthServiceImpl implements UserAuthService {

    public User save(UserDto dto) throws NotValidUsernameException {

        return null;
    }
}
