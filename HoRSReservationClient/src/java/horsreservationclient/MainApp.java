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
import entity.RoomRate;
import entity.RoomType;
import enumType.ReservationStatusEnum;
import enumType.RoomTypeEnum;
import exceptions.GuestNotFoundException;
import exceptions.ReservationNotFoundException;
import exceptions.RoomNotFoundException;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

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

    public MainApp() {
    }

    public MainApp(GuestSessionBeanRemote guestSessionBeanRemote, PartnerSessionBeanRemote partnerSessionBeanRemote, ReservationSessionBeanRemote reservationSessionBeanRemote, RoomRateSessionBeanRemote roomRateSessionBeanRemote, RoomSessionBeanRemote roomSessionBeanRemote, RoomTypeSessionBeanRemote roomTypeSessionBeanRemote) {
        this.guestSessionBeanRemote = guestSessionBeanRemote;
        this.partnerSessionBeanRemote = partnerSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.roomRateSessionBeanRemote = roomRateSessionBeanRemote;
        this.roomSessionBeanRemote = roomSessionBeanRemote;
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
        this.scanner = new Scanner(System.in);
    }

    public void runApp() {
        Integer response = 0;
        while (true) {
            System.out.println("*** Welcome to HoRS Reservation Client ***\n");
            System.out.println("1: Guest Login");
            System.out.println("2: Register as Guest");
            System.out.println("3: Search Hotel Room");
            System.out.println("4: Exit\n");
            System.out.print("> ");
            response = scanner.nextInt();
            scanner.nextLine();

            switch (response) {
                case 1:
                    doLogin();
                    break;
                case 2:
                    doRegisterAsGuest();
                    break;
                case 3:
                    doSearchHotelRoomVisitor();
                    break;
                case 4:
                    System.out.println("Exiting HoRS Reservation Client...");
                    return;
                default:
                    System.out.println("Invalid option, please try again!\n");
            }
        }
    }

    public void doLogin() {
        System.out.print("\n*** HoRS Reservation Client :: Login ***\n");
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

        while (true) {
            System.out.println("*** Welcome " + currentGuest.getUsername() + " ***\n");
            System.out.println("1: Search Hotel Room");
            System.out.println("2: View My Reservation Details");
            System.out.println("3: View All My Reservations");
            System.out.println("4: Logout\n");
            System.out.print("> ");
            response = scanner.nextInt();
            scanner.nextLine();

            switch (response) {
                case 1:
                    doSearchHotelRoomGuest();
                    break;
                case 2:
                    doViewReservationDetails();
                    break;
                case 3:
                    doViewAllReservations();
                    break;
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

        System.out.print("Enter Guest Name: ");
        System.out.flush();
        String guestName = scanner.nextLine().trim();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Phone Number: ");
        String phoneNumber = scanner.nextLine();
        System.out.print("Enter Passport Number: ");
        String passportNumber = scanner.nextLine();
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        Guest guest = guestSessionBeanRemote.registerGuest(new Guest(guestName, email, phoneNumber, passportNumber, username, password));
        System.out.println("Registered Successfully! Guest Username: " + guest.getUsername());
    }

    public void doSearchHotelRoomGuest() {
        try {
            System.out.print("\n*** Walk-In Room Search ***\n");
            System.out.print("Enter Check-In Date (yyyy-MM-dd): ");
            String checkInDateStr = scanner.nextLine().trim();
            System.out.print("Enter Check-Out Date (yyyy-MM-dd): ");
            String checkOutDateStr = scanner.nextLine().trim();
            searchHotelRoomFunction(checkInDateStr, checkOutDateStr);
            Integer response;
            System.out.println("----------------------------");
            System.out.println("1. Make Booking");
            System.out.println("2. Exit");
            System.out.print("> ");
            response = scanner.nextInt();
            scanner.nextLine();
            switch (response) {
                case 1:
                    doReserveHotelRoom(checkInDateStr, checkOutDateStr);
                    break;
                case 2:
                    return;
            }

        } catch (Exception e) {
            System.out.println("An error occurred while searching for rooms: " + e.getMessage());
        }
    }
    public void doSearchHotelRoomVisitor() {
        try {
            System.out.print("\n*** Walk-In Room Search ***\n");
            System.out.print("Enter Check-In Date (yyyy-MM-dd): ");
            String checkInDateStr = scanner.nextLine().trim();
            System.out.print("Enter Check-Out Date (yyyy-MM-dd): ");
            String checkOutDateStr = scanner.nextLine().trim();
            searchHotelRoomFunction(checkInDateStr, checkOutDateStr);
            System.out.println("\nTo book room, please log in or create account!");
        } catch (Exception e) {
            System.out.println("An error occurred while searching for rooms: " + e.getMessage());
        }
    }

    private void doReserveHotelRoom(String checkInDateStr, String checkOutDateStr) {
        java.util.Date checkInDate = null;
        java.util.Date checkOutDate = null;
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            checkInDate = dateFormat.parse(checkInDateStr);
            checkOutDate = dateFormat.parse(checkOutDateStr);
        } catch (ParseException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }
        System.out.println("\n*** Reserve Hotel Room ***\n");
    
        // Step 1: Select Room Type
        List<RoomType> availableRoomTypes = roomTypeSessionBeanRemote.getAllRoomTypes();
        System.out.println("Select Room Type:");
        for (int i = 0; i < availableRoomTypes.size(); i++) {
            System.out.println((i + 1) + ": " + availableRoomTypes.get(i).getName());
        }
        System.out.print("> ");
        int roomTypeSelection = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (roomTypeSelection < 1 || roomTypeSelection > availableRoomTypes.size()) {
            System.out.println("Invalid room type selection.");
            return;
        }

        RoomType selectedRoomType = availableRoomTypes.get(roomTypeSelection - 1);

        // Step 2: Enter the Number of Rooms to Reserve
        System.out.print("Enter the number of rooms to reserve: ");
        int numRooms = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (numRooms < 1) {
            System.out.println("Invalid number of rooms.");
            return;
        }

        try {
            // Step 3: Calculate the Total Amount for Each Room Reservation
            BigDecimal totalAmount = roomRateSessionBeanRemote.calculateRateForRoomType(selectedRoomType, checkInDate, checkOutDate);
            if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("Error: Total amount calculation failed.");
                return;
            }

            List<Long> reservationIDs = new ArrayList<>();

            // Step 4: Create Multiple Reservations (One for Each Room)
            for (int i = 0; i < numRooms; i++) {
                Reservation reservation = new Reservation(checkInDate, checkOutDate, ReservationStatusEnum.RESERVED, totalAmount, currentGuest, selectedRoomType, null, null);
                reservation.setRoomRate(roomRateSessionBeanRemote.getPublishedRateForRoomType(selectedRoomType, checkInDate, checkOutDate));

                Long reservationID = reservationSessionBeanRemote.createReservation(reservation);
                reservationIDs.add(reservationID);
            }

            // Confirmation of Successful Reservations
            System.out.println("Rooms reserved successfully for " + currentGuest.getName() + " from " + checkInDateStr + " to " + checkOutDateStr);
            for (Long reservationID : reservationIDs) {
                System.out.println("Reservation ID: " + reservationID);
            }
        } catch (Exception e) {
            System.out.println("Error during reservation: " + e.getMessage());
        }
    }

    /*
    public void doReserveHotelRoom() {
        System.out.println("\n*** Reserve Hotel Room ***\n");

        // Call searchHotelRoom to get the list of available rooms
        List<Room> availableRooms = searchHotelRoomFunctionWithList();

        // If no rooms are available, return
        if (availableRooms.isEmpty()) {
            System.out.println("No rooms available to reserve.");
            return;
        }

        System.out.print("Would you like to reserve one or more of these rooms? (yes/no): ");
        String response = scanner.nextLine().trim().toLowerCase();
        if (!response.equals("yes")) {
            System.out.println("Reservation canceled.");
            return;
        }

        // Prompt for check-in and check-out dates
        System.out.print("Enter check-in date (YYYY-MM-DD): ");
        String checkInStr = scanner.nextLine();
        Date checkInDate = Date.valueOf(checkInStr);

        System.out.print("Enter check-out date (YYYY-MM-DD): ");
        String checkOutStr = scanner.nextLine();
        Date checkOutDate = Date.valueOf(checkOutStr);

        LocalTime currentTime = LocalTime.now();
        boolean isSameDayCheckIn = checkInDate.equals(Date.valueOf(LocalDate.now()));
        boolean isAfter2AM = currentTime.isAfter(LocalTime.of(2, 0));

        // Allow the guest to reserve multiple rooms
        while (true) {
            System.out.print("Enter the number of the room you wish to reserve (or 0 to finish): ");
            int roomChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (roomChoice == 0) {
                break;
            }
if (roomChoice < 1 || roomChoice > availableRooms.size()) {
                System.out.println("Invalid selection. Please try again.");
                continue;
            }

            Room selectedRoom = availableRooms.get(roomChoice - 1);

            try {
                // Create the reservation
                Long reservationID = reservationSessionBeanRemote.createReservation(
                        currentGuest.getGuestID(), selectedRoom.getRoomID(), checkInDate, checkOutDate, isSameDayCheckIn && isAfter2AM);

                System.out.println("Reservation successful! Your reservation ID is: " + reservationID);

            } catch (Exception e) {
                System.out.println("Error while creating reservation: " + e.getMessage());
            }

            System.out.print("Would you like to reserve another room? (yes/no): ");
            response = scanner.nextLine().trim().toLowerCase();
            if (!response.equals("yes")) {
                break;
            }
        }

        System.out.println("Reservations completed.");
    }
*/
    public void searchHotelRoomFunction(String checkInDateStr, String checkOutDateStr) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            java.util.Date checkInDate = dateFormat.parse(checkInDateStr);
            java.util.Date checkOutDate = dateFormat.parse(checkOutDateStr);

            List<Room> availableRooms = roomSessionBeanRemote.searchAvailableRooms(checkInDate, checkOutDate);

            if (availableRooms.isEmpty()) {
                System.out.println("No rooms available for the selected dates.");
                return;
            }

            // Display available rooms and calculate the reservation amount
            System.out.println("Available Rooms:");
            for (Room room : availableRooms) {
                RoomType roomType = room.getRoomType();
                RoomRate publishedRate = roomRateSessionBeanRemote.getPublishedRateForRoomType(roomType, checkInDate, checkOutDate);

                if (publishedRate != null) {
                    long nights = (checkOutDate.getTime() - checkInDate.getTime()) / (1000 * 60 * 60 * 24);
                    BigDecimal reservationAmount = publishedRate.getRatePerNight().multiply(BigDecimal.valueOf(nights));

                    System.out.println("Room ID: " + room.getRoomID() + ", Type: " + roomType.getName()
                            + ", Rate per Night: " + publishedRate.getRatePerNight()
                            + ", Total for Stay: " + reservationAmount);
                } else {
                    System.out.println("Room ID: " + room.getRoomID() + ", Type: " + roomType.getName() + " - No published rate available.");
                }
            }
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please enter dates in yyyy-MM-dd format.");
        }
    }

    public List<Room> searchHotelRoomFunctionWithList() {
        try {
            System.out.println("\n*** Search Hotel Room ***\n");

            // Prompt for check-in and check-out dates
            System.out.print("Enter check-in date (YYYY-MM-DD): ");
            String checkInStr = scanner.nextLine();
            Date checkInDate = Date.valueOf(checkInStr);

            System.out.print("Enter check-out date (YYYY-MM-DD): ");
            String checkOutStr = scanner.nextLine();
            Date checkOutDate = Date.valueOf(checkOutStr);

            // Validate dates
            if (checkInDate.after(checkOutDate)) {
                System.out.println("Error: Check-out date must be after check-in date.");
                return new ArrayList<>();
            }

            // Retrieve available rooms
            List<Room> availableRooms = roomSessionBeanRemote.searchAvailableRooms(checkInDate, checkOutDate);
            if (availableRooms.isEmpty()) {
                System.out.println("No available rooms for the specified dates.");
                return new ArrayList<>();
            }
            // Display available rooms
            System.out.println("\nAvailable Rooms:");
            for (int i = 0; i < availableRooms.size(); i++) {
                Room room = availableRooms.get(i);
                BigDecimal rate = roomRateSessionBeanRemote.calculateRateForRoomType(room.getRoomType(), checkInDate, checkOutDate);
                System.out.printf("%d: Room Type: %s | Rate per Night: $%s | Total for stay: $%s\n",
                        i + 1, room.getRoomType().getName(), rate,
                        rate.multiply(BigDecimal.valueOf(daysBetween(checkInDate, checkOutDate))));
            }

            return availableRooms;

        } catch (Exception e) {
            System.out.println("An error occurred while searching for rooms: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private long daysBetween(Date start, Date end) {
        return ChronoUnit.DAYS.between(start.toInstant(), end.toInstant());
    }

    private void doViewReservationDetails() {
        System.out.println("\n*** View Reservation Details ***\n");
        System.out.print("Enter Reservation ID: ");
        Long response = scanner.nextLong();
        scanner.nextLine(); // Consume newline

        try {
            Reservation r = reservationSessionBeanRemote.viewReservationByGuest(response, currentGuest.getGuestID());
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

    private void doViewAllReservations() {
        List<Reservation> reservations = reservationSessionBeanRemote.viewAllReservations(currentGuest.getGuestID());
        if (reservations.isEmpty()) {
            System.out.println("No Reservations Found");
        } else {
            for (Reservation r : reservations) {
                RoomTypeEnum roomTypeName = r.getRoomType().getName(); // Access the room type to initialize it
                RoomRate roomRate = r.getRoomRate();
                
                System.out.println("Reservation ID: " + r.getReservationID());
                System.out.println("Room Type: " + roomTypeName);
                System.out.println("Check-in Date: " + r.getCheckInDate());
                System.out.println("Check-out Date: " + r.getCheckOutDate());
                System.out.println("Status: " + r.getStatus());
                System.out.println("Total Amount: $" + r.getTotalAmount());
                System.out.println("----------------------------");
            }
        }
    }

}