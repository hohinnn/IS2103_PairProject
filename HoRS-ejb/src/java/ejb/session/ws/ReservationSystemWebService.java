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
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.Partner;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import enumType.ReservationStatusEnum;
import exceptions.PartnerNotFoundException;
import exceptions.ReservationNotFoundException;
import exceptions.RoomNotFoundException;
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
import javax.xml.datatype.XMLGregorianCalendar;

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
    private RoomSessionBeanLocal roomSessionBeanLocal;

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;

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

    @WebMethod(operationName = "searchAvailableRooms")
    public List<Room> searchAvailableRooms(@WebParam(name = "checkInDate") Date checkInDate,
            @WebParam(name = "checkOutDate") Date checkOutDate) {
        List<Room> availableRooms = partnerSessionBeanLocal.searchAvailableRooms(checkInDate, checkOutDate);
        for (Room room : availableRooms) {
            RoomType roomType = room.getRoomType();
            if (roomType != null) {
                roomType.getRoomRates().size(); // Force fetch rates
                for (RoomRate rate : roomType.getRoomRates()) {
                    rate.getRoomType(); // Ensure RoomRate references are fetched
                }
            }

            // Detach entities to prevent cyclic dependencies
            em.detach(room);
            em.detach(roomType);
            for (RoomRate rr : roomType.getRoomRates()) {
                em.detach(rr);
            }
        }
        return availableRooms;
    }

    @WebMethod(operationName = "reserveRooms")
    public Long reserveRooms(@WebParam(name = "partnerId") Long partnerId,
                            @WebParam(name = "roomId") Long roomId,
                            @WebParam(name = "checkInDate") Date checkInDate,
                            @WebParam(name = "checkOutDate") Date checkOutDate) throws RoomNotFoundException {
        Room room = roomSessionBeanLocal.getRoomById(roomId);
        RoomType roomType = room.getRoomType();
        BigDecimal totalAmount = roomRateSessionBeanLocal.calculateRateForRoomType(roomType, checkInDate, checkOutDate);

        // Create a new reservation
        Reservation reservation = new Reservation(checkInDate, checkOutDate, ReservationStatusEnum.RESERVED, totalAmount, null, roomType, room, null);
        return reservationSessionBeanLocal.createReservation(reservation);
    }

    @WebMethod(operationName = "viewPartnerReservationDetails")
    public Reservation viewPartnerReservationDetails(@WebParam(name = "reservationId") Long reservationId) {
        try {
            Reservation reservation = reservationSessionBeanLocal.viewReservation(reservationId);
            if (reservation != null) {
                em.detach(reservation);
                em.detach(reservation.getRoomType());
                em.detach(reservation.getPartner());
                return reservation;
            }
        } catch (ReservationNotFoundException e) {
            System.out.println("Error retrieving reservation details: " + e.getMessage());
        }
        return null;
    }

    @WebMethod(operationName = "viewAllPartnerReservations")
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

    @WebMethod(operationName = "calculateRateForRoomType")
    public BigDecimal calculateRateForRoomType(@WebParam(name = "roomType") RoomType roomType,
            @WebParam(name = "checkInDate") XMLGregorianCalendar checkInDate,
            @WebParam(name = "checkOutDate") XMLGregorianCalendar checkOutDate) {
        Date checkIn = checkInDate.toGregorianCalendar().getTime();
        Date checkOut = checkOutDate.toGregorianCalendar().getTime();
        BigDecimal rate = roomRateSessionBeanLocal.calculateRateForRoomType(roomType, checkIn, checkOut);
        return rate;
    }
    
    @WebMethod(operationName = "getRoomById")
    public Room getRoomById(@WebParam(name = "roomId") Long roomId) throws RoomNotFoundException {
        return roomSessionBeanLocal.getRoomById(roomId);
    }
    
    @WebMethod(operationName = "getPublishedRateForRoomType")
    public RoomRate getPublishedRateForRoomType(
            @WebParam(name = "roomType") RoomType roomType,
            @WebParam(name = "checkInDate") XMLGregorianCalendar checkInDate,
            @WebParam(name = "checkOutDate") XMLGregorianCalendar checkOutDate) {

        Date checkIn = checkInDate.toGregorianCalendar().getTime();
        Date checkOut = checkOutDate.toGregorianCalendar().getTime();
        return roomRateSessionBeanLocal.getPublishedRateForRoomType(roomType, checkIn, checkOut);
    }

}
