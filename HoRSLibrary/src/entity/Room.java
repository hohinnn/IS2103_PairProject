/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import enumType.RoomAvailabilityEnum;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
/**
 *
 * @author hohin
 */
@Entity
public class Room implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomID;

    @Column(nullable = false)
    private int floorNumber;

    @Column(nullable = false)
    private int roomSequence;
    
    @Column(nullable = false)
    private String formattedRoomSequence;

    @Enumerated(EnumType.STRING)
    private RoomAvailabilityEnum status; // e.g., "Available", "Occupied", "Maintenance"

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private RoomType roomType;

    public Room(){}
    
    public Room(int floorNumber, int roomSequence, RoomAvailabilityEnum status, RoomType roomType) {
        this();
        this.floorNumber = floorNumber;
        this.roomSequence = roomSequence;
        this.formattedRoomSequence = String.format("%02d%02d", floorNumber, roomSequence);
        this.status = status;
        this.roomType = roomType;
    }

    public Long getRoomID() {
        return roomID;
    }

    public void setRoomID(Long roomID) {
        this.roomID = roomID;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public int getRoomSequence() {
        return roomSequence;
    }

    public void setRoomSequence(int roomSequence) {
        this.roomSequence = roomSequence;
    }

    public RoomAvailabilityEnum getStatus() {
        return status;
    }

    public void setStatus(RoomAvailabilityEnum status) {
        this.status = status;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }
    
    // Method to return formatted room number
    public String getFormattedRoomSequence() {
        return formattedRoomSequence;
    }
    
    public void setFormattedRoomSequence(String formattedRoomSequence) {
        this.formattedRoomSequence = formattedRoomSequence;
    }

}
