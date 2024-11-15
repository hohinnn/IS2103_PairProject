/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsmanagementclient;

import ejb.session.singleton.RoomAllocationSessionBeanRemote;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Employee;
import entity.Partner;
import entity.Reservation;
import entity.Room;
import entity.RoomAllocation;
import entity.RoomRate;
import entity.RoomType;
import enumType.EmployeeAccessRightEnum;
import enumType.RoomAvailabilityEnum;
import enumType.RoomRateStatusEnum;
import enumType.RoomRateTypeEnum;
import enumType.RoomTypeEnum;
import exceptions.EmployeeNotFoundException;
import exceptions.ReservationNotFoundException;
import exceptions.RoomNotFoundException;
import exceptions.RoomRateInUseException;
import exceptions.RoomRateNotFoundException;
import exceptions.RoomTypeNotFoundException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.EJB;

/**
 *
 * @author hohin
 */
public class MainApp {
    

    @EJB
    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private GuestSessionBeanRemote guestSessionBeanRemote;
    private PartnerSessionBeanRemote partnerSessionBeanRemote;
    private ReservationSessionBeanRemote reservationSessionBeanRemote;
    private RoomRateSessionBeanRemote roomRateSessionBeanRemote;
    private RoomSessionBeanRemote roomSessionBeanRemote;
    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;
    private RoomAllocationSessionBeanRemote roomAllocationSessionBeanRemote;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    
    private Employee currentEmployee = null;
    private Scanner scanner;

    public MainApp() {}

    public MainApp(EmployeeSessionBeanRemote employeeSessionBeanRemote, GuestSessionBeanRemote guestSessionBeanRemote, PartnerSessionBeanRemote partnerSessionBeanRemote, ReservationSessionBeanRemote reservationSessionBeanRemote, RoomRateSessionBeanRemote roomRateSessionBeanRemote, RoomSessionBeanRemote roomSessionBeanRemote, RoomTypeSessionBeanRemote roomTypeSessionBeanRemote, RoomAllocationSessionBeanRemote roomAllocationSessionBeanRemote) {
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.guestSessionBeanRemote = guestSessionBeanRemote;
        this.partnerSessionBeanRemote = partnerSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.roomRateSessionBeanRemote = roomRateSessionBeanRemote;
        this.roomSessionBeanRemote = roomSessionBeanRemote;
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
        this.roomAllocationSessionBeanRemote = roomAllocationSessionBeanRemote;
        this.scanner = new Scanner(System.in);
    }
    
    public void runApp() {
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Welcome to HoRS Management Client ***\n");
            System.out.println("1: Login");
            System.out.println("2: Exit\n");
            response = 0;
            
            while (response < 1 || response > 6)
            {
                System.out.print("> ");

                response = scanner.nextInt();
                scanner.nextLine(); 

                if(response == 1)
                {
                    doLogin();
                }
                else if(response == 2)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            return;
        }
        
    }

