/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import ejb.session.singleton.RoomAllocationSessionBeanLocal;
import entity.Guest;
import entity.Partner;
import entity.Reservation;
import entity.Room;
import entity.RoomType;
import enumType.ReservationStatusEnum;
import enumType.RoomAvailabilityEnum;
import exceptions.ReservationNotFoundException;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author hohin
 */
@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @PersistenceContext(unitName = "HoRS-ejbPU")
    private EntityManager em;

    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBeanLocal;
    @EJB
    private RoomAllocationSessionBeanLocal roomAllocationSessionBeanLocal;

    @Override
    public Long createReservation(Reservation reservation) {
        em.persist(reservation);
        em.flush();
        return reservation.getReservationID();
    }

    @Override
    public List<Reservation> viewAllReservations(long guestID) {
        Guest guest = em.createQuery("SELECT g FROM Guest g WHERE g.guestID = :guestID", Guest.class)
                .setParameter("guestID", guestID)
                .getSingleResult();
        return guest.getReservations();
    }
    
    public List<Reservation> viewAllReservationsPartner(long partnerID) {
        Partner partner = em.createQuery("SELECT p FROM Partner p WHERE p.partnerID = :partnerID", Partner.class)
                .setParameter("partnerID", partnerID)
                .getSingleResult();
        return partner.getPartnerReservations();
    }

    @Override
    public Reservation viewReservation(long reservationID) throws ReservationNotFoundException {
        try {
            return em.createQuery("SELECT r from Reservation r WHERE r.reservationID = :reservationID", Reservation.class)
                    .setParameter("reservationID", reservationID)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new ReservationNotFoundException("Reservation " + reservationID + " not found!");
        }
    }

    @Override
    public List<Reservation> walkInReserveRooms(String guestName, String phoneNumber, Date checkInDate, Date checkOutDate, List<Room> rooms) {
        Guest guest = new Guest();
        guest.setName(guestName);
        guest.setPhoneNumber(phoneNumber);
        em.persist(guest);

        Date currentDate = new Date();
        // Immediate check-in after 2am
        boolean immediateCheckIn = checkInDate.equals(currentDate) && currentDate.toInstant().atZone(ZoneId.systemDefault()).getHour() >= 2;

        List<Reservation> reservations = new ArrayList<>();
        for (Room room : rooms) {
            RoomType roomType = room.getRoomType();
            BigDecimal totalAmount = roomRateSessionBeanLocal.calculateRateForRoomType(roomType, checkInDate, checkOutDate);

            Reservation reservation = new Reservation(checkInDate, checkOutDate, ReservationStatusEnum.RESERVED, totalAmount, guest, roomType, room, null);
            em.persist(reservation);
            reservations.add(reservation);
        }

        em.flush();
        return reservations;
    }

    //Marks the reservation as "Checked-In" if it is currently in "Reserved" status. 
    @Override
    public void checkInGuest(long reservationId) throws ReservationNotFoundException {
        Reservation reservation = em.find(Reservation.class, reservationId);
        if (reservation == null) {
            throw new ReservationNotFoundException("Reservation ID " + reservationId + " not found.");
        }

        if (!reservation.getStatus().equals(ReservationStatusEnum.RESERVED)) {
            throw new IllegalStateException("Reservation is not in a state that allows check-in.");
        }

        reservation.setStatus(ReservationStatusEnum.CHECKED_IN);
        em.merge(reservation);
    }

    //Marks the reservation as "Completed" if it is currently in "Checked-In" status
    @Override
    public void checkOutGuest(long reservationId) throws ReservationNotFoundException {
        Reservation reservation = em.find(Reservation.class, reservationId);
        if (reservation == null) {
            throw new ReservationNotFoundException("Reservation ID " + reservationId + " not found.");
        }

        if (!reservation.getStatus().equals(ReservationStatusEnum.CHECKED_IN)) {
            throw new IllegalStateException("Guest is not currently checked-in.");
        }

        reservation.setStatus(ReservationStatusEnum.COMPLETED);
        em.merge(reservation);
    }

    @Override
    public Long createReservation(Long guestId, Long roomTypeId, Date checkInDate, Date checkOutDate, boolean isImmediateCheckIn) throws Exception {
        Guest guest = em.find(Guest.class, guestId);
        RoomType roomType = em.find(RoomType.class, roomTypeId);

        if (guest == null || roomType == null) {
            throw new Exception("Guest or Room Type not found.");
        }

        // Check if there are enough rooms of the requested type available
        boolean isAvailable = roomAllocationSessionBeanLocal.checkRoomTypeAvailability(roomType, checkInDate, checkOutDate);
        if (!isAvailable) {
            throw new Exception("No rooms available for the entire duration of the stay.");
        }

        // Create and persist the reservation without calculating the total amount
        Reservation reservation = new Reservation(checkInDate, checkOutDate, ReservationStatusEnum.RESERVED, guest, roomType);
        Long reservationID = createReservation(reservation);

        // For same-day check-ins after 2 am, allocate a specific room immediately
        if (isImmediateCheckIn) {
            roomAllocationSessionBeanLocal.allocateRoomForReservation(reservation);
        }

        return reservationID;
    }
    
    @Override
    public Long createReservationPartner(Long partnerID, Long roomTypeId, Date checkInDate, Date checkOutDate, boolean isImmediateCheckIn) throws Exception {
        Partner partner = em.find(Partner.class, partnerID);
        RoomType roomType = em.find(RoomType.class, roomTypeId);

        if (partner == null || roomType == null) {
            throw new Exception("Guest or Room Type not found.");
        }

        // Check if there are enough rooms of the requested type available
        boolean isAvailable = roomAllocationSessionBeanLocal.checkRoomTypeAvailability(roomType, checkInDate, checkOutDate);
        if (!isAvailable) {
            throw new Exception("No rooms available for the entire duration of the stay.");
        }

        // Create and persist the reservation without calculating the total amount
        Reservation reservation = new Reservation(checkInDate, checkOutDate, ReservationStatusEnum.RESERVED, roomType, partner);
        Long reservationID = createReservation(reservation);

        // For same-day check-ins after 2 am, allocate a specific room immediately
        if (isImmediateCheckIn) {
            roomAllocationSessionBeanLocal.allocateRoomForReservation(reservation);
        }

        return reservationID;
    }
}
