/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessionbeans;

import entities.Crafft;
import entities.clap;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Seba
 */
@Local
public interface CrafftFacadeLocal {

    void create(Crafft crafft);

    void edit(Crafft crafft);

    void remove(Crafft crafft);

    Crafft find(Object id);

    List<Crafft> findAll();

    List<Crafft> findRange(int[] range);
    
    List<Crafft> findbyClap(clap clap);

    int count();
    
}
