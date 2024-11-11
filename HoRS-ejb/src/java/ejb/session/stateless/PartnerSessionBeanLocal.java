/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.Partner;
import entity.Reservation;
import entity.Room;
import exceptions.PartnerNotFoundException;
import exceptions.ReservationNotFoundException;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author hohin
 */
@Local
public interface PartnerSessionBeanLocal {

    public Partner createPartner(Partner partner);

    public List<Partner> viewAllPartners();

    public Partner partnerLogin(String username, String password) throws PartnerNotFoundException;

    public List<Room> searchAvailableRooms(Date checkInDate, Date checkOutDate);

    public List<Reservation> viewAllPartnerReservations(Long partnerId) throws PartnerNotFoundException;

    public Reservation viewPartnerReservationDetails(Long reservationId) throws ReservationNotFoundException;

    public Long partnerReservation(Long partnerID, Long roomTypeId, Date checkInDate, Date checkOutDate, boolean isImmediateCheckIn) throws Exception;
    
}
