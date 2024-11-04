/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import enumType.EmployeeAccessRightEnum;
import java.io.Serializable;
import java.util.*;
import javax.persistence.*;

/**
 *
 * @author hohin
 */
@Entity
public class Employee implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeID;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private EmployeeAccessRightEnum accessRight;
    
    public Employee(){}

    public Employee(String username, String password, EmployeeAccessRightEnum accessRight) {
        this();
        this.username = username;
        this.password = password;
        this.accessRight = accessRight;
    }

    public Long getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(Long employeeID) {
        this.employeeID = employeeID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public EmployeeAccessRightEnum getAccessRight() {
        return accessRight;
    }

    public void setRole(EmployeeAccessRightEnum accessRight) {
        this.accessRight = accessRight;
    }
    
}
