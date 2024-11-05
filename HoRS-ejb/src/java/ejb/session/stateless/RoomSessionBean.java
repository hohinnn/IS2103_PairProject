/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Room;
import enumType.RoomAvailabilityEnum;
import exceptions.RoomNotFoundException;
import java.util.Date;
import java.util.List;
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
public class RoomSessionBean implements RoomSessionBeanRemote, RoomSessionBeanLocal {

    @PersistenceContext(unitName = "HoRS-ejbPU")
    private EntityManager em;

    @Override
    public Room createRoom(Room room) {
        em.persist(room);
        em.flush();
        return room;
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
    public void updateRoomStatus(String formattedRoomSequence, RoomAvailabilityEnum newStatus) throws RoomNotFoundException {
        try {
            Room room = em.createQuery("SELECT r FROM Room r WHERE r.formattedRoomSequence = :formattedRoomSequence", Room.class)
                     .setParameter("formattedRoomSequence", formattedRoomSequence)
                     .getSingleResult();
            room.setStatus(newStatus);
            em.merge(room);
        } catch (NoResultException e) {
            throw new RoomNotFoundException("Room " + formattedRoomSequence + " not found.");
        }
    }
    
    @Override
    public void deleteRoom(String formattedRoomSequence) throws RoomNotFoundException {
        try {
            Room room = em.createQuery("SELECT r FROM Room r WHERE r.formattedRoomSequence = :formattedRoomSequence", Room.class)
                     .setParameter("formattedRoomSequence", formattedRoomSequence)
                     .getSingleResult();
            em.remove(room);
        } catch (NoResultException e) {
            throw new RoomNotFoundException("Room " + formattedRoomSequence + " not found.");
        }
    }
    
    @Override
    public List<Room> getAllRooms() {
        return em.createQuery("SELECT r FROM Room r", Room.class).getResultList();
    }
}
