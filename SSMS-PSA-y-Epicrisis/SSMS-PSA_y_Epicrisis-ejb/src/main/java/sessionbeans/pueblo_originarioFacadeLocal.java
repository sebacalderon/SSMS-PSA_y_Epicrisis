/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessionbeans;

import entities.pueblo_originario;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author obi
 */
@Local
public interface pueblo_originarioFacadeLocal {

    void create(pueblo_originario pueblo_originario);

    void edit(pueblo_originario pueblo_originario);

    void remove(pueblo_originario pueblo_originario);

    pueblo_originario find(Object id);

    List<pueblo_originario> findAll();

    List<pueblo_originario> findRange(int[] range);

    int count();
    
}
