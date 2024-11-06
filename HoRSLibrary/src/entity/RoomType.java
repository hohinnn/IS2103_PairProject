/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import enumType.RoomTypeEnum;
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
    
    @Enumerated(EnumType.STRING)
    private RoomTypeEnum name;
    
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
    
    public RoomType(RoomTypeEnum name, String description, double size, String bedType, int capacity, String amenities) {
        this();
        this.name = name;
        this.description = description;
        this.size = size;
        this.bedType = bedType;
        this.capacity = capacity;
        this.amenities = amenities;
        this.rooms = new ArrayList<Room>();
        this.roomRates = new ArrayList<RoomRate>();
        if (name == RoomTypeEnum.DELUXE) this.priorityRanking = 1;
        else if (name == RoomTypeEnum.PREMIER) this.priorityRanking = 2;
        else if (name == RoomTypeEnum.FAMILY) this.priorityRanking = 3;
        else if (name == RoomTypeEnum.JUNIOR_SUITE) this.priorityRanking = 4;
        else if (name == RoomTypeEnum.GRAND_SUITE) this.priorityRanking = 5;
    }

    public Long getRoomTypeID() {
        return roomTypeID;
    }

    public void setRoomTypeID(Long roomTypeID) {
        this.roomTypeID = roomTypeID;
    }

    public RoomTypeEnum getName() {
        return name;
    }

    public void setName(RoomTypeEnum name) {
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
