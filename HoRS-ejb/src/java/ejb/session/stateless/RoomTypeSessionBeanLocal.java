/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.RoomType;
import exceptions.RoomTypeNotFoundException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author hohin
 */
@Local
public interface RoomTypeSessionBeanLocal {

    public RoomType createRoomType(RoomType roomType);

    public RoomType viewRoomType(Long roomTypeID) throws RoomTypeNotFoundException;

    public void updateRoomType(Long roomTypeID, String name, int priorityRanking, String description, double size, String bedType, int capacity, String amenities);

    public boolean deleteRoomType(Long roomTypeID) throws IllegalStateException, RoomTypeNotFoundException;

    public List<RoomType> getAllRoomTypes();
    
}
