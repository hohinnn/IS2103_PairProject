/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB31/SingletonEjbClass.java to edit this template
 */
package ejb.session.singleton;

import entity.Reservation;
import entity.Room;
import entity.RoomAllocation;
import entity.RoomType;
import enumType.RoomAvailabilityEnum;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
                "SELECT r FROM Reservation r WHERE r.checkInDate = :date ORDER BY r.roomType.priority ASC", Reservation.class)
                .setParameter("date", date)
                .getResultList();

        for (Reservation reservation : reservations) {
            allocateRoomForReservation(reservation);
        }
    }

    @Override
    public void allocateRoomForReservation(Reservation reservation) {
        Room allocatedRoom = findAvailableRoomOrUpgrade(reservation.getRoomType());

        if (allocatedRoom != null) {
            reservation.setRoom(allocatedRoom);
            allocatedRoom.setStatus(RoomAvailabilityEnum.OCCUPIED); // Mark room as unavailable
            em.merge(reservation);
            em.merge(allocatedRoom);

            if (!allocatedRoom.getRoomType().equals(reservation.getRoomType())) {
                logRoomAllocationException(reservation, "Type 1: Room upgrade allocated to next higher tier.");
            }
        } else {
            logRoomAllocationException(reservation, "Type 2: No rooms available, manual handling required.");
        }
    }

    // Finds an available room in the requested room type or next higher tier
    private Room findAvailableRoomOrUpgrade(RoomType requestedRoomType) {
        RoomType currentRoomType = requestedRoomType;

        while (currentRoomType != null) {
            Room availableRoom = findAvailableRoom(currentRoomType);
            if (availableRoom != null) {
                return availableRoom;
            }
            currentRoomType = getNextHigherRoomType(currentRoomType); // Upgrade to higher room type if needed
        }

        return null; // No rooms available even after attempting upgrades
    }

    // Helper method to find an available room of a specific type
    private Room findAvailableRoom(RoomType roomType) {
        List<Room> rooms = em.createQuery(
                "SELECT r FROM Room r WHERE r.roomType = :roomType AND r.status = :status", Room.class)
                .setParameter("roomType", roomType)
                .setParameter("status", RoomAvailabilityEnum.AVAILABLE)
                .getResultList();
        return rooms.isEmpty() ? null : rooms.get(0);
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
}