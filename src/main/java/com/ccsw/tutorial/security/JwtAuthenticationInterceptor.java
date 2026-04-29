package com.ccsw.tutorial.security;

import com.ccsw.tutorial.exceptions.NotValidTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import com.ccsw.tutorial.security.config.PublicRoutesConfig;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class JwtAuthenticationInterceptor extends OncePerRequestFilter {

    @Autowired
    JwtService jwtService;

    @Autowired
    PublicRoutesConfig publicRoutesConfig;

    @Autowired
    HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        String path = request.getRequestURI();
        return path.startsWith("/h2-console");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try{
            this.miFiltro(request, response, filterChain);
        } catch(Exception exception){
            handlerExceptionResolver.resolveException(request,response,null,exception);
        }
    }

    private void miFiltro(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, NotValidTokenException {

        String uri = request.getRequestURI();
        System.out.println(uri);
        String method = request.getMethod();

        System.out.println("estoy en Inerceptor");



        if(publicRoutesConfig.isPublic(method,uri)){
            System.out.println("soy una ruta publica");
            filterChain.doFilter(request,response);
            return;
        }

        System.out.println("NO soy una ruta publica");

        String bearerToken = request.getHeader("Authorization");

        if(bearerToken==null){
            throw new NotValidTokenException();
        }
            String cleanToken = bearerToken.substring(7);
            if(jwtService.isTokenValid(cleanToken)){
                System.out.println("tengo un token valido");

                filterChain.doFilter(request,response);
            }


    }

}
