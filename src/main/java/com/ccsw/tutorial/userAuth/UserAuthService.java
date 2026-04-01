package com.ccsw.tutorial.userAuth;

import com.ccsw.tutorial.exceptions.NotValidUsernameException;
import com.ccsw.tutorial.userAuth.model.User;
import com.ccsw.tutorial.userAuth.model.UserDto;

public interface UserAuthService {

    User save(UserDto dto) throws NotValidUsernameException;
}
