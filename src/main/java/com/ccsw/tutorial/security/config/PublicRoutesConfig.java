package com.ccsw.tutorial.security.config;

import com.ccsw.tutorial.security.model.PublicRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.PathContainer;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.List;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

/**
 * Clase que implementa la lista de Rutas Públicas
 */
@Component
public class PublicRoutesConfig {

@Autowired
PathPatternParser patternParser;
    private final List<PublicRoute> publicRoutes = List.of(
            //Author
            new PublicRoute(GET, "/author/{id}/can-delete"),
            new PublicRoute(GET, "/author"),
            new PublicRoute(POST, "/author")
            ,
            //Category
            new PublicRoute(GET,"/category"),
            new PublicRoute(GET,"/category/{id}/can-delete")
            ,
            //Games
            new PublicRoute(GET,"/game")
            ,
            //UserAuth
            new PublicRoute(POST,"/auth/signIn"),
            new PublicRoute(POST,"/auth/logIn")
            ,
            //Client
            new PublicRoute(GET,"/client"),
            new PublicRoute(GET,"/client/can-delete"),

            //Loan
            new PublicRoute(POST, "/loan")

    );

    public List<PublicRoute> getPublicRoutes() {
        return publicRoutes;
    }

    public boolean isPublic(String methodStr, String path){
        HttpMethod method = HttpMethod.valueOf(methodStr);

        return publicRoutes.stream().anyMatch(
                route -> route.getMethod().equals(method)
                && pathMatches(route.getPath(),path)
        );
    }

    private boolean pathMatches(String pattern, String path){
        PathPattern pathPattern = patternParser.parse(pattern);
        return pathPattern.matches(PathContainer.parsePath(path));
    }
}