    private void doLogin() {
        System.out.println("\n*** HoRS Management Client :: Login ***\n");
        System.out.print("Enter username> ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        String password = scanner.nextLine().trim();

        try {
            currentEmployee = employeeSessionBeanRemote.loginEmployee(username, password);
            if (currentEmployee != null) {
                System.out.println("Login successful! Welcome " + currentEmployee.getUsername() + ".\n");
                EmployeeAccessRightEnum accessRight = currentEmployee.getAccessRight();
                menuMain(accessRight);
            } else {
                System.out.println("Invalid login credentials.");
            }
        } catch (EmployeeNotFoundException e) {
            System.out.println("Invalid login: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();  // Catch any unexpected exceptions and print the stack trace for debugging
        }
    }

    private void doLogout() {
        currentEmployee = null;
        System.out.println("You have logged out successfully.\n");
    }
    
     private void menuMain(EmployeeAccessRightEnum accessRight) {
        Integer response;

        while (true) {
            if (accessRight == EmployeeAccessRightEnum.SYSTEM_ADMIN) {
                displayMenuOptionsSystemAdmin();
            } else if (accessRight == EmployeeAccessRightEnum.OPERATION_MANAGER) {
                displayMenuOptionsOperationManager();
            } else if (accessRight == EmployeeAccessRightEnum.SALES_MANAGER) {
                displayMenuOptionsSalesManager();
            } else {
                displayMenuOptionsGuestRelationOfficer();
            }
            System.out.print("Choose an option: ");
            response = scanner.nextInt();
            scanner.nextLine();

            if (response == 1) {
                doLogout();
                return;
            } else {
                executeMenuOption(response, accessRight);
            }
        }
    }

    private void displayMenuOptionsSystemAdmin() {
        System.out.println("*** HoRS Management Client -- System Administrator ***\n");
        System.out.println("1: Logout");
        System.out.println("2: Create New Employee");
        System.out.println("3: View All Employees");
        System.out.println("4: Create New Partner");
        System.out.println("5: View All Partners");
    }
    
    private void displayMenuOptionsOperationManager() {
        System.out.println("\n*** HoRS Management Client -- Operation Manager ***\n");
        System.out.println("1: Logout");
        System.out.println("2: Create New Room Type");
        System.out.println("3: View Room Type Details");
        System.out.println("4: View All Room Types");
        System.out.println("5: Create New Room");
        System.out.println("6: Update Room");
        System.out.println("7: Delete Room");
        System.out.println("8: View All Rooms");
        System.out.println("9: View Room Allocation Exception Report");
        System.out.println("10: Trigger Allocation (for Testing)");
    }
    
    private void displayMenuOptionsSalesManager() {
        System.out.println("\n*** HoRS Management Client -- Sales Manager ***\n");
        System.out.println("1: Logout");
        System.out.println("2: Create New Room Rate");
        System.out.println("3: View Room Rate Details");
        System.out.println("4: View All Room Rates");
    }
    
    private void displayMenuOptionsGuestRelationOfficer() {
        System.out.println("\n*** HoRS Management Client -- Guest Relation Officer ***\n");
        System.out.println("1: Logout");
        System.out.println("2: Walk-in Reserve Room");
        System.out.println("3: Check-in Guest");
        System.out.println("4: Check-out Guest");
    }

    private void executeMenuOption(int option, EmployeeAccessRightEnum accessRight) {
        if (accessRight == EmployeeAccessRightEnum.SYSTEM_ADMIN) {
            switch (option) {
            case 2:
                createNewEmployee();
                break;
            case 3:
                viewAllEmployees();
                break;
            case 4:
                createNewPartner();
                break;
            case 5:
                viewAllPartners();
                break;
            default:
                System.out.println("Invalid option, please try again.");
            }
        } else if (accessRight == EmployeeAccessRightEnum.OPERATION_MANAGER) {
            switch (option) {
            case 2:
                createNewRoomType();
                break;
            case 3:
                viewRoomTypeDetails();
                break;
            case 4:
                viewAllRoomTypes();
                break;
            case 5:
                createNewRoom();
                break;
            case 6:
                updateRoom();
                break;
            case 7:
                deleteRoom();
                break;
            case 8:
                viewAllRooms();
                break;
            case 9:
                viewRoomAllocationExceptionReport();
                break;
            case 10:
                allocateRoomsForDate();
                break;
            default:
                System.out.println("Invalid option, please try again.");
            }
        } else if (accessRight == EmployeeAccessRightEnum.SALES_MANAGER) {
            switch (option) {
            case 2:
                createNewRoomRate();
                break;
            case 3:
                viewRoomRateDetails();
                break;
            case 4:
                viewAllRoomRates();
                break;
            default:
                System.out.println("Invalid option, please try again.");
            }
        } else if (accessRight == EmployeeAccessRightEnum.GUEST_RELATION_OFFICER ) {
            switch (option) {
            case 2:
                walkInSearchRoom();
                break;
            case 3:
                checkInGuest();
                break;
            case 4:
                checkOutGuest();
                break;
            default:
                System.out.println("Invalid option, please try again.");
            }  
        } 
    }

    // Methods for each menu option
    private void createNewEmployee() {
        System.out.println("*** New Employee *** ");
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();
        
        // Choose access right based on user input
        System.out.println();
        System.out.println("***Select Employee Access Right:***");
        System.out.println("1. System Administrator");
        System.out.println("2. Operation Manager");
        System.out.println("3. Sales Manager");
        System.out.println("4. Guest Relation Officer");
        System.out.println("5. Back");
        System.out.print(">");
        int accessRightChoice = scanner.nextInt();
        scanner.nextLine();

        try {
            EmployeeAccessRightEnum accessRight = null;
            switch (accessRightChoice) {
                case 1:
                    accessRight = EmployeeAccessRightEnum.SYSTEM_ADMIN;
                    break;
                case 2:
                    accessRight = EmployeeAccessRightEnum.OPERATION_MANAGER;
                    break;
                case 3:
                    accessRight = EmployeeAccessRightEnum.SALES_MANAGER;
                    break;
                case 4:
                    accessRight = EmployeeAccessRightEnum.GUEST_RELATION_OFFICER;
                    break;
                case 5:
                    return; // Go back to the main menu or previous step
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }

            Employee employee = employeeSessionBeanRemote.createEmployee(new Employee(username, password, accessRight));
            System.out.println("New employee created successfully! Employee ID: " + employee.getEmployeeID());
        } catch (Exception e) {
            System.out.println("Error creating employee: " + e.getMessage());
        }
    }

    private void viewAllEmployees() {
        System.out.println("\n*** Employee List ***");
        List<Employee> employees = employeeSessionBeanRemote.viewAllEmployees();
        for (Employee e : employees) {
            System.out.println("ID: "+ e.getEmployeeID()+ " | Name: " + e.getUsername() + "| Role: " + e.getAccessRight());
        }
    }

    private void createNewPartner() {
        System.out.println("\n*** New Partner *** ");
        System.out.print("Enter Partner Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        try {
            Partner partner = partnerSessionBeanRemote.createPartner(new Partner(name, username, password));
            System.out.println("New Partner created successfully! Partner ID: " + partner.getPartnerID() + "\nPartner Name: " + partner.getPartnerName());
        } catch (Exception e) {
            System.out.println("Error creating Partner: " + e.getMessage());
        }
    }

    private void viewAllPartners() {
        System.out.println("\n*** Partner List ***");
        List<Partner> partners = partnerSessionBeanRemote.viewAllPartners();
        for (Partner p : partners) {
            System.out.println(p.getPartnerName());
            System.out.println("ID: "+ p.getPartnerID()+ " | Partner Name: " + p.getPartnerName() + "| Username: " + p.getUsername());
        }
    }

    private void createNewRoomType() {
        System.out.println("\n*** New Room Type *** ");
        System.out.print("Enter Room Type Option "
                + "\n1.Deluxe "
                + "\n2.Premier "
                + "\n3.Family "
                + "\n4.Junior Suite "
                + "\n5.Grand Suite "
                + "\n>");
        int name = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter Description: ");
        String description = scanner.nextLine();
        System.out.print("Enter Size: ");
        double size = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Bed Type: ");
        String bedType = scanner.nextLine();
        System.out.print("Enter Capacity: ");
        int capacity = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter Amenities: ");
        String amenities = scanner.nextLine();
        
        RoomTypeEnum rtEnum = null;
        
        switch(name) {
            case 1:
                rtEnum = RoomTypeEnum.DELUXE;
                break;
            case 2:
                rtEnum = RoomTypeEnum.PREMIER;
                break;
            case 3:
                rtEnum = RoomTypeEnum.FAMILY;
                break;
            case 4:
                rtEnum = RoomTypeEnum.JUNIOR_SUITE;
                break;
            case 5:
                rtEnum= RoomTypeEnum.GRAND_SUITE;
                break;
            default:
                System.out.println("Error: Invalid Room Type Option"); //update message
                return;
        }
        
        try {
            RoomType rt = roomTypeSessionBeanRemote.createRoomType(new RoomType(rtEnum, description, size, bedType, capacity, amenities));
            System.out.println("New Room Type created successfully! Room Type ID: " + rt.getRoomTypeID() + "\nRoom Type Name: " + rt.getName());
        } catch (Exception e) {
            System.out.println("Error creating Room Rype: " + e.getMessage());
        }
        
    }

    private void viewRoomTypeDetails() {
        System.out.println("\n*** View Room Type Details ***");
        System.out.println("Enter Room Type ID:");
        long roomTypeID = scanner.nextLong();
        try {
            while (true) {
                RoomType roomType = roomTypeSessionBeanRemote.viewRoomType(roomTypeID);
                if (roomType == null) {
                    System.out.println("Room type not found. Try again.");
                    return;
                }
                
                System.out.println("\n*** Room Type Details ***");
                System.out.println("Name: " + roomType.getName());
                System.out.println("Description: " + roomType.getDescription());
                System.out.println("Size: " + roomType.getSize());
                System.out.println("Bed Type: " + roomType.getBedType());
                System.out.println("Capacity: " + roomType.getCapacity());
                System.out.println("Amenities: " + roomType.getAmenities());
                
                System.out.println();
                
                System.out.println("1. Update Room Type");
                System.out.println("2. Delete Room Type");
                System.out.println("3. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); 

                switch (choice) {
                    case 1:
                        updateRoomType(roomType);
                        break;
                    case 2:
                        deleteRoomType(roomType);
                        return; // exit method after deletion
                    case 3:
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        } catch (RoomTypeNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
        System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void updateRoomType(RoomType roomType) {
        System.out.print("\n*** Update Room Type ***\n");
        System.out.print("Room Type ID: " + roomType.getRoomTypeID() + "\n");
        
        try {
            // RoomTypeEnum selection
            System.out.println("Select new Room Type:");
            System.out.println("1. DELUXE");
            System.out.println("2. PREMIER");
            System.out.println("3. FAMILY");
            System.out.println("4. JUNIOR SUITE");
            System.out.println("5. GRAND SUITE");
            System.out.print("Enter option: ");
            int nameOption = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            RoomTypeEnum name;
            switch (nameOption) {
              case 1:
                name = RoomTypeEnum.DELUXE;
                break;
              case 2:
                name = RoomTypeEnum.PREMIER;
                break;
              case 3:
                name = RoomTypeEnum.FAMILY;
                break;
              case 4:
                name = RoomTypeEnum.JUNIOR_SUITE;
                break;
              case 5:
                name = RoomTypeEnum.GRAND_SUITE;
                break;
              default:
                throw new IllegalArgumentException("Invalid Room Type selection.");
            }

            // Get and update description
            System.out.print("Enter new Description (current: " + roomType.getDescription() + "): ");
            String description = scanner.nextLine().trim();
            description = description.isEmpty() ? roomType.getDescription() : description;

            // Get and update size
            System.out.print("Enter new Size (current: " + roomType.getSize() + "): ");
            double size = scanner.nextDouble();
            size = size <= 0 ? roomType.getSize() : size;
            scanner.nextLine(); // Consume newline

            // Get and update bed type
            System.out.print("Enter new Bed Type (current: " + roomType.getBedType() + "): ");
            String bedType = scanner.nextLine().trim();
            bedType = bedType.isEmpty() ? roomType.getBedType() : bedType;

            // Get and update capacity
            System.out.print("Enter new Capacity (current: " + roomType.getCapacity() + "): ");
            int capacity = scanner.nextInt();
            capacity = capacity <= 0 ? roomType.getCapacity() : capacity;
            scanner.nextLine(); // Consume newline

            // Get and update amenities
            System.out.print("Enter new Amenities (current: " + roomType.getAmenities() + "): ");
            String amenities = scanner.nextLine().trim();
            amenities = amenities.isEmpty() ? roomType.getAmenities() : amenities;

            // Call session bean to update the Room Type
            roomTypeSessionBeanRemote.updateRoomType(roomType.getRoomTypeID(), name, description, size, bedType, capacity, amenities);
            System.out.println("Room Type updated successfully!");

  
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteRoomType(RoomType roomType) {
        System.out.print("\n*** Delete Room Type ***\n");
        System.out.print("Room Type ID: " + roomType.getRoomTypeID() + "\n");

        try {
            boolean deleted = roomTypeSessionBeanRemote.deleteRoomType(roomType.getRoomTypeID());
            if (deleted) {
                System.out.println("Room Type deleted successfully.");
            } else {
                System.out.println("Room Type is in use, marked as disabled.");
            }
        } catch (RoomTypeNotFoundException e) {
            System.out.println("Error deleting Room Type: " + e.getMessage());
        }
    }

    private void viewAllRoomTypes() {
        System.out.println("\n*** Room Type List ***");
        List<RoomType> roomTypes = roomTypeSessionBeanRemote.getAllRoomTypes();
        for (RoomType r : roomTypes) {
            System.out.println("ID: "+ r.getRoomTypeID() + " | Name: " + r.getName());
        }
    }

    private void createNewRoom() {
        try {
            System.out.println("\n*** Create New Room ***");
            
            // Get floor number and room sequence from user
            System.out.print("Enter Room Number (e.g., 0102): ");
            String roomNumber = scanner.nextLine();
            
            // Validate input format
            if (roomNumber.length() != 4 || !roomNumber.matches("\\d{4}")) {
                System.out.println("Invalid room number format. Please enter in the format 'FloorRoom' (e.g., 0102).");
                return;
            }
            
            int floorNumber = Integer.parseInt(roomNumber.substring(0, 2));
            int roomSequence = Integer.parseInt(roomNumber.substring(2, 4));
            
            // RoomType selection
            System.out.println("Select Room Type:");
            List<RoomType> roomTypes = roomTypeSessionBeanRemote.getAllRoomTypes();
            for (RoomType roomType : roomTypes) {
                System.out.println(roomType.getRoomTypeID() + ": " + roomType.getName());
            }
            
            System.out.print("Enter Room Type ID: ");
            long roomTypeId = scanner.nextLong();
            scanner.nextLine(); // Consume newline
            
            // Fetch the selected room type
            RoomType roomType = roomTypeSessionBeanRemote.viewRoomType(roomTypeId);
            if (roomType == null) {
                System.out.println("Room Type not found.");
                return;
            }
            
            // Create a new room
            Room room = new Room(floorNumber, roomSequence, RoomAvailabilityEnum.AVAILABLE, roomType);
            
            // Persist the new room
            try {
                roomSessionBeanRemote.createRoom(room); // Assuming you have a method to save the room
                System.out.println("New room created successfully! Room Number: " + roomNumber);
            } catch (Exception e) {
                System.out.println("Error creating room: " + e.getMessage());
            }
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("RoomType Error:" + ex.getMessage());
        }
    }
    
    private void updateRoom() {
        System.out.println("\n*** Update Room ***");
        System.out.print("Enter Room Number (e.g., 0102): ");
        String roomNumber = scanner.nextLine();

        try {
            if (roomNumber.isEmpty() || !roomNumber.matches("\\d{4}")) {
                System.out.println("Invalid room number format. Please enter in the format 'Floor+Room' (e.g., 0102).");
                return;
            }
            Room room = roomSessionBeanRemote.getRoomByNumber(roomNumber);
            
            System.out.println("Enter new Room Status: (current: " + room.getStatus()+")");
            System.out.println("1. AVAILABLE");
            System.out.println("2. OCCUPIED");
            
            int intStatus = -1;
            while (intStatus < 1 || intStatus > 2) {
                intStatus = scanner.nextInt();
                scanner.nextLine(); 
                
                if (intStatus < 1 || intStatus > 2) {
                    System.out.println("Invalid option. Please try again.");
                }
            }

            RoomAvailabilityEnum newStatus = (intStatus == 1) 
                ? RoomAvailabilityEnum.AVAILABLE 
                : RoomAvailabilityEnum.OCCUPIED;
        
            roomSessionBeanRemote.updateRoomStatus(roomNumber, newStatus);
            System.out.println("Room updated successfully!");
            System.out.println("Room " + roomNumber + " New Status: " + newStatus);

        } catch (RoomNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid status provided: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }
    
    private void deleteRoom() {
        System.out.print("\n*** Delete Room ***\n");
        System.out.print("Enter Room Number (e.g., 0102): ");
        String roomNumber = scanner.nextLine();

        try {
            Room room = roomSessionBeanRemote.getRoomByNumber(roomNumber);

            // Delete the room by its formatted number
            roomSessionBeanRemote.deleteRoom(roomNumber);
            
            if (room.getStatus() == RoomAvailabilityEnum.AVAILABLE) {
                System.out.println(room.getStatus() + " Room deleted successfully.");
            } else {
                System.out.println(room.getStatus() +" Room marked as disabled successfully.");
            }
            
        } catch (RoomNotFoundException e) {
            System.out.println("Error deleting room: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void viewAllRooms() {
        System.out.println("\n*** Room List ***");
        List<Room> rooms = roomSessionBeanRemote.getAllRooms();
        for (Room r : rooms) {
            System.out.println("Room Number: "+ r.getFormattedRoomSequence() + " | Status: " + r.getStatus());
        }
    }

    //NOT DONE
    private void viewRoomAllocationExceptionReport() {
        System.out.println("\n--- Room Allocation Exception Report ---");

        // Retrieve all room allocation exceptions
        List<RoomAllocation> exceptions = roomAllocationSessionBeanRemote.retrieveRoomAllocationExceptions();

        if (exceptions.isEmpty()) {
            System.out.println("No room allocation exceptions found.");
            return;
        }
        
        int type1Count = 0;
        int type2Count = 0;

        // Display exceptions by type
        for (RoomAllocation exception : exceptions) {
            String message = exception.getAllocationExceptionReport();
            System.out.println("Reservation ID: " + exception.getReservation().getReservationID());
            System.out.println("Exception Date: " + exception.getAllocationDate());

            if (message.contains("Type 1")) {
                System.out.println("Type 1 Exception: " + message);
                type1Count++;
            } else if (message.contains("Type 2")) {
                System.out.println("Type 2 Exception: " + message);
                type2Count++;
            }

            System.out.println("-------------------------------------------------");
        }
        
        System.out.println("Summary:");
        System.out.println("Total Type 1 Exceptions (upgraded room allocated): " + type1Count);
        System.out.println("Total Type 2 Exceptions (no room available): " + type2Count);
    }

    private void createNewRoomRate() {
        try {
            System.out.print("\n*** New Room Rate ***\n");
                System.out.print("Enter Rate Name: ");
                String name = scanner.nextLine().trim();
                
                //System.out.print("Enter Room Type ID: ");
                //long roomTypeId = scanner.nextLong();
                //scanner.nextLine();
                
                System.out.println("Choose Rate Type (1. Published, 2. Normal, 3. Peak, 4. Promotion): ");
                int rateTypeOption = scanner.nextInt();
                scanner.nextLine();
                
                RoomRateTypeEnum rateType;
                switch (rateTypeOption) {
                    case 1:
                        rateType = RoomRateTypeEnum.PUBLISHED;
                        break;
                    case 2:
                        rateType = RoomRateTypeEnum.NORMAL;
                        break;
                    case 3:
                        rateType = RoomRateTypeEnum.PEAK;
                        break;
                    case 4:
                        rateType = RoomRateTypeEnum.PROMOTION;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid Rate Type selection.");
                }
                
                System.out.print("Enter Rate per Night: ");
                BigDecimal ratePerNight = scanner.nextBigDecimal();
                scanner.nextLine(); // Consume newline
                
                Date startDate = null;
                Date endDate = null;
                
                // Only prompt for start and end dates if rate type is Peak or Promotion
                if (rateType == RoomRateTypeEnum.PEAK || rateType == RoomRateTypeEnum.PROMOTION) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    
                    try {
                        System.out.print("Enter Start Date (yyyy-MM-dd): ");
                        String startDateStr = scanner.nextLine().trim();
                        startDate = dateFormat.parse(startDateStr);
                        
                        System.out.print("Enter End Date (yyyy-MM-dd): ");
                        String endDateStr = scanner.nextLine().trim();
                        endDate = dateFormat.parse(endDateStr);
                    } catch (ParseException e) {
                        System.out.println("Invalid date format. Please enter dates in yyyy-MM-dd format.");
                        return;
                    }
                }
                
                // Prompt for Room Type
                System.out.print("Enter Room Type ID: ");
                long roomTypeId = scanner.nextLong();
                scanner.nextLine();
                
                RoomType roomType = roomTypeSessionBeanRemote.viewRoomType(roomTypeId);
                if (roomType == null) {
                    System.out.println("Room Type not found. Please create it first.");
                    return;
                }
                
                try {
                    RoomRate roomRate = new RoomRate(name, rateType, ratePerNight, startDate, endDate);
                    roomRate.setRoomType(roomType); // Set the room type
                    roomRateSessionBeanRemote.createRoomRate(roomRate);
                    System.out.println("Room Rate created successfully!");
                } catch (Exception e) {
                    System.out.println("Error creating Room Rate: " + e.getMessage());
                }
            } catch (RoomTypeNotFoundException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }


    private void viewRoomRateDetails() {
        while (true) {
            System.out.println("\n*** Room Rate Details ***");
            //show room details here
            System.out.print("Enter Room Rate ID: ");
            long rateId = scanner.nextLong();
            scanner.nextLine(); // Consume newline

            try {
                RoomRate roomRate = roomRateSessionBeanRemote.getRoomRate(rateId);

                System.out.println("ID: " + roomRate.getRateID());
                System.out.println("Name: " + roomRate.getName());
                System.out.println("Rate Type: " + roomRate.getRateType());
                System.out.println("Rate per Night: " + roomRate.getRatePerNight());
                System.out.println("Start Date: " + (roomRate.getStartDate() != null ? roomRate.getStartDate(): "N/A") );
                System.out.println("End Date: " + (roomRate.getEndDate()!= null ? roomRate.getEndDate(): "N/A") );
                System.out.println("Room Type: " + (roomRate.getRoomType() != null ? roomRate.getRoomType().getName() : "N/A"));
                System.out.println("Room Status: " + roomRate.getStatus());

            } catch (RoomRateNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
            
            System.out.println();
            System.out.println("1. Update Room Rate");
            System.out.println("2. Delete Room Rate");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1:
                    updateRoomRate(rateId);
                    break;
                case 2:
                    deleteRoomRate(rateId);
                    return;
                case 3:
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void updateRoomRate(Long roomRateId) {
        System.out.print("\n*** Update Room Rate ***\n");

        try {
            RoomRate roomRate = roomRateSessionBeanRemote.getRoomRate(roomRateId);

            System.out.print("Enter new Rate Name (current: " + roomRate.getName() + "): ");
            String name = scanner.nextLine().trim();
            name = name.isEmpty() ? roomRate.getName() : name;

            System.out.println("Select new Rate Type (current: " + roomRate.getRateType() + "):");
            System.out.println("1. Published");
            System.out.println("2. Normal");
            System.out.println("3. Peak");
            System.out.println("4. Promotion");
            System.out.print("Enter option: ");
            int rateTypeOption = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            RoomRateTypeEnum rateType;
            switch (rateTypeOption) {
                case 1:
                    rateType = RoomRateTypeEnum.PUBLISHED;
                    break;
                case 2:
                    rateType = RoomRateTypeEnum.NORMAL;
                    break;
                case 3:
                    rateType = RoomRateTypeEnum.PEAK;
                    break;
                case 4:
                    rateType = RoomRateTypeEnum.PROMOTION;
                    break;
                default:
                    rateType = roomRate.getRateType();
            }

            System.out.print("Enter new Rate per Night (current: " + roomRate.getRatePerNight() + "): ");
            BigDecimal ratePerNight = scanner.nextBigDecimal();
            ratePerNight = ratePerNight.compareTo(BigDecimal.ZERO) > 0 ? ratePerNight : roomRate.getRatePerNight();
            scanner.nextLine();

            Date startDate = null;
            Date endDate = null;

            if (rateType == RoomRateTypeEnum.PEAK || rateType == RoomRateTypeEnum.PROMOTION) {
                try {
                    System.out.print("Enter Start Date (yyyy-MM-dd, current: " + roomRate.getStartDate() + "): ");
                    String startDateStr = scanner.nextLine().trim();
                    startDate = startDateStr.isEmpty() ? roomRate.getStartDate() : dateFormat.parse(startDateStr);

                    System.out.print("Enter End Date (yyyy-MM-dd, current: " + roomRate.getEndDate() + "): ");
                    String endDateStr = scanner.nextLine().trim();
                    endDate = endDateStr.isEmpty() ? roomRate.getEndDate() : dateFormat.parse(endDateStr);
                } catch (ParseException e) {
                    System.out.println("Invalid date format. Please enter dates in yyyy-MM-dd format.");
                    return;
                }
            }
            
            System.out.print("Enter new status (1. ACTIVE, 2. DISABLED): ");
            int statusOption = scanner.nextInt();
            RoomRateStatusEnum status = (statusOption == 2) ? RoomRateStatusEnum.DISABLED : RoomRateStatusEnum.ACTIVE;
            
            roomRateSessionBeanRemote.updateRoomRate(roomRateId, name, rateType, status, ratePerNight, startDate, endDate);            System.out.println("Room Rate updated successfully!");
        } catch (Exception e) {
            System.out.println("Error updating Room Rate: " + e.getMessage());
        }
    }


    // Deletes a room rate if it’s not associated with any reservations; otherwise, marks it as disabled.
    private void deleteRoomRate(Long roomRateId) {
        System.out.print("\n*** Delete Room Rate ***\n");

        try {
            boolean isDeleted = roomRateSessionBeanRemote.deleteRoomRate(roomRateId);
            if (isDeleted) {
                System.out.println("Room Rate deleted successfully.");
            } else {
                System.out.println("Room Rate marked as disabled successfully.");
            }
        } catch (RoomRateInUseException e) {
            System.out.println("Error deleting Room Rate: " + e.getMessage());
        } catch (RoomRateNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    public void allocateRoomsForDate() {
        System.out.println("Enter check-in date (yyyy-MM-dd) for room allocation or press Enter for today's date:");
        String input = scanner.nextLine();
        Date date;

        try {
            date = input.isEmpty() ? new Date() : dateFormat.parse(input);
            roomAllocationSessionBeanRemote.allocateRoomsForDate(date);
            System.out.println("Room allocation completed for " + dateFormat.format(date));
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please enter dates in yyyy-MM-dd format.");
        }catch (IllegalStateException e) {
            // Handle the specific exception thrown from allocateRoomForReservation
            System.out.println("Error during room allocation: " + e.getMessage());
        } catch (Exception e) {
            // Handle any other exceptions
            System.out.println("An error has occurred: " + e.getMessage());
        }
    }

    private void viewAllRoomRates() {
        System.out.println("\n*** Room Rates List ***");
        List<RoomRate> roomRates = roomRateSessionBeanRemote.getAllRoomRates();
        for (RoomRate r : roomRates) {
            System.out.println("ID: "+ r.getRateID() + " | Name: " + r.getName() +  " | RateType: " + r.getRateType() + " | RoomType: " + r.getRoomType().getName());
        }
    }
    
    private void walkInSearchRoom() {
        System.out.print("\n*** Walk-In Room Search ***\n");
        System.out.print("Enter Check-In Date (yyyy-MM-dd): ");
        String checkInDateStr = scanner.nextLine().trim();
        System.out.print("Enter Check-Out Date (yyyy-MM-dd): ");
        String checkOutDateStr = scanner.nextLine().trim();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date checkInDate = dateFormat.parse(checkInDateStr);
            Date checkOutDate = dateFormat.parse(checkOutDateStr);

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
                
                    System.out.println("Room ID: " + room.getRoomID() + ", Type: " + roomType.getName() +
                                   ", Rate per Night: " + publishedRate.getRatePerNight() +
                                   ", Total for Stay: " + reservationAmount);
                } else {
                    System.out.println("Room ID: " + room.getRoomID() + ", Type: " + roomType.getName() + " - No published rate available.");
                }
            }

            // Prompt to reserve rooms
            System.out.print("Enter Room IDs to Reserve (comma-separated, e.g., 101,102): ");
            String roomIdStr = scanner.nextLine().trim();
            List<Long> selectedRoomIds = Arrays.stream(roomIdStr.split(","))
                                               .map(Long::parseLong)
                                               .collect(Collectors.toList());

            walkInReserveRoom(selectedRoomIds, checkInDate, checkOutDate);
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please enter dates in yyyy-MM-dd format.");
        }
    }


    //Reserve room(s) shown in search
    private void walkInReserveRoom(List<Long> roomIds, Date checkInDate, Date checkOutDate) {
        System.out.print("Enter Guest Name: ");
        String guestName = scanner.nextLine().trim();
        System.out.print("Enter Contact Number: ");
        String contactNumber = scanner.nextLine().trim();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter Passport Number: ");
        String passportNumber = scanner.nextLine().trim();

    try {
        List<Room> rooms = new ArrayList<>();
        for (Long roomId : roomIds) {
            rooms.add(roomSessionBeanRemote.getRoomById(roomId));  
        }

        List<Reservation> reservations = reservationSessionBeanRemote.walkInReserveRooms(guestName, contactNumber, email, passportNumber, checkInDate, checkOutDate, rooms);        
        System.out.println("Rooms reserved successfully for " + guestName + " from " + checkInDate + " to " + checkOutDate);
        for (Reservation reservation : reservations) {
            System.out.println("Reservation ID: " + reservation.getReservationID() + ", Room: " + reservation.getRoom().getFormattedRoomSequence() + ", Status: " + reservation.getStatus());
        }
    } catch (RoomNotFoundException e) {
        System.out.println("Error during reservation: " + e.getMessage());
    } catch (Exception e) {
        System.out.println("Error during reservation: " + e.getMessage());
    }
}

    private void checkInGuest() {
        System.out.print("\n*** Check-In Guest ***\n");
        System.out.print("Enter Reservation ID to Check-In: ");
        long reservationId = scanner.nextLong();
        scanner.nextLine(); // Consume newline

        try {
            reservationSessionBeanRemote.checkInGuest(reservationId);
            System.out.println("Guest checked in successfully for reservation ID: " + reservationId);
        } catch (ReservationNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Cannot check-in: " + e.getMessage());
        }
    }

    
    private void checkOutGuest() {
        System.out.print("\n*** Check-Out Guest ***\n");
        System.out.print("Enter Reservation ID to Check-Out: ");
        long reservationId = scanner.nextLong();
        scanner.nextLine(); // Consume newline

        try {
            reservationSessionBeanRemote.checkOutGuest(reservationId);
            System.out.println("Guest checked out successfully for reservation ID: " + reservationId);
        } catch (ReservationNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Cannot check-out: " + e.getMessage());
        }
    }
}
