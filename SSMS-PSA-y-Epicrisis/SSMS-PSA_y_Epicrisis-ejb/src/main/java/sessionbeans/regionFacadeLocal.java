/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessionbeans;

import entities.region;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author obi
 */
@Local
public interface regionFacadeLocal {

    void create(region region);

    void edit(region region);

    void remove(region region);

    region find(Object id);

    List<region> findAll();

    List<region> findRange(int[] range);

    int count();
    
}
