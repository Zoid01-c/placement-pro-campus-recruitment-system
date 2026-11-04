package interfaces;

import exceptions.InvalidInputException;
import exceptions.InvalidLoginException;
import exceptions.UserAlreadyExistsException;
import models.Company;
import models.Student;
import models.User;

/**
 * Interface defining user authentication and registration workflows.
 */
public interface Authentication {
    User login(String email, String password) throws InvalidLoginException;
    Student registerStudent(Student student) throws UserAlreadyExistsException, InvalidInputException;
    Company registerCompany(Company company) throws UserAlreadyExistsException, InvalidInputException;
    boolean changePassword(String userId, String oldPassword, String newPassword) throws InvalidInputException;
}
