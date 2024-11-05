/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Employee;
import entity.Partner;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import enumType.EmployeeAccessRightEnum;
import enumType.RoomTypeEnum;
import exceptions.EmployeeNotFoundException;
import exceptions.RoomTypeNotFoundException;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author hohin
 */
public class MainApp {
    

    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private GuestSessionBeanRemote guestSessionBeanRemote;
    private PartnerSessionBeanRemote partnerSessionBeanRemote;
    private ReservationSessionBeanRemote reservationSessionBeanRemote;
    private RoomRateSessionBeanRemote roomRateSessionBeanRemote;
    private RoomSessionBeanRemote roomSessionBeanRemote;
    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;
    
    private Employee currentEmployee = null;
    private Scanner scanner;

    public MainApp() {}

    public MainApp(EmployeeSessionBeanRemote employeeSessionBeanRemote, GuestSessionBeanRemote guestSessionBeanRemote, PartnerSessionBeanRemote partnerSessionBeanRemote, ReservationSessionBeanRemote reservationSessionBeanRemote, RoomRateSessionBeanRemote roomRateSessionBeanRemote, RoomSessionBeanRemote roomSessionBeanRemote, RoomTypeSessionBeanRemote roomTypeSessionBeanRemote) {
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
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
        
        while(true)
        {
            System.out.println("*** Welcome to HoRS Management Client ***\n");
            System.out.println("1: Login");
            System.out.println("2: Exit\n");
            response = 0;
            
            while(response < 1 || response > 6)
            {
                System.out.print("> ");

                response = scanner.nextInt();

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
            System.out.println("Login successful! Welcome " + currentEmployee.getUsername() + ".\n");
            EmployeeAccessRightEnum accessRight = currentEmployee.getAccessRight();
            menuMain(accessRight);
        } catch (EmployeeNotFoundException e) {
            System.out.println("Invalid login: " + e.getMessage());
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
        System.out.println("*** HoRS Management Client -- Operation Manager ***\n");
        System.out.println("1: Logout");
        System.out.println("2: Create New Room Type");
        System.out.println("3: View Room Type Details");
        System.out.println("4: View All Room Types");
        System.out.println("5: Create New Room");
        System.out.println("6: View All Rooms");
        System.out.println("7: View Room Allocation Exception Report");
    }
    
    private void displayMenuOptionsSalesManager() {
        System.out.println("*** HoRS Management Client -- Sales Manager ***\n");
        System.out.println("1: Logout");
        System.out.println("2: Create New Room Rate");
        System.out.println("3: View Room Rate Details");
        System.out.println("4: View All Room Rates");
    }
    
    private void displayMenuOptionsGuestRelationOfficer() {
        System.out.println("*** HoRS Management Client -- Guest Relation Officer ***\n");
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
                viewAllRooms();
                break;
            case 7:
                viewRoomAllocationExceptionReport();
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
        System.out.print("*** New Employee *** ");
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        // Choose access right based on user input
        System.out.println("Select Employee Access Right:");
        System.out.println("1. System Administrator");
        System.out.println("2. Operation Manager");
        System.out.println("3. Sales Manager");
        System.out.println("4. Guest Relation Officer");
        System.out.println("5. Back");
        int accessRightChoice = scanner.nextInt();

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
        System.out.print("\n*** New Partner *** ");
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
        }
    }

    private void createNewRoomType() {
        System.out.print("\n*** New Room Type *** ");
        System.out.print("Enter Room Type Option (1.Deluxe 2.Premier 3.Family 4.Junior Suite 5.Grand Suite): ");
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
            case 2:
                rtEnum = RoomTypeEnum.PREMIER;
            case 3:
                rtEnum = RoomTypeEnum.FAMILY;
            case 4:
                rtEnum = RoomTypeEnum.JUNIOR_SUITE;
            case 5:
                rtEnum= RoomTypeEnum.GRAND_SUITE;
            default:
                System.out.println("Error!"); //update message
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
        System.out.println("\nEnter Room Type ID:");
        long roomTypeID = scanner.nextLong();
        try {
            while (true) {
                System.out.println("\n*** Room Type Details ***");
                RoomType roomType = roomTypeSessionBeanRemote.viewRoomType(roomTypeID);
                
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
                        updateRoomType();
                        break;
                    case 2:
                        deleteRoomType();
                        break;
                    case 3:
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        } catch (RoomTypeNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    //NOT DONE need take into account rs with other entities lazy fetching all that shit
    private void updateRoomType() {
        System.out.println("Updating room type...");
        // Implementation goes here
    }

    //NOT DONE need take into account rs with other entities lazy fetching all that shit
    private void deleteRoomType() {
        System.out.println("Deleting room type...");
        // Implementation goes here
    }

    private void viewAllRoomTypes() {
        System.out.println("\n*** Room Type List ***");
        List<RoomType> roomTypes = roomTypeSessionBeanRemote.getAllRoomTypes();
        for (RoomType r : roomTypes) {
            System.out.println("ID: "+ r.getRoomTypeID() + " | Name: " + r.getName());
        }
    }

    private void createNewRoom() {
        System.out.print("\n*** New Room *** ");
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
            System.out.println("Error creating employee: " + e.getMessage());
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
        System.out.println("Viewing room allocation exception report...");
        // Implementation goes here
    }

    //NOT DONE
    private void createNewRoomRate() {
        System.out.print("\n*** New Room *** ");
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
            System.out.println("Error creating employee: " + e.getMessage());
        }
    }

    private void viewRoomRateDetails() {
        while (true) {
            System.out.println("\n*** Room Rate Details ***");
            //show room details here
            System.out.println("1. Update Room Rate");
            System.out.println("2. Delete Room Rate");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1:
                    updateRoomRate();
                    break;
                case 2:
                    deleteRoomRate();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    //NOT DONE need take into account rs with other entities lazy fetching all that shit
    private void updateRoomRate() {
        System.out.println("Updating room rate...");
        // Implementation goes here
    }

    //NOT DONE need take into account rs with other entities lazy fetching all that shit
    private void deleteRoomRate() {
        System.out.println("Deleting room rate...");
        // Implementation goes here
    }

    private void viewAllRoomRates() {
        System.out.println("\n*** Room Rates List ***");
        List<RoomRate> roomRates = roomRateSessionBeanRemote.getAllRoomRates();
        for (RoomRate r : roomRates) {
            System.out.println("ID: "+ r.getRateID() + " | Name: " + r.getName() +  "| Role: " + r.getRateType());
        }
    }
    
    //NOT DONE
    private void walkInSearchRoom() {
        while (true) {
            System.out.println("\n--- Room Search ---");
            //show search room details here
            System.out.println("1. Update Room Rate");
            System.out.println("2. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1:
                    walkInReserveRoom();
                    break;
                case 2:
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    //NOT DONE
    private void walkInReserveRoom() {
        System.out.println("Deleting room rate...");
        // Implementation goes here
    }
    
    //NOT DONE
    private void checkInGuest() {
        System.out.println("Check in guest");
        // Implementation goes here
    }
    
    //NOT DONE
    private void checkOutGuest() {
        System.out.println("Check out guest");
        // Implementation goes here
    }
    
}
