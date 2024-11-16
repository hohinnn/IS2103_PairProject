/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB31/SingletonEjbClass.java to edit this template
 */
package ejb.session.singleton;

import ejb.session.stateless.RoomRateSessionBeanLocal;
import entity.Reservation;
import entity.Room;
import entity.RoomAllocation;
import entity.RoomRate;
import entity.RoomType;
import enumType.ReservationStatusEnum;
import enumType.RoomAvailabilityEnum;
import enumType.RoomRateTypeEnum;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author hohin
 */
@Singleton
public class RoomAllocationSessionBean implements RoomAllocationSessionBeanRemote, RoomAllocationSessionBeanLocal {

    @PersistenceContext(unitName = "HoRS-ejbPU")
    private EntityManager em;

    private static final Logger LOGGER = Logger.getLogger(RoomAllocationSessionBean.class.getName());
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBeanLocal;
    
    @Override
    @Schedule(hour = "2", minute = "0", persistent = true)
    public void allocateRoomsDaily() {
        Date today = new Date();
        allocateRoomsForDate(today);
    }

    // Allocates rooms for reservations on the specified date
    @Override
    public void allocateRoomsForDate(Date date) {
        List<Reservation> reservations = em.createQuery(
                "SELECT r FROM Reservation r WHERE r.checkInDate = :date ORDER BY r.roomType.priorityRanking ASC", Reservation.class)
                .setParameter("date", date)
                .getResultList();

        for (Reservation reservation : reservations) {
            allocateRoomForReservation(reservation);
        }
    }

    @Override
    public void allocateRoomForReservation(Reservation reservation) {
        
        if (reservation == null || reservation.getRoomType() == null || reservation.getCheckInDate() == null || reservation.getCheckOutDate() == null) {
                throw new IllegalArgumentException("Invalid reservation data. Please check the reservation fields.");
            }
        // Check if the reservation already has an allocated room
        if (reservation.getRoom() != null && reservation.getStatus() == ReservationStatusEnum.ALLOCATED) {
            return; // Early return to avoid reallocating an already allocated room
        }

        Room allocatedRoom = findAvailableRoomOrUpgrade(
                reservation.getRoomType(), 
                reservation.getCheckInDate(), 
                reservation.getCheckOutDate());
        
        if (allocatedRoom == null) {
            // Log a Type 2 exception if no rooms are available
            logRoomAllocationException(reservation, "Type 2: No rooms available for the requested or higher room types.");
            return;
        }

        // Check if the allocated room's type is different from the requested room type
        if (!allocatedRoom.getRoomType().equals(reservation.getRoomType())) {
            logRoomAllocationException(reservation, "Type 1: Room upgraded to next higher tier.");
            return;
        
        }

        // Check if the allocated room is available and not already occupied
        if (allocatedRoom != null && allocatedRoom.getStatus() == RoomAvailabilityEnum.AVAILABLE) {
            // Assign the room to the reservation
            reservation.setRoom(allocatedRoom);
            allocatedRoom.setStatus(RoomAvailabilityEnum.OCCUPIED); // Mark room as occupied
            reservation.setStatus(ReservationStatusEnum.ALLOCATED);
            em.merge(allocatedRoom); // Update room status in the database

            // Calculate the total amount based on the prevailing rates for each night of stay
            BigDecimal totalAmount = calculateTotalAmount(reservation);
            reservation.setTotalAmount(totalAmount);

            // Make sure to correctly set the room type and rate
            reservation.setRoomType(allocatedRoom.getRoomType());
            reservation.setRoomRate(roomRateSessionBeanLocal.getPublishedRateForRoomType(
                allocatedRoom.getRoomType(), reservation.getCheckInDate(), reservation.getCheckOutDate()));

            
            em.merge(reservation); // Update reservation details in the database

        }
    }

    // Finds an available room in the requested room type or next higher tier
    private Room findAvailableRoomOrUpgrade(RoomType requestedRoomType, Date checkInDate, Date checkOutDate) {
        // First, try to find an available room in the requested room type
        Room availableRoom = findAvailableRoom(requestedRoomType, checkInDate, checkOutDate);
        if (availableRoom != null) {
            return availableRoom;
        }

        // If no room is available in the requested type, upgrade to a higher room type
        RoomType currentRoomType = requestedRoomType;
        while (currentRoomType != null) {
            availableRoom = findAvailableRoom(currentRoomType, checkInDate, checkOutDate);
            if (availableRoom != null) {
                return availableRoom;
            }
            currentRoomType = getNextHigherRoomType(currentRoomType); // Try upgrading to a higher room type if needed
        }

        return null; // No rooms available even after attempting upgrades
    }


