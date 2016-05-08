/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessionbeans;

import entities.ley_social;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author obi
 */
@Local
public interface ley_socialFacadeLocal {

    void create(ley_social ley_social);

    void edit(ley_social ley_social);

    void remove(ley_social ley_social);

    ley_social find(Object id);

    List<ley_social> findAll();

    List<ley_social> findRange(int[] range);

    int count();
    
}
