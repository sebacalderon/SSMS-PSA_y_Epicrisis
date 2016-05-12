/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbeans;

import entities.audit;
import java.util.List;
import java.util.Random;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Seba
 */
public class auditControllerTest {
    auditController instance;
    audit Audit;
    
    public auditControllerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new auditController();
        Audit = new audit();
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testEsIntervencionMinima(){
        System.out.println("testEsIntervencionMinima");
        boolean expResult = true;
        boolean result;
        Random random = new Random();
        if (random.nextBoolean()){
            //HOMBRE
            System.out.println("Hombre");
            Audit.setP1(1);
            Audit.setP2(1);
            Audit.setP3(2);
            instance.setSelected(Audit);
            instance.setSexo(1);
            result = instance.esIntervencionMinima();
        }else{
            //Mujer
            System.out.println("Mujer");
            Audit.setP1(1);
            Audit.setP2(1);
            Audit.setP3(1);
            instance.setSelected(Audit);
            instance.setSexo(2);
            result = instance.esIntervencionMinima();
        }
        assertEquals(expResult, result);
    }
    
    @Test
    public void testNoEsIntervencionMinima(){
        System.out.println("testNoEsIntervencionMinima");
        boolean expResult = false;
        boolean result;
        Random random = new Random();
        if (random.nextBoolean()){
            //HOMBRE
            System.out.println("Hombre");
            Audit.setP1(1);
            Audit.setP2(1);
            Audit.setP3(3);
            instance.setSelected(Audit);
            instance.setSexo(1);
            result = instance.esIntervencionMinima();
        }else{
            //Mujer
            System.out.println("Mujer");
            Audit.setP1(1);
            Audit.setP2(1);
            Audit.setP3(2);
            instance.setSelected(Audit);
            instance.setSexo(2);
            result = instance.esIntervencionMinima();
        }
        assertEquals(expResult, result);
    }
    
}
