package models;

/**
 * Represents a recruiting company in the placement system.
 * Inherits from User and manages company profile and recruitment details.
 */
public class Company extends User {
    private static final long serialVersionUID = 1L;

    private String industry;
    private String location;
    private String description;
    private String hrContact;

    public Company() {
        super();
        this.role = "COMPANY";
    }

    public Company(String id, String name, String email, String password,
                   String industry, String location, String description, String hrContact) {
        super(id, name, email, password, "COMPANY");
        this.industry = industry;
        this.location = location;
        this.description = description;
        this.hrContact = hrContact;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHrContact() {
        return hrContact;
    }

    public void setHrContact(String hrContact) {
        this.hrContact = hrContact;
    }

    @Override
    public String getDisplaySummary() {
        return String.format("Company: %s | ID: %s | Industry: %s | Location: %s | HR Contact: %s",
                name, id, industry, location, hrContact);
    }
}
