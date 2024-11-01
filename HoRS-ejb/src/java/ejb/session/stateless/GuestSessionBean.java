/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Guest;
import entity.Reservation;
import exceptions.GuestNotFoundException;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author hohin
 */
@Stateless
public class GuestSessionBean implements GuestSessionBeanRemote, GuestSessionBeanLocal {

    @PersistenceContext(unitName = "HoRS-ejbPU")
    private EntityManager em;

    public Guest registerGuest(Guest guest) {
        em.persist(guest);
        em.flush();
        return guest;
    }
    
    public Guest loginGuest(String username) throws GuestNotFoundException {
        try {
            return em.createQuery("SELECT g FROM Guest g WHERE g.username = :username", Guest.class)
                     .setParameter("username", username)
                     .getSingleResult();
        } catch (NoResultException e) {
            throw new GuestNotFoundException("Guest with username: " + username + " not found.");
        }
    }
    
    public Guest viewGuestDetails(Long guestID) {
        return em.find(Guest.class, guestID);
    }
    
    public List<Guest> getAllGuests() {
        return em.createQuery("SELECT g FROM Guest g", Guest.class).getResultList();
    }
    
    public List<Reservation> getGuestReservations(Long guestID) {
        return em.createQuery("SELECT r FROM Reservation r WHERE r.guest.guestID = :guestID", Reservation.class)
                 .setParameter("guestID", guestID)
                 .getResultList();
    }
    
}
