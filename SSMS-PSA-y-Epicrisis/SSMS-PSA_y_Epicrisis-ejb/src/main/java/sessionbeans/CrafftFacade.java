/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessionbeans;

import entities.Crafft;
import entities.clap;
import entities.paciente;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Seba
 */
@Stateless
public class CrafftFacade extends AbstractFacade<Crafft> implements CrafftFacadeLocal {

    @PersistenceContext(unitName = "cl.diinf_SSMS-PSA_y_Epicrisis-ejb_ejb_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CrafftFacade() {
        super(Crafft.class);
    }
    
    @Override
    public List<Crafft> findbyClap(clap clap) {
        Query query;
        query = em.createNamedQuery("Crafft.findbyClap").
                setParameter("clap",clap);
        return query.getResultList();
    }
    
}
