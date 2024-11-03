/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.Partner;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author hohin
 */
@Local
public interface PartnerSessionBeanLocal {

    public Partner createPartner(Partner partner);

    public List<Partner> viewAllPartners();
    
}
