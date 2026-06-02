package com.ccsw.tutorial.client.model;



import jakarta.persistence.*;
@Entity
@Table(name="client")
public class Client {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name="name", nullable = false, unique = true)
    private String name;

    /**
     *
     * @return id
     */
    public long getId() {
        return id;
    }

    /**
     *
     * @param id nuevo valor de {@link #getId()}
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name nuevo valor de {@link #getName()}
     */
    public void setName(String name) {
        this.name = name;
    }
}
