/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.Guest;
import entity.Reservation;
import exceptions.GuestNotFoundException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author hohin
 */
@Local
public interface GuestSessionBeanLocal {

    public Guest registerGuest(Guest guest);

    public Guest loginGuest(String username, String password) throws GuestNotFoundException;

    public Guest viewGuestDetails(Long guestID);

    public List<Guest> getAllGuests();

    public List<Reservation> getGuestReservations(Long guestID);
    
}
