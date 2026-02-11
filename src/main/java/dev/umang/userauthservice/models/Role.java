package dev.umang.userauthservice.models;
/*
Enums
constants with related values

Country

 */

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;

import java.util.List;

@Entity
public class Role extends BaseModel{
    private String name; //Mentor, Instructor, Admin, TA
    /*
    allowed permissions for this role
     */

//    @ManyToMany(mappedBy = "roles")
//    private List<User> users;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
