package com.ccsw.tutorial.userAuth;

import com.ccsw.tutorial.exceptions.NotFoundUserException;
import com.ccsw.tutorial.exceptions.NotValidUsernameException;
import com.ccsw.tutorial.exceptions.WrongPasswordException;
import com.ccsw.tutorial.userAuth.model.User;
import com.ccsw.tutorial.userAuth.model.UserDto;

public interface UserAuthService {

    /**
     * Guarda un nuevo usuario en la BD
     * @param dto {@link UserDto} con los datos del nuevo usuario
     * @return {@link User} nuevo usuario
     * @throws NotValidUsernameException Si el usuario ya existe en la BD
     */
    User save(UserDto dto) throws NotValidUsernameException;

    /**
     * Devuelve el usuario a partir de su nombre
     * @param username Nombre de usuario
     * @return {@link User}
     * @throws NotFoundUserException Si el {@link User} no existe en la BD
     */
    User getUser(String username) throws NotFoundUserException;

    /**
     * Comprueba si la contraseña dada es la misma que la guardada en la BD
     * @param passwordDB Contraseña guardada en la BD
     * @param password Contraseña a comparar
     * @throws WrongPasswordException Si las contraseñas no coinciden
     */
    void checkPassword(String passwordDB, String password) throws WrongPasswordException;
}
