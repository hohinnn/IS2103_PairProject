/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import exceptions.RoomTypeNotFoundException;
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
public class RoomTypeSessionBean implements RoomTypeSessionBeanRemote, RoomTypeSessionBeanLocal {

    @PersistenceContext(unitName = "HoRS-ejbPU")
    private EntityManager em;

    @Override
    public RoomType createRoomType(RoomType roomType) {
        em.persist(roomType);
        em.flush();
        return roomType;
    }
    
    @Override
    public RoomType viewRoomType(Long roomTypeID) throws RoomTypeNotFoundException {
        try {
            return em.find(RoomType.class, roomTypeID);
        } catch (NoResultException e) {
            throw new RoomTypeNotFoundException("Room Type ID: " + roomTypeID + " not found.");
        }
    }
    
    @Override
    public void updateRoomType(Long roomTypeID, String name, String description, double size, String bedType, int capacity, String amenities) {
        RoomType roomType = em.find(RoomType.class, roomTypeID);
        if (roomType != null) {
            roomType.setName(name);
            roomType.setDescription(description);
            roomType.setSize(size);
            roomType.setBedType(bedType);
            roomType.setCapacity(capacity);
            roomType.setAmenities(amenities);
            em.merge(roomType);
        }
    }
    
    @Override
    public boolean deleteRoomType(Long roomTypeID) throws IllegalStateException, RoomTypeNotFoundException{
            RoomType roomType = em.find(RoomType.class, roomTypeID);
            if (roomType != null) {
                List<Room> rooms = em.createQuery("SELECT r FROM Room r WHERE r.roomType.roomTypeID = :roomTypeID", Room.class)
                                      .setParameter("roomTypeID", roomTypeID)
                                      .getResultList();

                List<RoomRate> roomRates = em.createQuery("SELECT rr FROM RoomRate rr WHERE rr.roomType.roomTypeID = :roomTypeID", RoomRate.class)
                                              .setParameter("roomTypeID", roomTypeID)
                                              .getResultList();

                if (rooms.isEmpty() && roomRates.isEmpty()) {
                    em.remove(roomType);
                    return true; 
                } else {
                    throw new IllegalStateException("RoomType cannot be deleted as it has associated Rooms or RoomRates.");
                }
            } else {
                throw new RoomTypeNotFoundException("RoomType with ID " + roomTypeID + " not found.");
            }
        }
    
    @Override
    public List<RoomType> getAllRoomTypes() {
        return em.createQuery("SELECT rt FROM RoomType rt", RoomType.class).getResultList();
    } 
}
