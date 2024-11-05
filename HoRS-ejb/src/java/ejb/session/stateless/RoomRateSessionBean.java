/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomType;
import enumType.RoomRateTypeEnum;
import exceptions.RoomRateNotFoundException;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author hohin
 */
@Stateless
public class RoomRateSessionBean implements RoomRateSessionBeanRemote, RoomRateSessionBeanLocal {

    @PersistenceContext(unitName = "HoRS-ejbPU")
    private EntityManager em;

    @Override
    public RoomRate createRoomRate(RoomRate roomRate) {
        em.persist(roomRate);
        em.flush();
        return roomRate;
    }
    
    @Override
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
    
    @Override
    public void deleteRoomRate(Long rateID) throws RoomRateNotFoundException {
        try {
            RoomRate roomRate = em.find(RoomRate.class, rateID);
            em.remove(roomRate);
        } catch (NoResultException e) {
            throw new RoomRateNotFoundException("Room Rate: " + rateID + " not found.");
        }
    }
    
    @Override
    public RoomRate getRoomRate(Long rateID) throws RoomRateNotFoundException {
        try {
            RoomRate roomRate = em.find(RoomRate.class, rateID);
            return roomRate;
        } catch (NoResultException e) {
            throw new RoomRateNotFoundException("Room Rate: " + rateID + " not found.");
        }
    }
    
    @Override
    public List<RoomRate> getAllRoomRates() {
        return em.createQuery("SELECT rr FROM RoomRate rr", RoomRate.class).getResultList();
    }
    
    @Override
    public BigDecimal calculateRateForRoomType(RoomType roomType, Date checkInDate, Date checkOutDate) {
        Query query = em.createQuery("SELECT rr FROM RoomRate rr WHERE rr.roomType = :roomType AND " +
                "(rr.startDate IS NULL OR rr.startDate <= :checkInDate) AND " +
                "(rr.endDate IS NULL OR rr.endDate >= :checkOutDate) ORDER BY rr.ratePerNight DESC");
        query.setParameter("roomType", roomType);
        query.setParameter("checkInDate", checkInDate);
        query.setParameter("checkOutDate", checkOutDate);
        
        List<RoomRate> rates = query.getResultList();
        if (rates.isEmpty()) {
            return BigDecimal.ZERO;
        }

        RoomRate bestRate = rates.get(0);
        long days = ChronoUnit.DAYS.between(checkInDate.toInstant(), checkOutDate.toInstant());
        return bestRate.getRatePerNight().multiply(BigDecimal.valueOf(days));
    }
    
}
