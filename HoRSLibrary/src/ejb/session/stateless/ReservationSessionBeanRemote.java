/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.Reservation;
import exceptions.ReservationNotFoundException;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author hohin
 */
@Remote
public interface ReservationSessionBeanRemote {
    
        public Reservation createReservation(Reservation reservation);
        
        public List<Reservation> viewAllReservations(long guestID);

        public Reservation viewReservation(long reservationID) throws ReservationNotFoundException;


}
