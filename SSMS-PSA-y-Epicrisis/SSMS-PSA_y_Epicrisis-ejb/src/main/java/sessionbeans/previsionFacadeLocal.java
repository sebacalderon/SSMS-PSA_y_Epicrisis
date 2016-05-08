/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessionbeans;

import entities.prevision;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author obi
 */
@Local
public interface previsionFacadeLocal {

    void create(prevision prevision);

    void edit(prevision prevision);

    void remove(prevision prevision);

    prevision find(Object id);

    List<prevision> findAll();

    List<prevision> findRange(int[] range);

    int count();
    
}
