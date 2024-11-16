/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.*;

/**
 *
 * @author hohin
 */

@Entity
public class RoomType implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomTypeID;

    //@Column(nullable = false, unique = true)
    //private String name;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column
    private int priorityRanking;

    @Column
    private String description;

    @Column
    private double size;

    @Column
    private String bedType;

    @Column
    private int capacity;

    @Column
    private String amenities;

    @OneToMany(mappedBy = "roomType")
    private List<Room> rooms;

    @OneToMany(mappedBy = "roomType")
    private List<RoomRate> roomRates;

    public RoomType(){}
    
    public RoomType(String name, int priorityRanking, String description, double size, String bedType, int capacity, String amenities) {
        this();
        this.name = name;
        this.priorityRanking = priorityRanking;
        this.description = description;
        this.size = size;
        this.bedType = bedType;
        this.capacity = capacity;
        this.amenities = amenities;
        this.rooms = new ArrayList<>();
        this.roomRates = new ArrayList<>();
    }

    public Long getRoomTypeID() {
        return roomTypeID;
    }

    public void setRoomTypeID(Long roomTypeID) {
        this.roomTypeID = roomTypeID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriorityRanking() {
        return priorityRanking;
    }

    public void setPriorityRanking(int priorityRanking) {
        this.priorityRanking = priorityRanking;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public String getBedType() {
        return bedType;
    }

    public void setBedType(String bedType) {
        this.bedType = bedType;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public List<RoomRate> getRoomRates() {
        return roomRates;
    }

    public void setRoomRates(List<RoomRate> roomRates) {
        this.roomRates = roomRates;
    }

}
