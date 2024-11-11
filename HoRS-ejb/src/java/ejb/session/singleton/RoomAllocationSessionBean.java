/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB31/SingletonEjbClass.java to edit this template
 */
package ejb.session.singleton;

import entity.Reservation;
import entity.Room;
import entity.RoomAllocation;
import entity.RoomRate;
import entity.RoomType;
import enumType.RoomAvailabilityEnum;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    @Override
    @Schedule(hour = "2", minute = "0", persistent = false)
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
        Room allocatedRoom = findAvailableRoomOrUpgrade(
                reservation.getRoomType(), reservation.getCheckInDate(), reservation.getCheckOutDate());

        if (allocatedRoom != null) {
            // Assign the room to the reservation
            reservation.setRoom(allocatedRoom);
            allocatedRoom.setStatus(RoomAvailabilityEnum.OCCUPIED);
            em.merge(allocatedRoom);

            // Calculate the total amount based on the prevailing rates for each night of stay
            BigDecimal totalAmount = calculateTotalAmount(reservation);
            reservation.setTotalAmount(totalAmount);
            em.merge(reservation);

            if (!allocatedRoom.getRoomType().equals(reservation.getRoomType())) {
                logRoomAllocationException(reservation, "Room upgraded to next higher tier.");
            }
        } else {
            logRoomAllocationException(reservation, "No rooms available, manual handling required.");
        }
    }

    // Finds an available room in the requested room type or next higher tier
    private Room findAvailableRoomOrUpgrade(RoomType requestedRoomType, Date checkInDate, Date checkOutDate) {
        RoomType currentRoomType = requestedRoomType;

        while (currentRoomType != null) {
            Room availableRoom = findAvailableRoom(currentRoomType, checkInDate, checkOutDate);
            if (availableRoom != null) {
                return availableRoom;
            }
            currentRoomType = getNextHigherRoomType(currentRoomType); // Try upgrading to a higher room type if needed
        }

        return null; // No rooms available even after attempting upgrades
    }

    // Helper method to find an available room of a specific type
    private Room findAvailableRoom(RoomType roomType, Date checkInDate, Date checkOutDate) {
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
                return room; // Room is available for the entire duration
            }
        }
        return null; // No available rooms found for the specified period
    }

    // Retrieve the next higher room type if available
    private RoomType getNextHigherRoomType(RoomType currentRoomType) {
        // Assume room types have a priority attribute, with higher numbers being higher tiers
        List<RoomType> higherRoomTypes = em.createQuery(
                "SELECT rt FROM RoomType rt WHERE rt.priority > :priority ORDER BY rt.priority ASC", RoomType.class)
                .setParameter("priority", currentRoomType.getPriorityRanking())
                .getResultList();
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
        RoomRate promotionRate = findRoomRate(roomType, "PROMOTION", date);
        if (promotionRate != null) {
            return promotionRate.getRatePerNight();
        }

        // Check for Peak Rate if no Promotion Rate is found
        RoomRate peakRate = findRoomRate(roomType, "PEAK", date);
        if (peakRate != null) {
            return peakRate.getRatePerNight();
        }

        // Use Normal Rate as the default rate
        RoomRate normalRate = findRoomRate(roomType, "NORMAL", date);
        return (normalRate != null) ? normalRate.getRatePerNight() : BigDecimal.ZERO;
    }

    private RoomRate findRoomRate(RoomType roomType, String rateType, Date date) {
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
