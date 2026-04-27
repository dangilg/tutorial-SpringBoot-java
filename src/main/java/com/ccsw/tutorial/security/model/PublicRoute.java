package com.ccsw.tutorial.security.model;

import org.springframework.http.HttpMethod;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Clase que implementa el objeto Public Route, HttpMethod y path de na ruta que no necesita authToken
 */
public class PublicRoute {
    private final HttpMethod method;
    private final String path;

    /**
     * Objeto PublicRute
     * @param method HttpMethod metodo Http de la petición publica
     * @param path String ruta de la peticion publica
     */
    public PublicRoute(HttpMethod method, String path) {
        this.method = method;
        this.path=path;

    }



    public HttpMethod getMethod() {
        return this.method;
    }

    public String getPath() {
        return this.path;
    }
}
