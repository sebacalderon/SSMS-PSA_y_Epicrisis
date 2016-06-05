/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessionbeans;

import entities.paciente;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author obi
 */
@Local
public interface pacienteFacadeLocal {

    void create(paciente paciente);

    void edit(paciente paciente);

    void remove(paciente paciente);

    paciente find(Object id);

    List<paciente> findAll();

    List<paciente> findRange(int[] range);

    int count();
    
    List<paciente> findbyRUN(int RUN);
}
