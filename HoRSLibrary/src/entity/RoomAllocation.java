/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.*;

/**
 *
 * @author hohin
 */
@Entity
public class RoomAllocation implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long allocationId;

    @Column(nullable = false)
    private Date allocationDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "roomId", nullable = true)
    private Room room;

    @OneToOne(optional = false)
    @JoinColumn(name = "reservationId", unique = true)
    private Reservation reservation;

    private String allocationExceptionReport;

    public RoomAllocation() {
    }

    public RoomAllocation(Date allocationDate, Room room, Reservation reservation, String allocationExceptionReport) {
        this.allocationDate = allocationDate;
        this.room = room;
        this.reservation = reservation;
        this.allocationExceptionReport = allocationExceptionReport;
    }

    public Long getAllocationId() {
        return allocationId;
    }

    public void setAllocationId(Long allocationId) {
        this.allocationId = allocationId;
    }

    public Date getAllocationDate() {
        return allocationDate;
    }

    public void setAllocationDate(Date allocationDate) {
        this.allocationDate = allocationDate;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public String getAllocationExceptionReport() {
        return allocationExceptionReport;
    }

    public void setAllocationExceptionReport(String allocationExceptionReport) {
        this.allocationExceptionReport = allocationExceptionReport;
    }
}
