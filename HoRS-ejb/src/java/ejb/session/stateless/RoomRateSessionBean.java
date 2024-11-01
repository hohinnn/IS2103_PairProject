/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.RoomRate;
import enumType.RoomRateTypeEnum;
import exceptions.RoomRateNotFoundException;
import java.math.BigDecimal;
import java.util.Date;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author hohin
 */
@Stateless
public class RoomRateSessionBean implements RoomRateSessionBeanRemote, RoomRateSessionBeanLocal {

    @PersistenceContext(unitName = "HoRS-ejbPU")
    private EntityManager em;

    public RoomRate createRoomRate(RoomRate roomRate) {
        em.persist(roomRate);
        em.flush();
        return roomRate;
    }
    
    public void updateRoomRate(Long rateID, String name, RoomRateTypeEnum rateType, BigDecimal ratePerNight, Date startDate, Date endDate) throws RoomRateNotFoundException {
        try {
            RoomRate roomRate = em.find(RoomRate.class, rateID);
            roomRate.setName(name);
            roomRate.setRateType(rateType);
            roomRate.setRatePerNight(ratePerNight);
            roomRate.setStartDate(startDate);
            roomRate.setEndDate(endDate);
            em.merge(roomRate);
        } catch (NoResultException e) {
            throw new RoomRateNotFoundException("Room Rate: " + rateID + " not found.");
        }
    }
    
    public void deleteRoomRate(Long rateID) throws RoomRateNotFoundException {
        try {
            RoomRate roomRate = em.find(RoomRate.class, rateID);
            em.remove(roomRate);
        } catch (NoResultException e) {
            throw new RoomRateNotFoundException("Room Rate: " + rateID + " not found.");
        }
    }
    
    public RoomRate getRoomRate(Long rateID) throws RoomRateNotFoundException {
        try {
            RoomRate roomRate = em.find(RoomRate.class, rateID);
            return roomRate;
        } catch (NoResultException e) {
            throw new RoomRateNotFoundException("Room Rate: " + rateID + " not found.");
        }
    }
}
