/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessionbeans;

import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date;
import entities.cesfam;
import entities.clap;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author obi
 */
@Stateless
public class clapFacade extends AbstractFacade<clap> implements clapFacadeLocal {

    @PersistenceContext(unitName = "cl.diinf_SSMS-PSA_y_Epicrisis-ejb_ejb_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public clapFacade() {
        super(clap.class);
    }
    
    @Override
    public List<clap> findbyPaciente(int RUN) {
        Query query;
        query = em.createNamedQuery("clap.findbyPaciente").
                setParameter("RUN",RUN);
        return query.getResultList();
    }
    
    @Override
    public List<clap> findbyEstado(String estado, Date fecha) {
        Query query;
        query = em.createNamedQuery("clap.findbyEstado").
                setParameter("estado", estado).setParameter("fecha", fecha);
        return query.getResultList();
    }
    
    @Override
    public List<clap> findbyEstadoCesfam(String estado, cesfam cesfam, Date fecha){
        Query query;
        query = em.createNamedQuery("clap.findbyEstadoCesfam").
                setParameter("estado", estado).setParameter("cesfam", cesfam).setParameter("fecha", fecha);
        return query.getResultList();
    }
}
