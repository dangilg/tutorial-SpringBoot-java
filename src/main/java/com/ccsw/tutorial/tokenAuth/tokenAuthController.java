package com.ccsw.tutorial.tokenAuth;

import com.ccsw.tutorial.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author dgilgut
 */
@Tag(name = "TokenAuth", description = "API of TokenAuth")
@RequestMapping(value = "/authToken")
@RestController
@CrossOrigin(origins = "*")

public class tokenAuthController {
    @Autowired
    JwtService tokenService;

    @Operation(summary = "check if a token is valid", description = "method that check if a token gived in params is valid")
    @RequestMapping(path = "/validateToken", method = RequestMethod.GET)
    public ResponseEntity<Void> validateToken(@RequestHeader("Authorization") String token) {
        token = token.substring(7);
        if (tokenService.isTokenValid(token)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
