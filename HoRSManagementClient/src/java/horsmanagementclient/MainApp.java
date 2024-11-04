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
import enumType.EmployeeAccessRightEnum;
import exceptions.EmployeeNotFoundException;
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

    public MainApp() {}

    public MainApp(EmployeeSessionBeanRemote employeeSessionBeanRemote, GuestSessionBeanRemote guestSessionBeanRemote, PartnerSessionBeanRemote partnerSessionBeanRemote, ReservationSessionBeanRemote reservationSessionBeanRemote, RoomRateSessionBeanRemote roomRateSessionBeanRemote, RoomSessionBeanRemote roomSessionBeanRemote, RoomTypeSessionBeanRemote roomTypeSessionBeanRemote) {
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.guestSessionBeanRemote = guestSessionBeanRemote;
        this.partnerSessionBeanRemote = partnerSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.roomRateSessionBeanRemote = roomRateSessionBeanRemote;
        this.roomSessionBeanRemote = roomSessionBeanRemote;
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
    }
    
    public void runApp() {
        Scanner scanner = new Scanner(System.in);
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
            
            if(response == 6)
            {
                break;
            }
        }
        
    }

    private void doLogin() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n*** HoRS Management Client :: Login ***\n");
        System.out.print("Enter username> ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        String password = scanner.nextLine().trim();

        try {
            currentEmployee = employeeSessionBeanRemote.loginEmployee(username, password);
            System.out.println("Login successful! Welcome " + currentEmployee.getUsername() + ".\n");
        } catch (EmployeeNotFoundException e) {
            System.out.println("Invalid login: " + e.getMessage());
        }
    }

    private void doLogout() {
        currentEmployee = null;
        System.out.println("You have logged out successfully.\n");
    }
    
     private void menuMain() {
        Scanner scanner = new Scanner(System.in);
        Integer response;

        while (true) {
            displayMenuOptions();
            System.out.print("> ");
            response = scanner.nextInt();

            if (response == 1) {
                doLogout();
                return;
            } else {
                executeMenuOption(response);
            }
        }
    }

    private void displayMenuOptions() {
        System.out.println("*** HoRS Management Client ***\n");
        System.out.println("1: Logout");
        System.out.println("2: Create New Employee");
        System.out.println("3: View All Employees");
        System.out.println("4: Create New Partner");
        System.out.println("5: View All Partners");
        System.out.println("6: Create New Room Type");
        System.out.println("7: View Room Type Details");
        System.out.println("8: Update Room Type");
        System.out.println("9: Delete Room Type");
        System.out.println("10: View All Room Types");
        System.out.println("11: Create New Room");
        System.out.println("12: View All Rooms");
        System.out.println("13: View Room Allocation Exception Report");
        System.out.println("14: Create New Room Rate");
        System.out.println("15: View Room Rate Details");
        System.out.println("16: Update Room Rate");
        System.out.println("17: Delete Room Rate");
        System.out.println("18: View All Room Rates");
    }

    private void executeMenuOption(int option) {
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
            case 6:
                createNewRoomType();
                break;
            case 7:
                viewRoomTypeDetails();
                break;
            case 8:
                updateRoomType();
                break;
            case 9:
                deleteRoomType();
                break;
            case 10:
                viewAllRoomTypes();
                break;
            case 11:
                createNewRoom();
                break;
            case 12:
                viewAllRooms();
                break;
            case 13:
                viewRoomAllocationExceptionReport();
                break;
            case 14:
                createNewRoomRate();
                break;
            case 15:
                viewRoomRateDetails();
                break;
            case 16:
                updateRoomRate();
                break;
            case 17:
                deleteRoomRate();
                break;
            case 18:
                viewAllRoomRates();
                break;
            default:
                System.out.println("Invalid option, please try again.");
        }
    }

    // Methods for each menu option
    private void createNewEmployee() {
        Scanner scanner = new Scanner(System.in);

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
        System.out.println("Viewing all employees...");
        // Implementation goes here
    }

    private void createNewPartner() {
        System.out.println("Creating new partner...");
        // Implementation goes here
    }

    private void viewAllPartners() {
        System.out.println("Viewing all partners...");
        // Implementation goes here
    }

    private void createNewRoomType() {
        System.out.println("Creating new room type...");
        // Implementation goes here
    }

    private void viewRoomTypeDetails() {
        System.out.println("Viewing room type details...");
        // Implementation goes here
    }

    private void updateRoomType() {
        System.out.println("Updating room type...");
        // Implementation goes here
    }

    private void deleteRoomType() {
        System.out.println("Deleting room type...");
        // Implementation goes here
    }

    private void viewAllRoomTypes() {
        System.out.println("Viewing all room types...");
        // Implementation goes here
    }

    private void createNewRoom() {
        System.out.println("Creating new room...");
        // Implementation goes here
    }

    private void viewAllRooms() {
        System.out.println("Viewing all rooms...");
        // Implementation goes here
    }

    private void viewRoomAllocationExceptionReport() {
        System.out.println("Viewing room allocation exception report...");
        // Implementation goes here
    }

    private void createNewRoomRate() {
        System.out.println("Creating new room rate...");
        // Implementation goes here
    }

    private void viewRoomRateDetails() {
        System.out.println("Viewing room rate details...");
        // Implementation goes here
    }

    private void updateRoomRate() {
        System.out.println("Updating room rate...");
        // Implementation goes here
    }

    private void deleteRoomRate() {
        System.out.println("Deleting room rate...");
        // Implementation goes here
    }

    private void viewAllRoomRates() {
        System.out.println("Viewing all room rates...");
        // Implementation goes here
    }
    
    
}
