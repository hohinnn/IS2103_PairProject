/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsreservationclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Guest;
import entity.Reservation;
import entity.Room;
import exceptions.GuestNotFoundException;
import exceptions.ReservationNotFoundException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author hohin
 */
public class MainApp {
    
    private GuestSessionBeanRemote guestSessionBeanRemote;
    private PartnerSessionBeanRemote partnerSessionBeanRemote;
    private ReservationSessionBeanRemote reservationSessionBeanRemote;
    private RoomRateSessionBeanRemote roomRateSessionBeanRemote;
    private RoomSessionBeanRemote roomSessionBeanRemote;
    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;
    
    private Guest currentGuest = null;
    private Scanner scanner;

    public MainApp() {}

    public MainApp(GuestSessionBeanRemote guestSessionBeanRemote, PartnerSessionBeanRemote partnerSessionBeanRemote, ReservationSessionBeanRemote reservationSessionBeanRemote, RoomRateSessionBeanRemote roomRateSessionBeanRemote, RoomSessionBeanRemote roomSessionBeanRemote, RoomTypeSessionBeanRemote roomTypeSessionBeanRemote) {
        this.guestSessionBeanRemote = guestSessionBeanRemote;
        this.partnerSessionBeanRemote = partnerSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.roomRateSessionBeanRemote = roomRateSessionBeanRemote;
        this.roomSessionBeanRemote = roomSessionBeanRemote;
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
        this.scanner = new Scanner(System.in);
    }
    
    public void runApp(){
        Integer response = 0;
        while(true)
        {
            System.out.println("*** Welcome to HoRS Reservation Client ***\n");
            System.out.println("1: Guest Login");
            System.out.println("2: Register as Guest");
            System.out.println("3: Search Hotel Room");
            System.out.println("4: Exit\n");
            System.out.print("> ");
            response = scanner.nextInt();
            
            switch(response) {
                case 1:
                    doLogin();
                case 2:
                    doRegisterAsGuest();
                case 3:
                    doSearchHotelRoomVisitor();
                case 4:
                    System.out.println("Exiting HoRS Reservation Client...");
                    return;
                default:
                    System.out.println("Invalid option, please try again!\n");     
            }
        }
    }
    
