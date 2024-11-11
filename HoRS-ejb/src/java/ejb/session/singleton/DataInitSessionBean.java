/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB31/SingletonEjbClass.java to edit this template
 */
package ejb.session.singleton;

import entity.Employee;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import enumType.EmployeeAccessRightEnum;
import enumType.RoomAvailabilityEnum;
import enumType.RoomRateTypeEnum;
import enumType.RoomTypeEnum;
import java.math.BigDecimal;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author serwe
 */
@Singleton
@LocalBean
//@Startup
public class DataInitSessionBean {

    @PersistenceContext(unitName = "HoRS-ejbPU")
    private EntityManager em;

    @PostConstruct
    public void postConstruct() {
        initializeEmployees();
        initializeRoomTypes();
        initializeRoomRates();
        initializeRooms();
    }

    private void initializeEmployees() {
        em.persist(new Employee("sysadmin", "password", EmployeeAccessRightEnum.SYSTEM_ADMIN));
        em.persist(new Employee("opmanager", "password", EmployeeAccessRightEnum.OPERATION_MANAGER));
        em.persist(new Employee("salesmanager", "password", EmployeeAccessRightEnum.SALES_MANAGER));
        em.persist(new Employee("guestrelo", "password", EmployeeAccessRightEnum.GUEST_RELATION_OFFICER));
    }

    private void initializeRoomTypes() {
        RoomType deluxe = new RoomType(RoomTypeEnum.DELUXE, "Deluxe Room", 30.0, "Queen", 2, "Wi-Fi, TV");
        RoomType premier = new RoomType(RoomTypeEnum.PREMIER, "Premier Room", 40.0, "King", 3, "Wi-Fi, TV, Sofa");
        RoomType family = new RoomType(RoomTypeEnum.FAMILY, "Family Room", 50.0, "Double Queen", 4, "Wi-Fi, TV, Kitchenette");
        RoomType juniorSuite = new RoomType(RoomTypeEnum.JUNIOR_SUITE, "Junior Suite", 60.0, "King", 3, "Wi-Fi, TV, Living Room");
        RoomType grandSuite = new RoomType(RoomTypeEnum.GRAND_SUITE, "Grand Suite", 80.0, "King", 5, "Wi-Fi, TV, Living Room, Kitchen");

        em.persist(deluxe);
        em.persist(premier);
        em.persist(family);
        em.persist(juniorSuite);
        em.persist(grandSuite);
    }

