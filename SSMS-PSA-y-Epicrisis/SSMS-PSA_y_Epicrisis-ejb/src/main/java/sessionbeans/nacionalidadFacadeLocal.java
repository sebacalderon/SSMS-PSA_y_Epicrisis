/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessionbeans;

import entities.nacionalidad;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author obi
 */
@Local
public interface nacionalidadFacadeLocal {

    void create(nacionalidad nacionalidad);

    void edit(nacionalidad nacionalidad);

    void remove(nacionalidad nacionalidad);

    nacionalidad find(Object id);

    List<nacionalidad> findAll();

    List<nacionalidad> findRange(int[] range);

    int count();
    
}
