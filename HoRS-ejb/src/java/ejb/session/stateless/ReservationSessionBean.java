/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Guest;
import entity.Reservation;
import exceptions.ReservationNotFoundException;
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
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @PersistenceContext(unitName = "HoRS-ejbPU")
    private EntityManager em;

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
    
}
