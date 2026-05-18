package com.ccsw.tutorial.common.deleteCheck;

public class DeleteCheckObject {
    private Long id;
    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DeleteCheckObject(Long id, String name){
        setId(id);
        setName(name);
    }
}
