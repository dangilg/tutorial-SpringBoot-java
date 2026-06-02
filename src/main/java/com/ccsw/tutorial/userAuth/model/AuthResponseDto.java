package com.ccsw.tutorial.userAuth.model;

/**
 * @author dgilguti
 * Clase que especifica la respuesta de autenticación de un usuario
 */
public class AuthResponseDto {

    private String token = "";

    /**
     *
     * @return token JWT
     */
    public String getToken() {
        return token;
    }

    /**
     *
     * @param token nuevo valor de {@link #getToken}
     */
    public void setToken(String token) {
        this.token = token;
    }
}
