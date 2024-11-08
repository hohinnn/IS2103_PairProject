/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Guest;
import entity.Reservation;
import entity.Room;
import entity.RoomType;
import exceptions.ReservationNotFoundException;
import java.math.BigDecimal;
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
    private RoomRateSessionBeanRemote roomRateSessionBean;

    @Override
    public Reservation createReservation(Reservation reservation) {
        em.persist(reservation);
        em.flush();
        return reservation;
    }
    
    @Override
    public List<Reservation> viewAllReservations(long guestID) {
        Guest guest = em.createQuery("SELECT g from GUEST g WHERE g.guestID = :guestID", Guest.class)
                        .setParameter("guestID", guestID)
                        .getSingleResult();
        return guest.getReservations();
    }
    
    @Override
    public Reservation viewReservation(long reservationID) throws ReservationNotFoundException{
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
        boolean immediateCheckIn = checkInDate.equals(currentDate) && currentDate.getHours() >= 2;

        List<Reservation> reservations = new ArrayList<>();
        for (Room room : rooms) {
            RoomType roomType = room.getRoomType();
            BigDecimal totalAmount = roomRateSessionBean.calculateRateForRoomType(roomType, checkInDate, checkOutDate);

            Reservation reservation = new Reservation(checkInDate, checkOutDate, immediateCheckIn ? "Checked-In" : "Reserved", totalAmount, guest, roomType, room, null);
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

        if (!reservation.getStatus().equals("Reserved")) {
            throw new IllegalStateException("Reservation is not in a state that allows check-in.");
        }

        reservation.setStatus("Checked-In");
        em.merge(reservation);
    }

    //Marks the reservation as "Completed" if it is currently in "Checked-In" status
    @Override
    public void checkOutGuest(long reservationId) throws ReservationNotFoundException {
        Reservation reservation = em.find(Reservation.class, reservationId);
        if (reservation == null) {
            throw new ReservationNotFoundException("Reservation ID " + reservationId + " not found.");
        }

        if (!reservation.getStatus().equals("Checked-In")) {
            throw new IllegalStateException("Guest is not currently checked-in.");
        }

        reservation.setStatus("Completed");
        em.merge(reservation);
    }
}
