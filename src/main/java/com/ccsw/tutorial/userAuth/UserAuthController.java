package com.ccsw.tutorial.userAuth;

import com.ccsw.tutorial.exceptions.NotValidUsernameException;
import com.ccsw.tutorial.userAuth.model.User;
import com.ccsw.tutorial.userAuth.model.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author dgilgut
 */
@Tag(name = "UserAuth", description = "API of userAuth")
@RequestMapping(value = "/auth")
@RestController
@CrossOrigin(origins = "*")
public class UserAuthController {
    @Autowired
    UserAuthService userAuthService;

    @Autowired
    ModelMapper mapper;

    /**
     * Metodo para registrar un nuevo usuario
     * @throws NotValidUsernameException when the User username is already in the DB
     */
    @Operation(summary = "register a new User", description = "Method that create a new user")
    @RequestMapping(path = "/signIn", method = RequestMethod.POST)
    public void register(@RequestBody UserDto dto) throws NotValidUsernameException {
        try {
            User user = userAuthService.save(dto);
        } catch (NotValidUsernameException exception) {

        }
    }
}
