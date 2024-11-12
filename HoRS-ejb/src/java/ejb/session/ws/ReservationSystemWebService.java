/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/EjbWebService.java to edit this template
 */
package ejb.session.ws;

import ejb.session.singleton.RoomAllocationSessionBeanLocal;
import ejb.session.stateless.PartnerSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import ejb.session.stateless.RoomRateSessionBeanLocal;
import ejb.session.stateless.RoomSessionBeanLocal;
import entity.Partner;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import exceptions.PartnerNotFoundException;
import exceptions.RoomTypeNotFoundException;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author hohin
 */
@WebService(serviceName = "ReservationSystemWebService")
@Stateless()
public class ReservationSystemWebService {

    @EJB(name = "RoomRateSessionBeanLocal")
    private RoomRateSessionBeanLocal roomRateSessionBeanLocal;

    @PersistenceContext(unitName = "HoRS-ejbPU")
    private EntityManager em;
    
    @EJB
    private PartnerSessionBeanLocal partnerSessionBeanLocal;
    
    @EJB
    private RoomAllocationSessionBeanLocal roomAllocationSessionBean;
    
    @EJB(name = "ReservationSessionBeanLocal")
    private ReservationSessionBeanLocal reservationSessionBeanLocal;
    
    
    

    @WebMethod(operationName = "partnerLogin")
    public Partner partnerLogin(@WebParam(name = "username") String username,
                                @WebParam(name = "password") String password) throws PartnerNotFoundException {
        try {
            return partnerSessionBeanLocal.partnerLogin(username, password);
        } catch (PartnerNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    @WebMethod
    public List<Room> searchAvailableRooms(@WebParam(name = "checkInDate") Date checkInDate,
                                               @WebParam(name = "checkOutDate") Date checkOutDate) {
        List<Room> availableRooms = partnerSessionBeanLocal.searchAvailableRooms(checkInDate, checkOutDate);
        for (Room room : availableRooms) {
            em.detach(room);
            em.detach(room.getRoomType());
            for (Room r : room.getRoomType().getRooms()) {
                em.detach(room);
                room.setRoomType(null);
            }
        }
        return availableRooms;
    }

    @WebMethod
    public Long partnerReserveRoom(@WebParam(name = "partnerID") Long partnerID,
                                          @WebParam(name = "roomTypeId") Long roomTypeId,
                                          @WebParam(name = "checkInDate") Date checkInDate,
                                          @WebParam(name = "checkOutDate") Date checkOutDate,
                                          @WebParam(name = "numRooms") int numRooms) {
        try {
            Date currentDate = new Date();
            boolean isImmediateCheckIn = checkInDate.equals(currentDate) && currentDate.toInstant().atZone(ZoneId.systemDefault()).getHour() >= 2;
            Long reservationID = reservationSessionBeanLocal.createReservationPartner(partnerID, roomTypeId, checkInDate, checkOutDate, isImmediateCheckIn);
            return reservationID;
        } catch (Exception e) {
            System.out.println("Error during reservation: " + e.getMessage());
        }
        return null;
    }
    
    @WebMethod
    public Reservation viewPartnerReservationDetails(@WebParam(name = "reservationId") Long reservationId) {
        try {
            Reservation reservation = reservationSessionBeanLocal.viewReservation(reservationId);
            if (reservation != null) {
                em.detach(reservation);
                em.detach(reservation.getRoomType());
                em.detach(reservation.getPartner());
                return reservation;
            }
        } catch (Exception e) {
            System.out.println("Error retrieving reservation details: " + e.getMessage());
        }
        return null;
    }
    
    @WebMethod
    public List<Reservation> viewAllPartnerReservations(@WebParam(name = "partnerId") Long partnerId) {
        try {
            List<Reservation> reservations = reservationSessionBeanLocal.viewAllReservationsPartner(partnerId);
            for (Reservation r : reservations) {
                em.detach(r);
                em.detach(r.getRoomType());
                em.detach(r.getPartner());
            }
            return reservations;
        } catch (Exception e) {
            System.out.println("Error retrieving reservations: " + e.getMessage());
            return null;
        }
    }
    
    @WebMethod
    public BigDecimal calculateRateForRoomType(@WebParam(name = "roomType") RoomType roomType,
                                               @WebParam(name = "checkInDate") Date checkInDate,
                                               @WebParam(name = "checkOutDate") Date checkOutDate) {
        BigDecimal rate = roomRateSessionBeanLocal.calculateRateForRoomType(roomType, checkInDate, checkOutDate);
        return rate;
    }
    
}
