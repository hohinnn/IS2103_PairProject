/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import javax.ejb.EJB;

/**
 *
 * @author hohin
 */
public class Main {

    @EJB
    private static EmployeeSessionBeanRemote employeeSessionBeanRemote;
    @EJB
    private static GuestSessionBeanRemote guestSessionBeanRemote;
    @EJB
    private static PartnerSessionBeanRemote partnerSessionBeanRemote;
    @EJB
    private static ReservationSessionBeanRemote reservationSessionBeanRemote;
    @EJB
    private static RoomRateSessionBeanRemote roomRateSessionBeanRemote;
    @EJB
    private static RoomSessionBeanRemote roomSessionBeanRemote;
    @EJB
    private static RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;
    
    
    public static void main(String[] args) {
        MainApp mainApp = new MainApp(employeeSessionBeanRemote, guestSessionBeanRemote, partnerSessionBeanRemote, reservationSessionBeanRemote, 
                roomRateSessionBeanRemote, roomSessionBeanRemote, roomTypeSessionBeanRemote);
        mainApp.runApp();
    }
    
}
