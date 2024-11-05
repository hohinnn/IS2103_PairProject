/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomType;
import enumType.RoomRateTypeEnum;
import exceptions.RoomRateInUseException;
import exceptions.RoomRateNotFoundException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author hohin
 */
@Local
public interface RoomRateSessionBeanLocal {

    public RoomRate createRoomRate(RoomRate roomRate);

    public void updateRoomRate(Long rateID, String name, RoomRateTypeEnum rateType, BigDecimal ratePerNight, Date startDate, Date endDate) throws RoomRateNotFoundException;

    public void deleteRoomRate(Long rateID) throws RoomRateNotFoundException, RoomRateInUseException;

    public RoomRate getRoomRate(Long rateID) throws RoomRateNotFoundException;

    public List<RoomRate> getAllRoomRates();

    public BigDecimal calculateRateForRoomType(RoomType roomType, Date checkInDate, Date checkOutDate);
    
}
