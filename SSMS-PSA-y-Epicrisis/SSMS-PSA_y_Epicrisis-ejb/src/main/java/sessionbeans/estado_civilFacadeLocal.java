/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessionbeans;

import entities.estado_civil;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author obi
 */
@Local
public interface estado_civilFacadeLocal {

    void create(estado_civil estado_civil);

    void edit(estado_civil estado_civil);

    void remove(estado_civil estado_civil);

    estado_civil find(Object id);

    List<estado_civil> findAll();

    List<estado_civil> findRange(int[] range);

    int count();
    
}