    public void doLogin(){
        System.out.println("\n*** HoRS Reservation Client :: Login ***\n");
        System.out.print("Enter username> ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        String password = scanner.nextLine().trim();

        try {
            currentGuest = guestSessionBeanRemote.loginGuest(username, password);
            System.out.println("Login successful! Welcome " + currentGuest.getUsername() + ".\n");
            menuMain();
        } catch (GuestNotFoundException e) {
            System.out.println("Invalid login: " + e.getMessage());
        }
    }
    
    private void doLogout() {
        currentGuest = null;
        System.out.println("You have logged out successfully.\n");
    }
    
    private void menuMain() {
        Integer response;

        while(true) {
            System.out.println("*** Welcome "+ currentGuest.getUsername() + " ***\n");
            System.out.println("1: Search Hotel Room");
            System.out.println("2: View My Reservation Details");
            System.out.println("3: View All My Reservations");
            System.out.println("4: Logout\n");
            System.out.print("> ");
            response = scanner.nextInt();

            switch(response) {
                case 1:
                    doSearchHotelRoomGuest();
                case 2:
                    doViewReservationDetails();
                case 3:
                    doViewAllReservations();
                case 4:
                    doLogout();
                    return;
                default:
                    System.out.println("Invalid option, please try again!\n");     
            }
        }
    }
    
    private void doRegisterAsGuest() {
        System.out.print("\n*** Register as New Guest ***\n");
        System.out.print("Enter Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter Phone Number: ");
        String phoneNumber = scanner.nextLine().trim();
        System.out.print("Enter Passport Number: ");
        String passportNumber = scanner.nextLine().trim();
        System.out.print("Enter Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();
        
        Guest guest = guestSessionBeanRemote.registerGuest(new Guest(name, email, phoneNumber, passportNumber, username, password));
        System.out.println("Registered Successfully! Guest Username: " + guest.getUsername());
    }
    
    public void doSearchHotelRoomVisitor() {
        try {
            searchHotelRoomFunction();
            Integer response;
            System.out.println("----------------------------");
            System.out.println("1. Make Booking");
            System.out.println("2. Exit");
            response = scanner.nextInt();
            switch(response){
                case 1:
                    doReserveHotelRoom();
                case 2:
                    return;
            }
            
        } catch (Exception e) {
            System.out.println("An error occurred while searching for rooms: " + e.getMessage());
        }
    }
    
    public void doSearchHotelRoomGuest() {
        try {
            searchHotelRoomFunction();
            System.out.println("\nTo book room, please log in or create account!");
        } catch (Exception e) {
            System.out.println("An error occurred while searching for rooms: " + e.getMessage());
        }
    }
    
    //NOT DONE
    public void doReserveHotelRoom() {
        //Implementation
    }
    
    public void searchHotelRoomFunction() {
            System.out.println("\n*** Search Hotel Room ***\n");

            System.out.print("Enter check-in date (YYYY-MM-DD): ");
            String checkInStr = scanner.nextLine();
            Date checkInDate = Date.valueOf(checkInStr);

            System.out.print("Enter check-out date (YYYY-MM-DD): ");
            String checkOutStr = scanner.nextLine();
            Date checkOutDate = Date.valueOf(checkOutStr);

            if (checkInDate.after(checkOutDate)) {
                System.out.println("Check-out date must be after check-in date.");
                return;
            }

            List<Room> availableRooms = roomSessionBeanRemote.searchAvailableRooms(checkInDate, checkOutDate);

            if (availableRooms.isEmpty()) {
                System.out.println("No available rooms for the specified dates.");
                return;
            }

            System.out.println("\nAvailable Rooms:");
            for (Room room : availableRooms) {
                BigDecimal rate = roomRateSessionBeanRemote.calculateRateForRoomType(room.getRoomType(), checkInDate, checkOutDate);
                System.out.printf("Room Type: %s | Rate per Night: %s | Total: $%s\n",
                        room.getRoomType().getName(), rate, rate.multiply(BigDecimal.valueOf(daysBetween(checkInDate, checkOutDate))));
            }
    }

    private long daysBetween(Date start, Date end) {
        return ChronoUnit.DAYS.between(start.toInstant(), end.toInstant());
    }
    
    private void doViewReservationDetails(){
        Long response;
        System.out.println("\n*** View Reservation Details ***\n");
        System.out.println("Enter Reservation ID:");
        response = scanner.nextLong();
        try {
            Reservation r = reservationSessionBeanRemote.viewReservation(response);
            System.out.println("*** Reservation ID: " + r.getReservationID() + " ***");
            System.out.println("Room Type: " + r.getRoomType().getName());
            System.out.println("Check-in Date: " + r.getCheckInDate());
            System.out.println("Check-out Date: " + r.getCheckOutDate());
            System.out.println("Status: " + r.getStatus());
            System.out.println("Total Amount: " + r.getTotalAmount());
        } catch (ReservationNotFoundException e) {
            System.out.println(e.getMessage());
        }
        
    }
    
    private void doViewAllReservations(){
        List<Reservation> reservations = reservationSessionBeanRemote.viewAllReservations(currentGuest.getGuestID());
        if (reservations.isEmpty()) {
            System.out.println("No Reservations Found");
        } else {
            for (Reservation r : reservations){
                System.out.println("Reservation ID: " + r.getReservationID());
                System.out.println("Room Type: " + r.getRoomType().getName());
                System.out.println("Check-in Date: " + r.getCheckInDate());
                System.out.println("Check-out Date: " + r.getCheckOutDate());
                System.out.println("Status: " + r.getStatus());
                System.out.println("Total Amount: " + r.getTotalAmount());
                System.out.println("----------------------------");
            }
        }
    }
    
    
}