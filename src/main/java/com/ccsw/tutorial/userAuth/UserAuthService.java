package com.ccsw.tutorial.userAuth;

import com.ccsw.tutorial.exceptions.NotFoundUserException;
import com.ccsw.tutorial.exceptions.NotValidUsernameException;
import com.ccsw.tutorial.exceptions.WrongPasswordException;
import com.ccsw.tutorial.userAuth.model.User;
import com.ccsw.tutorial.userAuth.model.UserDto;

public interface UserAuthService {

    User save(UserDto dto) throws NotValidUsernameException;

    User getUser(String username) throws NotFoundUserException;

    void checkPassword(String passwordDB, String password) throws WrongPasswordException;
}
