/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomType;
import enumType.RoomRateStatusEnum;
import enumType.RoomRateTypeEnum;
import exceptions.RoomRateInUseException;
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
import javax.persistence.TypedQuery;

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
    public void updateRoomRate(Long rateID, String name, RoomRateTypeEnum rateType, RoomRateStatusEnum rateStatus, BigDecimal ratePerNight, Date startDate, Date endDate) throws RoomRateNotFoundException {        RoomRate roomRate = em.find(RoomRate.class, rateID);
        if (roomRate == null) {
            throw new RoomRateNotFoundException("Room Rate ID " + rateID + " not found.");
        }
        
        roomRate.setName(name);
        roomRate.setRateType(rateType);
        roomRate.setStatus(rateStatus);
        roomRate.setRatePerNight(ratePerNight);
        roomRate.setStartDate(startDate);
        roomRate.setEndDate(endDate);
        em.merge(roomRate);
    }
    
    @Override
    public boolean deleteRoomRate(Long rateID) throws RoomRateNotFoundException, RoomRateInUseException {
        RoomRate roomRate = em.find(RoomRate.class, rateID);

        if (roomRate == null) {
            throw new RoomRateNotFoundException("Room Rate ID " + rateID + " not found.");
        }

        // Check if the room rate is associated with any reservations
        Query query = em.createQuery("SELECT COUNT(r) FROM Reservation r WHERE r.roomRate = :roomRate");
        query.setParameter("roomRate", roomRate);
        Long count = (Long) query.getSingleResult();

        if (count > 0) {
            roomRate.setStatus(RoomRateStatusEnum.DISABLED); 
            em.merge(roomRate); // Update the status in the database
            System.out.println("Room Rate ID " + rateID + " marked as disabled.");
            return false; // Exit the method        
        }

        // Proceed with deletion if not associated with any reservations
        em.remove(roomRate);
        return true;
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
        BigDecimal totalAmount = BigDecimal.ZERO;

        // Loop through each day of the reservation
        Date currentDate = checkInDate;
        while (currentDate.before(checkOutDate)) {
            BigDecimal rateForDay = getBestRateForDate(roomType, currentDate);
            totalAmount = totalAmount.add(rateForDay);
            currentDate = addDays(currentDate, 1); // Move to the next day
        }

        return totalAmount;
    }

    private BigDecimal getBestRateForDate(RoomType roomType, Date date) {
        // Check for Promotion Rate first (highest priority)
        RoomRate promotionRate = findRoomRate(roomType, RoomRateTypeEnum.PROMOTION, date);
        if (promotionRate != null) {
            return promotionRate.getRatePerNight();
        }

        // Check for Peak Rate next
        RoomRate peakRate = findRoomRate(roomType, RoomRateTypeEnum.PEAK, date);
        if (peakRate != null) {
            return peakRate.getRatePerNight();
        }

        // Use Normal Rate as the default rate (lowest priority)
        RoomRate normalRate = findRoomRate(roomType, RoomRateTypeEnum.NORMAL, date);
        return (normalRate != null) ? normalRate.getRatePerNight() : BigDecimal.ZERO;
    }

    private RoomRate findRoomRate(RoomType roomType, RoomRateTypeEnum rateType, Date date) {
        TypedQuery<RoomRate> query = em.createQuery(
                "SELECT rr FROM RoomRate rr WHERE rr.roomType = :roomType AND rr.rateType = :rateType " +
                "AND (rr.startDate IS NULL OR rr.startDate <= :date) " +
                "AND (rr.endDate IS NULL OR rr.endDate >= :date)", RoomRate.class);
        query.setParameter("roomType", roomType);
        query.setParameter("rateType", rateType);
        query.setParameter("date", date);

        List<RoomRate> rates = query.getResultList();
        return rates.isEmpty() ? null : rates.get(0);
    }

    private Date addDays(Date date, int days) {
        long milliseconds = date.getTime();
        long oneDayMilliseconds = days * 24L * 60L * 60L * 1000L;
        return new Date(milliseconds + oneDayMilliseconds);
    }

    
    // for walk-in search room use case
    @Override
    public RoomRate getPublishedRateForRoomType(RoomType roomType, Date checkInDate, Date checkOutDate) {
        Query query = em.createQuery("SELECT rr FROM RoomRate rr WHERE rr.roomType = :roomType AND rr.rateType = :rateType " +
                "AND (rr.startDate IS NULL OR rr.startDate <= :checkInDate) AND " +
                "(rr.endDate IS NULL OR rr.endDate >= :checkOutDate) ORDER BY rr.ratePerNight ASC");
        query.setParameter("roomType", roomType);
        query.setParameter("rateType", RoomRateTypeEnum.PUBLISHED);
        query.setParameter("checkInDate", checkInDate);
        query.setParameter("checkOutDate", checkOutDate);

        List<RoomRate> rates = query.getResultList();
        return rates.isEmpty() ? null : rates.get(0);
    }
    
}
