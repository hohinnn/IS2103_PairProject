/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author hohin
 */
@Entity
public class Reservation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationID;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date checkInDate;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date checkOutDate;

    @Column(nullable = false)
    private String status; // Confirmed, Checked-In, Completed

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Guest guest;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private RoomType roomType; // Requested room type for the reservation

    @ManyToOne
    @JoinColumn(nullable = true)
    private Room room; // The specific room assigned to this reservation

    public Reservation(){}
    
    public Reservation(Date checkInDate, Date checkOutDate, String status, BigDecimal totalAmount, Guest guest, RoomType roomType, Room room) {
        this();
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.guest = guest;
        this.roomType = roomType;
        this.room = room;
    }

    public Long getReservationID() {
        return reservationID;
    }

    public void setReservationID(Long reservationID) {
        this.reservationID = reservationID;
    }

    public Date getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Date getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(Date checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    
}