    private void initializeRoomRates() {
        RoomType deluxe = em.createQuery("SELECT r FROM RoomType r WHERE r.name = :name", RoomType.class)
                .setParameter("name", RoomTypeEnum.DELUXE)
                .getSingleResult();
        RoomType premier = em.createQuery("SELECT r FROM RoomType r WHERE r.name = :name", RoomType.class)
                .setParameter("name", RoomTypeEnum.PREMIER)
                .getSingleResult();
        RoomType family = em.createQuery("SELECT r FROM RoomType r WHERE r.name = :name", RoomType.class)
                .setParameter("name", RoomTypeEnum.FAMILY)
                .getSingleResult();
        RoomType juniorSuite = em.createQuery("SELECT r FROM RoomType r WHERE r.name = :name", RoomType.class)
                .setParameter("name", RoomTypeEnum.JUNIOR_SUITE)
                .getSingleResult();
        RoomType grandSuite = em.createQuery("SELECT r FROM RoomType r WHERE r.name = :name", RoomType.class)
                .setParameter("name", RoomTypeEnum.GRAND_SUITE)
                .getSingleResult();

        RoomRate deluxePublished = new RoomRate("Deluxe Room Published", RoomRateTypeEnum.PUBLISHED, new BigDecimal(100), null, null);
        deluxePublished.setRoomType(deluxe);
        em.persist(deluxePublished);

        RoomRate deluxeNormal = new RoomRate("Deluxe Room Normal", RoomRateTypeEnum.NORMAL, new BigDecimal(50), null, null);
        deluxeNormal.setRoomType(deluxe);
        em.persist(deluxeNormal);

        RoomRate premierPublished = new RoomRate("Premier Room Published", RoomRateTypeEnum.PUBLISHED, new BigDecimal(200), null, null);
        premierPublished.setRoomType(premier);
        em.persist(premierPublished);

        RoomRate premierNormal = new RoomRate("Premier Room Normal", RoomRateTypeEnum.NORMAL, new BigDecimal(100), null, null);
        premierNormal.setRoomType(premier);
        em.persist(premierNormal);

        RoomRate familyPublished = new RoomRate("Family Room Published", RoomRateTypeEnum.PUBLISHED, new BigDecimal(300), null, null);
        familyPublished.setRoomType(family);
        em.persist(familyPublished);

        RoomRate familyNormal = new RoomRate("Family Room Normal", RoomRateTypeEnum.NORMAL, new BigDecimal(150), null, null);
        familyNormal.setRoomType(family);
        em.persist(familyNormal);

        RoomRate juniorSuitePublished = new RoomRate("Junior Suite Published", RoomRateTypeEnum.PUBLISHED, new BigDecimal(400), null, null);
        juniorSuitePublished.setRoomType(juniorSuite);
        em.persist(juniorSuitePublished);

        RoomRate juniorSuiteNormal = new RoomRate("Junior Suite Normal", RoomRateTypeEnum.NORMAL, new BigDecimal(200), null, null);
        juniorSuiteNormal.setRoomType(juniorSuite);
        em.persist(juniorSuiteNormal);

        RoomRate grandSuitePublished = new RoomRate("Grand Suite Published", RoomRateTypeEnum.PUBLISHED, new BigDecimal(500), null, null);
        grandSuitePublished.setRoomType(grandSuite);
        em.persist(grandSuitePublished);

        RoomRate grandSuiteNormal = new RoomRate("Grand Suite Normal", RoomRateTypeEnum.NORMAL, new BigDecimal(250), null, null);
        grandSuiteNormal.setRoomType(grandSuite);
        em.persist(grandSuiteNormal);
    }


    private void initializeRooms() {
        RoomType deluxe = em.createQuery("SELECT r FROM RoomType r WHERE r.name = :name", RoomType.class)
                .setParameter("name", RoomTypeEnum.DELUXE)
                .getSingleResult();
        RoomType premier = em.createQuery("SELECT r FROM RoomType r WHERE r.name = :name", RoomType.class)
                .setParameter("name", RoomTypeEnum.PREMIER)
                .getSingleResult();
        RoomType family = em.createQuery("SELECT r FROM RoomType r WHERE r.name = :name", RoomType.class)
                .setParameter("name", RoomTypeEnum.FAMILY)
                .getSingleResult();
        RoomType juniorSuite = em.createQuery("SELECT r FROM RoomType r WHERE r.name = :name", RoomType.class)
                .setParameter("name", RoomTypeEnum.JUNIOR_SUITE)
                .getSingleResult();
        RoomType grandSuite = em.createQuery("SELECT r FROM RoomType r WHERE r.name = :name", RoomType.class)
                .setParameter("name", RoomTypeEnum.GRAND_SUITE)
                .getSingleResult();

        createRoomsForType(deluxe, new String[]{"0101", "0201", "0301", "0401", "0501"});
        createRoomsForType(premier, new String[]{"0102", "0202", "0302", "0402", "0502"});
        createRoomsForType(family, new String[]{"0103", "0203", "0303", "0403", "0503"});
        createRoomsForType(juniorSuite, new String[]{"0104", "0204", "0304", "0404", "0504"});
        createRoomsForType(grandSuite, new String[]{"0105", "0205", "0305", "0405", "0505"});
    }

    private void createRoomsForType(RoomType roomType, String[] roomNumbers) {
        for (String roomNumber : roomNumbers) {
            int floorNumber = Integer.parseInt(roomNumber.substring(0, 2));
            int roomSequence = Integer.parseInt(roomNumber.substring(2, 4));

            Room room = new Room(floorNumber, roomSequence, RoomAvailabilityEnum.AVAILABLE, roomType);
            em.persist(room);
        }
    }
}

