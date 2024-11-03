/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.Partner;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author hohin
 */
@Remote
public interface PartnerSessionBeanRemote {
    
    public Partner createPartner(Partner partner);

    public List<Partner> viewAllPartners();
    
}
