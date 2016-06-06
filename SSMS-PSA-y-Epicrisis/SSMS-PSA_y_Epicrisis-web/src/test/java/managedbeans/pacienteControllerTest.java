/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbeans;

import entities.paciente;
import entities.prevision;
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
public class pacienteControllerTest {
    
    public pacienteControllerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of esFONASA method, of class pacienteController.
     */
    @Test
    public void testEsFONASA() {
        pacienteController instance = new pacienteController();
        paciente paciente = new paciente();
        prevision prevision = new prevision();
        prevision.setNombre("FONASA");
        paciente.setPrevision(prevision);
        instance.setSelected(paciente);
        boolean expResult = true;
        boolean result = instance.esFONASA();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    @Test
    public void testNoEsFONASA() {
        pacienteController instance = new pacienteController();
        paciente paciente = new paciente();
        prevision prevision = new prevision();
        prevision.setNombre("NoFONASA");
        paciente.setPrevision(prevision);
        instance.setSelected(paciente);
        boolean expResult = false;
        boolean result = instance.esFONASA();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }
    
}
