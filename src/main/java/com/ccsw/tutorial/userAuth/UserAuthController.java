package com.ccsw.tutorial.userAuth;

import com.ccsw.tutorial.exceptions.NotFoundUserException;
import com.ccsw.tutorial.exceptions.NotValidUsernameException;
import com.ccsw.tutorial.exceptions.WrongPasswordException;
import com.ccsw.tutorial.security.JwtService;
import com.ccsw.tutorial.userAuth.model.AuthResponseDto;
import com.ccsw.tutorial.userAuth.model.User;
import com.ccsw.tutorial.userAuth.model.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Autowired
    JwtService jwtService;

    /**
     * Metodo para registrar un nuevo usuario
     * @throws NotValidUsernameException when the User username is already in the DB
     */
    @Operation(summary = "register a new User", description = "Method that create a new user")
    @RequestMapping(path = "/signIn", method = RequestMethod.POST)
    @ApiResponses({ @ApiResponse(responseCode = "409", description = "User already exists") })
    public AuthResponseDto register(@RequestBody UserDto dto) throws NotValidUsernameException {
        User user = userAuthService.save(dto);
        System.out.println("antes del return");
        String token = jwtService.generateToken(dto.getUsername());
        AuthResponseDto response = new AuthResponseDto();
        response.setToken(token);
        return response;
    }

    @Operation(summary = "iniciar sesion", description = "Method que inicia sesión de un user")
    @RequestMapping(path = "/logIn", method = RequestMethod.POST)
    @ApiResponses({ @ApiResponse(responseCode = "401", description = "Wrong User or Password") })
    public AuthResponseDto logIn(@RequestBody UserDto dto) throws NotFoundUserException, WrongPasswordException {

        User user = userAuthService.getUser(dto.getUsername());

        userAuthService.checkPassword(user.getPassword(), dto.getPassword());

        AuthResponseDto response = new AuthResponseDto();
        response.setToken(jwtService.generateToken(user.getUsername()));
        return response;
    }
}
