/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessionbeans;

import entities.cesfam;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author obi
 */
@Local
public interface cesfamFacadeLocal {

    void create(cesfam cesfam);

    void edit(cesfam cesfam);

    void remove(cesfam cesfam);

    cesfam find(Object id);

    List<cesfam> findAll();

    List<cesfam> findRange(int[] range);

    int count();
    
}
