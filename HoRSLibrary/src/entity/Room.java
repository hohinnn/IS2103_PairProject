/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.util.List;
import javax.persistence.*;
/**
 *
 * @author hohin
 */
@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomID;

    @Column(nullable = false)
    private int floorNumber;

    @Column(nullable = false)
    private int roomSequence;

    @Column(nullable = false)
    private String status; // e.g., "Available", "Occupied", "Maintenance"

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private RoomType roomType;

    @OneToMany(mappedBy = "room")
    private List<ReservationRoom> reservationRooms;

    // Method to return formatted room number
    public String getFormattedRoomNumber() {
        return String.format("%02d%02d", floorNumber, roomSequence);
    }

    // Getters, Setters, Constructors
}
