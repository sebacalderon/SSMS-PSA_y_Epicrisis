/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessionbeans;

import entities.parametros;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Seba
 */
@Local
public interface parametrosFacadeLocal {

    void create(parametros parametros);

    void edit(parametros parametros);

    void remove(parametros parametros);

    parametros find(Object id);

    List<parametros> findAll();

    List<parametros> findRange(int[] range);

    int count();
    
}
