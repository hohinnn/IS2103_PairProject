/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.Room;
import enumType.RoomAvailabilityEnum;
import exceptions.RoomNotFoundException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author hohin
 */
@Local
public interface RoomSessionBeanLocal {

    public Room createRoom(Room room);

    public void updateRoomStatus(String formattedRoomSequence, RoomAvailabilityEnum newStatus) throws RoomNotFoundException;

    public void deleteRoom(String formattedRoomSequence) throws RoomNotFoundException;

    public List<Room> getAllRooms();
    
}
