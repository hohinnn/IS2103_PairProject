/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package holidayreservationsystem;

import ejb.session.ws.ReservationSystemWebService_Service;

/**
 *
 * @author hohin
 */
public class HolidayReservationSystem {

    /**
     * @param args the command line arguments
     */
      public static void main(String[] args) {
        ReservationSystemWebService_Service service = new ReservationSystemWebService_Service();
        MainApp mainApp = new MainApp(service);
        mainApp.runApp();
    }
    
}
