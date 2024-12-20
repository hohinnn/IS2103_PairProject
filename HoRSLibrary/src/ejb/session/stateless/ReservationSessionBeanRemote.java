/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.Room;
import exceptions.ReservationNotFoundException;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author hohin
 */
@Remote
public interface ReservationSessionBeanRemote {

    public Long createReservation(Reservation reservation);

    public List<Reservation> viewAllReservations(long guestID);

    public Reservation viewReservation(long reservationID) throws ReservationNotFoundException;

    public List<Reservation> walkInReserveRooms(String guestName, String phoneNumber, String email, String passportNumber, Date checkInDate, Date checkOutDate, Date searchTime, int numRooms, Long roomTypeID);
    
    public void checkInGuest(long reservationId) throws ReservationNotFoundException;

    public void checkOutGuest(long reservationId) throws ReservationNotFoundException;

    public Long createReservation(Long guestId, Long roomId, Date checkInDate, Date checkOutDate, boolean isImmediateCheckIn) throws Exception;

    public Long createReservationPartner(Long partnerID, Long roomTypeId, Date checkInDate, Date checkOutDate, boolean isImmediateCheckIn) throws Exception;

    public List<Reservation> viewAllReservationsPartner(long partnerID);

    public Reservation viewReservationByGuest(Long reservationID, Long guestID) throws ReservationNotFoundException;

    public Reservation viewReservationByPartner(Long reservationID, Long partnerID) throws ReservationNotFoundException;

}
