/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.singleton;

import entity.Reservation;
import entity.RoomAllocation;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import javax.ejb.Remote;

/**
 *
 * @author hohin
 */
@Remote
public interface RoomAllocationSessionBeanRemote {
    
    public void allocateRoomsDaily();

    public void allocateRoomForReservation(Reservation reservation);
    
    public void allocateRoomsForDate(Date date);
    
    public List<RoomAllocation> retrieveRoomAllocationExceptions();


}
