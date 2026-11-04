package models;

import java.io.Serializable;

/**
 * Abstract parent class representing a system user.
 * Demonstrates inheritance, encapsulation, and polymorphism.
 */
public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String id;
    protected String name;
    protected String email;
    protected String password;
    protected String role; // "STUDENT", "COMPANY", "ADMIN"

    public User() {
    }

    public User(String id, String name, String email, String password, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Polymorphic method to get a formatted display summary of the user.
     */
    public abstract String getDisplaySummary();

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - %s", role, name, id, email);
    }
}
