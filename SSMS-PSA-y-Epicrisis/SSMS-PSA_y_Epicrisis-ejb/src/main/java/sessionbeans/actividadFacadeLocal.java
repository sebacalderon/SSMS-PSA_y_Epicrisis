/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessionbeans;

import entities.actividad;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Seba
 */
@Local
public interface actividadFacadeLocal {

    void create(actividad actividad);

    void edit(actividad actividad);

    void remove(actividad actividad);

    actividad find(Object id);

    List<actividad> findAll();

    List<actividad> findRange(int[] range);

    int count();
    
}
