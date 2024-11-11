/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Partner;
import entity.Reservation;
import entity.Room;
import entity.RoomType;
import exceptions.PartnerNotFoundException;
import exceptions.ReservationNotFoundException;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author hohin
 */
@Stateless
public class PartnerSessionBean implements PartnerSessionBeanRemote, PartnerSessionBeanLocal {

    @EJB
    private ReservationSessionBeanLocal reservationSessionBeaLocal;

    @PersistenceContext(unitName = "HoRS-ejbPU")
    private EntityManager em;
    
    

    @Override
    public Partner createPartner(Partner partner) {
        em.persist(partner);
        em.flush(); 
        return partner;
    }
    
    @Override
    public List<Partner> viewAllPartners() {
        return em.createQuery("SELECT p FROM Partner p", Partner.class)
                .getResultList();
    }
    
    @Override
    public Partner partnerLogin(String username, String password) throws PartnerNotFoundException {
        try {
            Partner partner = em.createQuery("SELECT p FROM Partner p WHERE p.username = :username", Partner.class)
                                .setParameter("username", username)
                                .getSingleResult();

            if (partner.getPassword().equals(password)) {
                return partner;
            } else {
                throw new PartnerNotFoundException("Invalid password.");
            }
        } catch (NoResultException ex) {
            throw new PartnerNotFoundException("Partner username does not exist.");
        }
    }
    
    @Override
    public List<Room> searchAvailableRooms(Date checkInDate, Date checkOutDate) {
        Query query = em.createQuery("SELECT r FROM Room r WHERE r.roomID NOT IN " +
                "(SELECT res.room.roomID FROM Reservation res WHERE " +
                "(res.checkInDate <= :checkOutDate AND res.checkOutDate >= :checkInDate))");
        query.setParameter("checkInDate", checkInDate);
        query.setParameter("checkOutDate", checkOutDate);
        return query.getResultList();
    }
    
    @Override
    public List<Reservation> viewAllPartnerReservations(Long partnerId) throws PartnerNotFoundException {
        Partner partner = em.find(Partner.class, partnerId);
        if (partner == null) {
            throw new PartnerNotFoundException("Partner not found.");
        }
        List<Reservation> reservations = partner.getPartnerReservations();
        return reservations;
    }
    
    
    @Override
    public Reservation viewPartnerReservationDetails(Long reservationId) throws ReservationNotFoundException {
        Reservation reservation = em.find(Reservation.class, reservationId);
        if (reservation == null) {
            throw new ReservationNotFoundException("Reservation not found.");
        }
        return reservation;
    }
    
    @Override
    public Long partnerReservation(Long partnerID, Long roomTypeId, Date checkInDate, Date checkOutDate, boolean isImmediateCheckIn) throws Exception {
        return reservationSessionBeaLocal.createReservationPartner(partnerID, roomTypeId, checkInDate, checkOutDate, isImmediateCheckIn);
    }
    
    
    
    
    
    
    
    

}
