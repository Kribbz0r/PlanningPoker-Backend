package org.planningPoker.model;

import org.bson.codecs.pojo.annotations.BsonId;
public class User {

    @BsonId 
    private String id;
    private String email;
    private String role;
    private int authorized;
    private String password;
    private String name;

    public User (){}

    public User(String id, String email, String role, int authorized, String password, String name) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.authorized = authorized;
        this.password = password;
        this.name = name;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public int getAuthorized() {
        return authorized;
    }
    public void setAuthorized(int authorized) {
        this.authorized = authorized;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

}
