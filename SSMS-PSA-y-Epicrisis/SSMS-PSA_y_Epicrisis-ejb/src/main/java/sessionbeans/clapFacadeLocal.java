/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessionbeans;

import entities.clap;
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

    int count();
    
}
