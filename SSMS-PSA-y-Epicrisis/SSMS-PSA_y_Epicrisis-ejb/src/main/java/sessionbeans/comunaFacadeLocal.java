/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessionbeans;

import entities.comuna;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author obi
 */
@Local
public interface comunaFacadeLocal {

    void create(comuna comuna);

    void edit(comuna comuna);

    void remove(comuna comuna);

    comuna find(Object id);

    List<comuna> findAll();

    List<comuna> findRange(int[] range);

    int count();
    
}
