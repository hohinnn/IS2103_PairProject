/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomType;
import enumType.RoomRateStatusEnum;
import enumType.RoomRateTypeEnum;
import exceptions.RoomRateInUseException;
import exceptions.RoomRateNotFoundException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author hohin
 */
@Remote
public interface RoomRateSessionBeanRemote {
    
    public RoomRate createRoomRate(RoomRate roomRate);

    public void updateRoomRate(Long rateID, String name, RoomRateTypeEnum rateType, RoomRateStatusEnum rateStatus, BigDecimal ratePerNight, Date startDate, Date endDate) throws RoomRateNotFoundException;
    
    public boolean deleteRoomRate(Long rateID) throws RoomRateNotFoundException, RoomRateInUseException;

    public RoomRate getRoomRate(Long rateID) throws RoomRateNotFoundException;
    
    public List<RoomRate> getAllRoomRates();
    
    public BigDecimal calculateRateForRoomType(RoomType roomType, Date checkInDate, Date checkOutDate);

    public RoomRate getPublishedRateForRoomType(RoomType roomType, Date checkInDate, Date checkOutDate);
    
    public BigDecimal calculatePublishedRateForRoomType(RoomType roomType, Date checkInDate, Date checkOutDate);

}
