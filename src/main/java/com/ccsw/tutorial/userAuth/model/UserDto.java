package com.ccsw.tutorial.userAuth.model;

/**
 * @author dgilgut
 */
public class UserDto {
    private long id;
    private String username;
    private String password;

    /**
     *
     * @return id
     */
    public long getId() {
        return id;
    }

    /**
     *
     * @return username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     *
     * @return password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     *
     * @param id  log id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @param username String username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @param password String password
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
