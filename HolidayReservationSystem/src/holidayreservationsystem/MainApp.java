/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package holidayreservationsystem;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author hohin
 */
public class MainApp {

    private ReservationSystemWebService_Service service;
    private Scanner scanner = new Scanner(System.in);
    private Partner currentPartner = null;

    public MainApp() {
    }

    public MainApp(ReservationSystemWebService_Service service) {
        this.service = service;
    }

    public void runApp() {
        Integer response = 0;
        while (true) {
            System.out.println("*** Welcome to Holiday Reservation System ***\n");
            System.out.println("1: Partner Login");
            System.out.println("2: Partner Search Room");
            System.out.println("3: Exit\n");
            System.out.print("> ");
            response = scanner.nextInt();
            scanner.nextLine();

            switch (response) {
                case 1:
                    doLogin();
                    break;
                case 2:
                    doSearchHotelRoomPartnerEmployee();
                    break;
                case 3:
                    System.out.println("Exiting Holiday Reservation System...");
                    return;
                default:
                    System.out.println("Invalid option, please try again!\n");
            }
        }
    }

    public void doLogin() {
        System.out.println("\n*** Holiday Reservation System :: Partner Login ***\n");
        System.out.print("Enter username> ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        String password = scanner.nextLine().trim();

        try {
            currentPartner = service.getReservationSystemWebServicePort().partnerLogin(username, password);
            
// Check if currentPartner is null
            if (currentPartner == null) {
                System.out.println("Invalid login: Partner not found.");
                return; // Exit if no partner found
            }
            
            System.out.println("Login successful! Welcome " + currentPartner.getUsername() + ".\n");
            menuMain();
        } catch (PartnerNotFoundException_Exception e) {
            System.out.println("Invalid login: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void doLogout() {
        currentPartner = null;
        System.out.println("You have logged out successfully.\n");
    }

    private void menuMain() {
        Integer response;

        while (true) {
            System.out.println("\n*** Welcome " + currentPartner.getUsername() + " ***\n");
            System.out.println("1: Search Hotel Room");
            System.out.println("2: View Partner Reservation Details");
            System.out.println("3: View All Partner Reservations");
            System.out.println("4: Logout\n");
            System.out.print("> ");
            response = scanner.nextInt();
            scanner.nextLine();

            switch (response) {
                case 1:
                    doSearchHotelRoomPartnerReservationManager();
                    break;
                case 2:
                    doViewPartnerReservationDetails();
                    break;
                case 3:
                    doViewAllPartnerReservations();
                    break;
                case 4:
                    doLogout();
                    return;
                default:
                    System.out.println("Invalid option, please try again!\n");
            }
        }
    }

    public void doSearchHotelRoomPartnerEmployee() {
        try {
            System.out.print("\n*** Room Search ***\n");
            System.out.print("Enter Check-In Date (yyyy-MM-dd): ");
            String checkInDateStr = scanner.nextLine().trim();
            System.out.print("Enter Check-Out Date (yyyy-MM-dd): ");
            String checkOutDateStr = scanner.nextLine().trim();
            searchHotelRoomFunction(checkInDateStr, checkOutDateStr);
            System.out.println("\nTo book room, please log in to Partner Account!");
        } catch (Exception e) {
            System.out.println("An error occurred while searching for rooms: " + e.getMessage());
        }
    }

    public void doSearchHotelRoomPartnerReservationManager() {
        
        try {
            Integer response;
            String checkInDateStr;
            String checkOutDateStr;
            System.out.print("\n*** Walk-In Room Search ***\n");
            System.out.print("Enter Check-In Date (yyyy-MM-dd): ");
            checkInDateStr = scanner.nextLine().trim();
            System.out.print("Enter Check-Out Date (yyyy-MM-dd): ");
            checkOutDateStr = scanner.nextLine().trim();
            searchHotelRoomFunction(checkInDateStr, checkOutDateStr);
            System.out.println("----------------------------");
            System.out.println("1. Make Booking");
            System.out.println("2. Exit");
            while (true) {
                System.out.print("Choose an option: ");
                try {
                    response = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    switch (response) {
                        case 1:
                            doReserveHotelRoom(checkInDateStr, checkOutDateStr);
                            return; // Exit after making a booking
                        case 2:
                            return; // Exit
                        default:
                            System.out.println("Invalid option. Please enter 1 or 2.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number (1 or 2).");
                    scanner.nextLine(); // Clear the invalid input
                }
            }
        } catch (Exception e) {
            System.out.println("An error occurred while searching for rooms: " + e.getMessage());
        }
    }

    public void doReserveHotelRoom(String checkInDateStr, String checkOutDateStr) {
        System.out.println("\n*** Reserve Hotel Room ***\n");
        java.util.Date checkInDate = null;
        java.util.Date checkOutDate = null;
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            checkInDate = dateFormat.parse(checkInDateStr);
            checkOutDate = dateFormat.parse(checkOutDateStr);
        } catch (ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.out.print("Enter Room IDs to Reserve (comma-separated, e.g., 101,102): ");
        String roomIdStr = scanner.nextLine().trim();
        List<Long> selectedRoomIds = Arrays.stream(roomIdStr.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        try {
            XMLGregorianCalendar checkInXMLDate = convertToXMLGregorianCalendar(checkInDate);
            XMLGregorianCalendar checkOutXMLDate = convertToXMLGregorianCalendar(checkOutDate);
            List<Long> reservationIDs = new ArrayList<>();

            for (Long roomId : selectedRoomIds) {
            Long reservationID = service.getReservationSystemWebServicePort().reserveRooms(currentPartner.getPartnerID(), roomId, checkInXMLDate, checkOutXMLDate);
                reservationIDs.add(reservationID);
            }
            System.out.println("Rooms reserved successfully for " + this.currentPartner.getPartnerName() + " from " + checkInDateStr + " to " + checkOutDateStr);
            for (Long reservationID : reservationIDs) {
                System.out.println("Reservation ID: " + reservationID);
            }
        } catch (Exception e) {
            System.out.println("Error during reservation: " + e.getMessage());
        }
        
    }

    public void searchHotelRoomFunction(String checkInDateStr, String checkOutDateStr) {
        System.out.println("\n*** Search Hotel Room ***\n");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);

        try {
            checkInDateStr = checkInDateStr.trim();
            checkOutDateStr = checkOutDateStr.trim();
            System.out.println("Check-In Date Entered: " + checkInDateStr);
            System.out.println("Check-Out Date Entered: " + checkOutDateStr);
            java.util.Date checkInDate = dateFormat.parse(checkInDateStr);
            java.util.Date checkOutDate = dateFormat.parse(checkOutDateStr);

            // Step 3: Validate the dates
            if (checkInDate.after(checkOutDate)) {
                System.out.println("Check-out date must be after check-in date.");
                return;
            }

            XMLGregorianCalendar checkInXMLDate = convertToXMLGregorianCalendar(checkInDate);
            XMLGregorianCalendar checkOutXMLDate = convertToXMLGregorianCalendar(checkOutDate);

            // Step 4: Search for available rooms
            List<Room> availableRooms = service.getReservationSystemWebServicePort().searchAvailableRooms(checkInXMLDate, checkOutXMLDate);

            if (availableRooms.isEmpty()) {
                System.out.println("No available rooms for the specified dates.");
                return;
            }

            // Step 5: Display available rooms with rates
            System.out.println("\nAvailable Rooms:");
            for (Room room : availableRooms) {
                BigDecimal rate = service.getReservationSystemWebServicePort().calculateRateForRoomType(room.getRoomType(), checkInXMLDate, checkOutXMLDate);
                System.out.printf("Room ID: %s | Room Type: %s | Rate per Night: %s | Total for stay: $%s\n",
                        room.getRoomID(), room.getRoomType().getName(), rate, rate.multiply(BigDecimal.valueOf(daysBetween(checkInXMLDate, checkOutXMLDate))));
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
            System.out.println("Invalid date format. Please enter the date in YYYY-MM-DD format.");
        }
    }

    public XMLGregorianCalendar convertToXMLGregorianCalendar(Date date) {
        try {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public long daysBetween(XMLGregorianCalendar checkInDate, XMLGregorianCalendar checkOutDate) {
        // Convert XMLGregorianCalendar to LocalDate
        LocalDate checkInLocalDate = checkInDate.toGregorianCalendar().toZonedDateTime().toLocalDate();
        LocalDate checkOutLocalDate = checkOutDate.toGregorianCalendar().toZonedDateTime().toLocalDate();

        // Calculate the number of days between the two dates
        return ChronoUnit.DAYS.between(checkInLocalDate, checkOutLocalDate);
    }

    private void doViewPartnerReservationDetails() {
        System.out.println("\n*** View Partner Reservation Details ***\n");
        System.out.print("Enter Reservation ID: ");
        Long response = scanner.nextLong();
        scanner.nextLine(); 
        
        try {
            Reservation r = service.getReservationSystemWebServicePort().viewPartnerReservationDetails(response, currentPartner.getPartnerID());
            System.out.println("*** Reservation ID: " + r.getReservationID() + " ***");
            System.out.println("Room Type: " + r.getRoomType().getName());
            System.out.println("Check-in Date: " + r.getCheckInDate());
            System.out.println("Check-out Date: " + r.getCheckOutDate());
            System.out.println("Status: " + r.getStatus());
            System.out.println("Total Amount: $" + r.getTotalAmount());
        } catch (Exception e) {
            System.out.println("Reservation Not Found!");
        }
    }
    

    private void doViewAllPartnerReservations() {
        System.out.println("\n*** View All Partner Reservation ***\n");
        List<Reservation> reservations = new ArrayList();
        try {
            reservations = service.getReservationSystemWebServicePort().viewAllPartnerReservations(currentPartner.getPartnerID());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if (reservations == null || reservations.isEmpty()) {
        System.out.println("No reservations found for this partner.");
        } else {
            for (Reservation r : reservations) {
                System.out.println("Reservation ID: " + r.getReservationID());
                System.out.println("Room Type: " + r.getRoomType().getName());
                System.out.println("Check-in Date: " + r.getCheckInDate());
                System.out.println("Check-out Date: " + r.getCheckOutDate());
                System.out.println("Status: " + r.getStatus());
                System.out.println("Total Amount: $" + r.getTotalAmount());
                System.out.println("----------------------------");
            }
        }
    }

}





