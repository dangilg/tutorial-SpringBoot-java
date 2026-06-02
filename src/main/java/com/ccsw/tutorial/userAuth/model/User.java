package com.ccsw.tutorial.userAuth.model;

import jakarta.persistence.*;

/**
 * @author dgilgut
 *
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    /**
     * @return id
     */
    public long getId() {
        return id;
    }

    /**
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param id new value of {@link #getId}.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @param username new value of {@link #getUsername}.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @param password new value of {@link #getPassword}.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
