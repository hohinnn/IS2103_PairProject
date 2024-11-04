/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import javax.persistence.*;
import java.util.List;

/**
 *
 * @author hohin
 */
@Entity
public class Partner implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partnerID;

    @Column(nullable = false, unique = true)
    private String partnerName;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "partner")
    private List<Reservation> partnerReservations;

    public Partner() {}
    
    public Partner(String partnerName, String username, String password) {
        this();
        this.partnerName = partnerName;
        this.username = username;
        this.password = password;
        this.partnerReservations = new ArrayList<Reservation>();
    }

    public Long getPartnerID() {
        return partnerID;
    }

    public void setPartnerID(Long partnerID) {
        this.partnerID = partnerID;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
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

    public List<Reservation> getPartnerReservations() {
        return partnerReservations;
    }

    public void setPartnerReservations(List<Reservation> partnerReservations) {
        this.partnerReservations = partnerReservations;
    }

}
