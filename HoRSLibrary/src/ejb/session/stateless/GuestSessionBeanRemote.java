/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.Guest;
import entity.Reservation;
import exceptions.GuestNotFoundException;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author hohin
 */
@Remote
public interface GuestSessionBeanRemote {

    public Guest registerGuest(Guest guest);

    public Guest loginGuest(String username, String password) throws GuestNotFoundException;

    public Guest viewGuestDetails(Long guestID);

    public List<Guest> getAllGuests();
    
    public List<Reservation> getGuestReservations(Long guestID);
}
