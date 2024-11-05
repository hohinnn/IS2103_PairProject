/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.Reservation;
import exceptions.ReservationNotFoundException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author hohin
 */
@Local
public interface ReservationSessionBeanLocal {

    public Reservation createReservation(Reservation reservation);

    public List<Reservation> viewAllReservations(long guestID);

    public Reservation viewReservation(long reservationID) throws ReservationNotFoundException;
    
}
