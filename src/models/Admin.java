package models;

/**
 * Represents the placement cell administrator.
 * Inherits from User and possesses full administrative control.
 */
public class Admin extends User {
    private static final long serialVersionUID = 1L;

    private String department;
    private String contactNumber;
    private boolean defaultPassword;

    public Admin() {
        super();
        this.role = "ADMIN";
    }

    public Admin(String id, String name, String email, String password,
                 String department, String contactNumber, boolean defaultPassword) {
        super(id, name, email, password, "ADMIN");
        this.department = department;
        this.contactNumber = contactNumber;
        this.defaultPassword = defaultPassword;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public boolean isDefaultPassword() {
        return defaultPassword;
    }

    public void setDefaultPassword(boolean defaultPassword) {
        this.defaultPassword = defaultPassword;
    }

    @Override
    public String getDisplaySummary() {
        return String.format("Admin: %s | ID: %s | Dept: %s | Email: %s",
                name, id, department, email);
    }
}
