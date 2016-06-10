/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessionbeans;

import entities.cesfam;
import entities.clap;
import entities.paciente;
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
public class pacienteFacade extends AbstractFacade<paciente> implements pacienteFacadeLocal {

    @PersistenceContext(unitName = "cl.diinf_SSMS-PSA_y_Epicrisis-ejb_ejb_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public pacienteFacade() {
        super(paciente.class);
    }
    
    @Override
    public List<paciente> findbyRUN(int RUN) {
        Query query;
        query = em.createNamedQuery("paciente.findbyRUN").
                setParameter("RUN",RUN);
        return query.getResultList();
    }
    
    @Override
    public List<paciente> findbyEstadoCesfamFecha(String estado, cesfam cesfam, Date fecha){
        Query query;
        query = em.createNamedQuery("paciente.findbyEstadoCesfamFecha").
                setParameter("estado", estado).setParameter("cesfam", cesfam).setParameter("fecha", fecha);
        return query.getResultList();
    }
    
    @Override
    public List<paciente> findbyEstadoFecha(String estado, Date fecha) {
        Query query;
        query = em.createNamedQuery("paciente.findbyEstadoFecha").
                setParameter("estado", estado).setParameter("fecha", fecha);
        return query.getResultList();
    }
}
