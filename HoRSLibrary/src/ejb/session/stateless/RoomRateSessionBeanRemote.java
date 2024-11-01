/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.RoomRate;
import enumType.RoomRateTypeEnum;
import exceptions.RoomRateNotFoundException;
import java.math.BigDecimal;
import java.util.Date;
import javax.ejb.Remote;

/**
 *
 * @author hohin
 */
@Remote
public interface RoomRateSessionBeanRemote {
    
    public RoomRate createRoomRate(RoomRate roomRate);

    public void updateRoomRate(Long rateID, String name, RoomRateTypeEnum rateType, BigDecimal ratePerNight, Date startDate, Date endDate) throws RoomRateNotFoundException;

    public void deleteRoomRate(Long rateID) throws RoomRateNotFoundException;

    public RoomRate getRoomRate(Long rateID) throws RoomRateNotFoundException;
    
}