    private Room findAvailableRoom(RoomType roomType, Date checkInDate, Date checkOutDate) {
        List<Room> rooms = em.createQuery(
                "SELECT r FROM Room r WHERE r.roomType = :roomType AND r.status != :occupiedStatus AND r.status != :disabledStatus", Room.class)
                .setParameter("roomType", roomType)
                .setParameter("occupiedStatus", RoomAvailabilityEnum.OCCUPIED)  // Exclude rooms with 'OCCUPIED' status
                .setParameter("disabledStatus", RoomAvailabilityEnum.DISABLED)  // Exclude rooms with 'DISABLED' status
                .getResultList();

        for (Room room : rooms) {
            // Check for overlapping reservations and exclude rooms already allocated (ALLOCATED status)
            List<Reservation> overlappingReservations = em.createQuery(
                    "SELECT res FROM Reservation res WHERE res.room = :room AND "
                    + "(res.checkInDate < :checkOutDate AND res.checkOutDate > :checkInDate) "
                    + "AND res.status != :allocatedStatus", Reservation.class)
                    .setParameter("room", room)
                    .setParameter("checkInDate", checkInDate)
                    .setParameter("checkOutDate", checkOutDate)
                    .setParameter("allocatedStatus", ReservationStatusEnum.ALLOCATED)  // Exclude 'ALLOCATED' reservations
                    .getResultList();

            if (overlappingReservations.isEmpty() && room.getStatus() == RoomAvailabilityEnum.AVAILABLE) {
                return room; // Room is available for the entire duration
            }
        }
        return null; // No available rooms found for the specified period
    }



    // Retrieve the next higher room type if available
    private RoomType getNextHigherRoomType(RoomType currentRoomType) {
        List<RoomType> higherRoomTypes = em.createQuery(
                "SELECT rt FROM RoomType rt WHERE rt.priorityRanking > :priorityRanking ORDER BY rt.priorityRanking ASC", RoomType.class)
                .setParameter("priorityRanking", currentRoomType.getPriorityRanking())
                .getResultList();
        if (higherRoomTypes.isEmpty()) {
            LOGGER.log(Level.INFO, "No higher room types found for {0}", currentRoomType.getName());
        }
        return higherRoomTypes.isEmpty() ? null : higherRoomTypes.get(0);
    }

    // Logs an exception to the allocation exception report
    private void logRoomAllocationException(Reservation reservation, String message) {
        RoomAllocation exception = new RoomAllocation(
                new Date(), null, reservation, message);
        em.persist(exception);

        LOGGER.log(Level.WARNING, "Room Allocation Exception: {0} for Reservation ID: {1}", new Object[]{message, reservation.getReservationID()});
    }

    @Override
    public List<RoomAllocation> retrieveRoomAllocationExceptions() {
        TypedQuery<RoomAllocation> query = em.createQuery(
                "SELECT ra FROM RoomAllocation ra WHERE ra.allocationExceptionReport IS NOT NULL",
                RoomAllocation.class);
        return query.getResultList();
    }

    @Override
    public boolean checkRoomTypeAvailability(RoomType roomType, Date checkInDate, Date checkOutDate) {
        List<Room> rooms = em.createQuery(
                "SELECT r FROM Room r WHERE r.roomType = :roomType AND r.status = :status", Room.class)
                .setParameter("roomType", roomType)
                .setParameter("status", RoomAvailabilityEnum.AVAILABLE)
                .getResultList();

        for (Room room : rooms) {
            List<Reservation> overlappingReservations = em.createQuery(
                    "SELECT res FROM Reservation res WHERE res.room = :room AND "
                    + "(res.checkInDate < :checkOutDate AND res.checkOutDate > :checkInDate)", Reservation.class)
                    .setParameter("room", room)
                    .setParameter("checkInDate", checkInDate)
                    .setParameter("checkOutDate", checkOutDate)
                    .getResultList();

            if (overlappingReservations.isEmpty()) {
                return true; // Room type is available
            }
        }
        return false;
    }

    private long daysBetween(Date start, Date end) {
        return ChronoUnit.DAYS.between(start.toInstant(), end.toInstant());
    }

    private BigDecimal calculateTotalAmount(Reservation reservation) {
        RoomType roomType = reservation.getRoomType();
        Date checkInDate = reservation.getCheckInDate();
        Date checkOutDate = reservation.getCheckOutDate();
        BigDecimal totalAmount = BigDecimal.ZERO;

        // Loop through each day of the reservation
        Date currentDate = checkInDate;
        while (currentDate.before(checkOutDate)) {
            BigDecimal ratePerNight = getRateForDate(roomType, currentDate);
            totalAmount = totalAmount.add(ratePerNight);
            currentDate = addDays(currentDate, 1); // Move to the next day
        }

        return totalAmount;
    }

    private BigDecimal getRateForDate(RoomType roomType, Date date) {
        // Check for Promotion Rate first
        RoomRate promotionRate = findRoomRate(roomType, RoomRateTypeEnum.PROMOTION, date);
        if (promotionRate != null) {
            return promotionRate.getRatePerNight();
        }

        // Check for Peak Rate if no Promotion Rate is found
        RoomRate peakRate = findRoomRate(roomType, RoomRateTypeEnum.PEAK, date);
        if (peakRate != null) {
            return peakRate.getRatePerNight();
        }

        // Use Normal Rate as the default rate
        RoomRate normalRate = findRoomRate(roomType, RoomRateTypeEnum.NORMAL, date);
        return (normalRate != null) ? normalRate.getRatePerNight() : BigDecimal.ZERO;
    }

    private RoomRate findRoomRate(RoomType roomType, RoomRateTypeEnum rateType, Date date) {
        TypedQuery<RoomRate> query = em.createQuery(
                "SELECT rr FROM RoomRate rr WHERE rr.roomType = :roomType AND rr.rateType = :rateType "
                + "AND (rr.startDate IS NULL OR rr.startDate <= :date) "
                + "AND (rr.endDate IS NULL OR rr.endDate >= :date)", RoomRate.class);
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

}
