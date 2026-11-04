package services;

import exceptions.InvalidInputException;
import exceptions.InvalidLoginException;
import exceptions.UserAlreadyExistsException;
import interfaces.Authentication;
import models.Admin;
import models.Company;
import models.Student;
import models.User;
import utils.FileManager;
import utils.IDGenerator;

import java.util.*;

/**
 * Service managing user authentication, registration, and credentials.
 * Implements the Authentication interface.
 */
public class AuthenticationService implements Authentication {

    private List<Student> students;
    private List<Company> companies;
    private List<Admin> admins;

    // Lookup caches for fast O(1) checks
    private Map<String, User> emailMap;
    private Map<String, User> idMap;

    public AuthenticationService() {
        this.students = new ArrayList<>();
        this.companies = new ArrayList<>();
        this.admins = new ArrayList<>();
        this.emailMap = new HashMap<>();
        this.idMap = new HashMap<>();
        reload();
    }

    public void reload() {
        this.students = FileManager.loadStudents();
        this.companies = FileManager.loadCompanies();
        this.admins = FileManager.loadAdmins();

        this.emailMap.clear();
        this.idMap.clear();

        for (Admin a : admins) {
            emailMap.put(a.getEmail().toLowerCase(), a);
            idMap.put(a.getId(), a);
        }
        for (Company c : companies) {
            emailMap.put(c.getEmail().toLowerCase(), c);
            idMap.put(c.getId(), c);
        }
        for (Student s : students) {
            emailMap.put(s.getEmail().toLowerCase(), s);
            idMap.put(s.getId(), s);
        }
    }

    public void saveAll() {
        FileManager.saveStudents(this.students);
        FileManager.saveCompanies(this.companies);
        FileManager.saveAdmins(this.admins);
    }

    @Override
    public User login(String email, String password) throws InvalidLoginException {
        if (email == null || password == null) {
            throw new InvalidLoginException("Email and password cannot be empty.");
        }
        String cleanEmail = email.trim().toLowerCase();
        User user = emailMap.get(cleanEmail);

        if (user == null || !user.getPassword().equals(password)) {
            throw new InvalidLoginException("Invalid email or password combination. Please try again.");
        }

        return user;
    }

    @Override
    public Student registerStudent(Student student) throws UserAlreadyExistsException, InvalidInputException {
        if (student == null) {
            throw new InvalidInputException("Student data cannot be null.");
        }
        String cleanEmail = student.getEmail().trim().toLowerCase();
        if (emailMap.containsKey(cleanEmail)) {
            throw new UserAlreadyExistsException("An account with email " + cleanEmail + " already exists.");
        }

        String stuId = IDGenerator.generateStudentId(this.students.size());
        student.setId(stuId);
        student.setEmail(cleanEmail);

        this.students.add(student);
        this.emailMap.put(cleanEmail, student);
        this.idMap.put(stuId, student);
        FileManager.saveStudents(this.students);

        return student;
    }

    @Override
    public Company registerCompany(Company company) throws UserAlreadyExistsException, InvalidInputException {
        if (company == null) {
            throw new InvalidInputException("Company data cannot be null.");
        }
        String cleanEmail = company.getEmail().trim().toLowerCase();
        if (emailMap.containsKey(cleanEmail)) {
            throw new UserAlreadyExistsException("An account with email " + cleanEmail + " already exists.");
        }

        String comId = IDGenerator.generateCompanyId(this.companies.size());
        company.setId(comId);
        company.setEmail(cleanEmail);

        this.companies.add(company);
        this.emailMap.put(cleanEmail, company);
        this.idMap.put(comId, company);
        FileManager.saveCompanies(this.companies);

        return company;
    }

    @Override
    public boolean changePassword(String userId, String oldPassword, String newPassword) throws InvalidInputException {
        User user = idMap.get(userId);
        if (user == null) {
            throw new InvalidInputException("User account not found.");
        }
        if (!user.getPassword().equals(oldPassword)) {
            throw new InvalidInputException("Current password does not match.");
        }
        if (newPassword == null || newPassword.trim().length() < 6) {
            throw new InvalidInputException("New password must be at least 6 characters long.");
        }

        user.setPassword(newPassword.trim());
        if (user instanceof Admin) {
            ((Admin) user).setDefaultPassword(false);
        }
        saveAll();
        return true;
    }

    public List<Student> getStudents() {
        return students;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public List<Admin> getAdmins() {
        return admins;
    }

    public User getUserById(String id) {
        return idMap.get(id);
    }

    public boolean removeUser(String userId) {
        User u = idMap.remove(userId);
        if (u != null) {
            emailMap.remove(u.getEmail().toLowerCase());
            if (u instanceof Student) {
                students.remove(u);
            } else if (u instanceof Company) {
                companies.remove(u);
            }
            saveAll();
            return true;
        }
        return false;
    }
}
