/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessionbeans;

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
    public List<clap> findbyEstadoFecha(String estado, Date fecha) {
        Query query;
        query = em.createNamedQuery("clap.findbyEstadoFecha").
                setParameter("estado", estado).setParameter("fecha", fecha);
        return query.getResultList();
    }
    
    @Override
    public List<clap> findbyEstadoCesfamFecha(String estado, cesfam cesfam, Date fecha){
        Query query;
        query = em.createNamedQuery("clap.findbyEstadoCesfamFecha").
                setParameter("estado", estado).setParameter("cesfam", cesfam).setParameter("fecha", fecha);
        return query.getResultList();
    }
    
    @Override
    public List<clap> findbyPacienteEstado(int RUN, String estado) {
        Query query;
        query = em.createNamedQuery("clap.findbyPacienteEstado").
                setParameter("RUN",RUN).setParameter("estado", estado);
        return query.getResultList();
    }
    
    @Override
    public List<clap> findbyEstadoEntreFechas(Date fecha1, Date fecha2, String estado) {
        Query query;
        query = em.createNamedQuery("clap.findbyEstadoEntreFechas").
                setParameter("fecha1",fecha1).setParameter("fecha2", fecha2).setParameter("estado", estado);
        return query.getResultList();
    }
    
    @Override
    public List<clap> findbyEstadoEntreFechasCesfam(Date fecha1, Date fecha2, String estado, cesfam cesfam) {
        Query query;
        query = em.createNamedQuery("clap.findbyEstadoEntreFechasCesfam").
                setParameter("fecha1",fecha1).setParameter("fecha2", fecha2).setParameter("estado", estado).setParameter("cesfam", cesfam);
        return query.getResultList();
    }
    @Override
    public List<clap> findbyEstado(String estado) {
        Query query;
        query = em.createNamedQuery("clap.findbyEstado").
                setParameter("estado", estado);
        return query.getResultList();
    }
    @Override
    public List<clap> findbyEstadoCesfam(String estado, cesfam cesfam) {
        Query query;
        query = em.createNamedQuery("clap.findbyEstadoCesfam").
                setParameter("estado", estado).setParameter("cesfam", cesfam);
        return query.getResultList();
    }
}
