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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
            Partner partner = partnerSessionBeanLocal.partnerLogin(username, password);
            return detachPartner(partner);
        } catch (PartnerNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @WebMethod(operationName = "searchAvailableRooms")
    public List<Room> searchAvailableRooms(@WebParam(name = "checkInDate") XMLGregorianCalendar checkInDate,
            @WebParam(name = "checkOutDate") XMLGregorianCalendar checkOutDate) {
        Date checkIn = checkInDate.toGregorianCalendar().getTime();
        Date checkOut = checkOutDate.toGregorianCalendar().getTime();
        List<Room> availableRooms = partnerSessionBeanLocal.searchAvailableRooms(checkIn, checkOut);
        List<Room> detachedRooms = new ArrayList<>();
        for (Room room : availableRooms) {
            detachedRooms.add(detachRoom(room));
        }
        return detachedRooms;
    }

    @WebMethod(operationName = "reserveRoomType")
    public Long reserveRoomType(@WebParam(name = "partnerId") Long partnerId,
                                @WebParam(name = "roomTypeId") Long roomTypeId,
                                @WebParam(name = "checkInDate") XMLGregorianCalendar checkInDate,
                                @WebParam(name = "checkOutDate") XMLGregorianCalendar checkOutDate) 
                                throws RoomNotFoundException {
        Date checkIn = checkInDate.toGregorianCalendar().getTime();
        Date checkOut = checkOutDate.toGregorianCalendar().getTime();

        // Find partner and validate
        Partner partner = em.find(Partner.class, partnerId);
        if (partner == null) {
            throw new IllegalArgumentException("Partner not found for ID: " + partnerId);
        }

        // Find room type and check availability
        RoomType roomType = em.find(RoomType.class, roomTypeId);
        if (roomType == null) {
            throw new IllegalArgumentException("Room type not found for ID: " + roomTypeId);
        }

        // Get available rooms of the specified room type
        List<Room> availableRooms = roomSessionBeanLocal.searchAvailableRooms(checkIn, checkOut);

        // Filter available rooms by the requested room type
        List<Room> availableRoomOfType = availableRooms.stream()
                .filter(room -> room.getRoomType().getRoomTypeID().equals(roomTypeId))
                .collect(Collectors.toList());

        if (availableRoomOfType.isEmpty()) {
            throw new RoomNotFoundException("No available rooms for the specified room type and date range.");
        }

        // Calculate total rate for the stay
        BigDecimal totalAmount = roomRateSessionBeanLocal.calculateRateForRoomType(roomType, checkIn, checkOut);

        // Select the first available room for reservation (you can modify this logic if you want to reserve more than one)
        Room selectedRoom = availableRoomOfType.get(0);

        // Create a new reservation
        Reservation reservation = new Reservation();
        reservation.setCheckInDate(checkIn);
        reservation.setCheckOutDate(checkOut);
        reservation.setStatus(ReservationStatusEnum.RESERVED);
        reservation.setTotalAmount(totalAmount);
        reservation.setPartner(partner);
        reservation.setRoom(selectedRoom);
        reservation.setRoomType(roomType);

        // Get and set the applicable room rate
        RoomRate roomRate = roomRateSessionBeanLocal.getPublishedRateForRoomType(roomType, checkIn, checkOut);
        if (roomRate != null) {
            reservation.setRoomRate(roomRate);
        }

        reservation.setGuest(null); // Since this is a partner booking

        // Persist reservation and return its ID
        return reservationSessionBeanLocal.createReservation(reservation);
    }



    @WebMethod(operationName = "viewPartnerReservationDetails")
    public Reservation viewPartnerReservationDetails(@WebParam(name = "reservationId") Long reservationId, 
            @WebParam(name = "partnerId") Long partnerId) {
        try {
            Reservation reservation = reservationSessionBeanLocal.viewReservationByPartner(reservationId, partnerId);
            return detachReservation(reservation);
        } catch (ReservationNotFoundException e) {
            System.out.println("Error retrieving reservation details: " + e.getMessage());
        }
        return null;
    }

    @WebMethod(operationName = "viewAllPartnerReservations")
    public List<Reservation> viewAllPartnerReservations(@WebParam(name = "partnerId") Long partnerId) {
        try {
            List<Reservation> reservations = reservationSessionBeanLocal.viewAllReservationsPartner(partnerId);
            List<Reservation> detachedReservations = new ArrayList();
            for (Reservation r : reservations) {
                detachedReservations.add(detachReservation(r));
            }
            return detachedReservations;
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
        return detachRoomRate(roomRateSessionBeanLocal.getPublishedRateForRoomType(roomType, checkIn, checkOut));
    }

    private Room detachRoom(Room room) {
        if (room == null) {
            return null;
        }

        Room detachedRoom = new Room();
        detachedRoom.setRoomID(room.getRoomID());
        detachedRoom.setFormattedRoomSequence(room.getFormattedRoomSequence());
        detachedRoom.setStatus(room.getStatus());

        // Detach references to prevent cyclic dependencies
        detachedRoom.setRoomType(detachRoomType(room.getRoomType()));
        return detachedRoom;
    }

    private RoomType detachRoomType(RoomType roomType) {
        if (roomType == null) {
            return null;
        }

        RoomType detachedRoomType = new RoomType();
        detachedRoomType.setRoomTypeID(roomType.getRoomTypeID());
        detachedRoomType.setName(roomType.getName());

        // Detach relationships to prevent cyclic dependencies
        detachedRoomType.setRoomRates(null); // Detach room rates
        detachedRoomType.setRooms(null); // Detach rooms
        return detachedRoomType;
    }

    private Reservation detachReservation(Reservation reservation) {
        if (reservation == null) {
            return null;
        }

        Reservation detachedReservation = new Reservation();
        detachedReservation.setReservationID(reservation.getReservationID());
        detachedReservation.setCheckInDate(reservation.getCheckInDate());
        detachedReservation.setCheckOutDate(reservation.getCheckOutDate());
        detachedReservation.setStatus(reservation.getStatus());
        detachedReservation.setTotalAmount(reservation.getTotalAmount());

        // Detach related entities
        detachedReservation.setRoom(detachRoom(reservation.getRoom()));
        detachedReservation.setRoomType(detachRoomType(reservation.getRoomType()));
        detachedReservation.setGuest(null); // Detach guest to avoid cycles
        return detachedReservation;
    }

    private RoomRate detachRoomRate(RoomRate roomRate) {
        if (roomRate != null) {
            roomRate.setRoomType(null);
        }
        return roomRate;
    }

    private Partner detachPartner(Partner partner) {
        if (partner != null) {
            partner.setPartnerReservations(null);
        }
        return partner;
    }

}
