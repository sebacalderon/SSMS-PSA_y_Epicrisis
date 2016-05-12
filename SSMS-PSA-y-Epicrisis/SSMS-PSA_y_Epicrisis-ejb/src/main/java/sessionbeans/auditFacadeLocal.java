/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessionbeans;

import entities.audit;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Seba
 */
@Local
public interface auditFacadeLocal {

    void create(audit audit);

    void edit(audit audit);

    void remove(audit audit);

    audit find(Object id);

    List<audit> findAll();

    List<audit> findRange(int[] range);

    int count();
    
}
