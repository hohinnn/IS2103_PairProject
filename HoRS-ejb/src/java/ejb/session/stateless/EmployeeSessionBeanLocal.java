/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.Employee;
import exceptions.EmployeeNotFoundException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author hohin
 */
@Local
public interface EmployeeSessionBeanLocal {

    public Employee createEmployee(Employee employee);

    public Employee loginEmployee(String username, String password) throws EmployeeNotFoundException;

    public List<Employee> viewAllEmployees();
    
}
