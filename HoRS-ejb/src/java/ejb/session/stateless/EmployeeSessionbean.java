/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Employee;
import exceptions.EmployeeNotFoundException;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author hohin
 */
@Stateless
public class EmployeeSessionbean implements EmployeeSessionbeanRemote, EmployeeSessionbeanLocal {

    @PersistenceContext(unitName = "HoRS-ejbPU")
    private EntityManager em;

    public Employee createEmployee(Employee employee) {
        em.persist(employee);
        em.flush();
        return employee;
    }
    
    public Employee loginEmployee(String username, String password) throws EmployeeNotFoundException {
        try {
            return em.createQuery("SELECT e FROM Employee e WHERE e.username = :username AND e.password = :password", Employee.class)
                     .setParameter("username", username)
                     .setParameter("password", password)
                     .getSingleResult();
        } catch (NoResultException e) {
            throw new EmployeeNotFoundException("Employee with username: " + username + " not found.");
        }
        
    }
    
    public List<Employee> viewAllEmployees() {
        return em.createQuery("SELECT e FROM Employee e", Employee.class).getResultList();
    }
    
}
