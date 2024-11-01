/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import enumType.RoomRateTypeEnum;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author hohin
 */
public class RoomRate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rateID;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    private RoomRateTypeEnum rateType; // Published, Normal, Peak, Promotion

    @Column(nullable = false)
    private BigDecimal ratePerNight;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date endDate;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private RoomType roomType;

    public RoomRate(String name, RoomRateTypeEnum rateType, BigDecimal ratePerNight, Date startDate, Date endDate) {
        this.name = name;
        this.rateType = rateType;
        this.ratePerNight = ratePerNight;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getRateID() {
        return rateID;
    }

    public void setRateID(Long rateID) {
        this.rateID = rateID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RoomRateTypeEnum getRateType() {
        return rateType;
    }

    public void setRateType(RoomRateTypeEnum rateType) {
        this.rateType = rateType;
    }

    public BigDecimal getRatePerNight() {
        return ratePerNight;
    }

    public void setRatePerNight(BigDecimal ratePerNight) {
        this.ratePerNight = ratePerNight;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }
    
    
}
