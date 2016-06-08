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
import javax.ejb.Local;

/**
 *
 * @author obi
 */
@Local
public interface clapFacadeLocal {

    void create(clap clap);

    void edit(clap clap);

    void remove(clap clap);

    clap find(Object id);

    List<clap> findAll();

    List<clap> findRange(int[] range);
    
    List<clap> findbyPaciente(int RUN);
    
    List<clap> findbyEstado(String estado, Date fecha);
    
    int count();

    List<clap> findbyEstadoCesfam(String estado, cesfam cesfam, Date fecha);
    
}
