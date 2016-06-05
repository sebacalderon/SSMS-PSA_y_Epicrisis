/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbeans;

import managedbeans.CrafftController;
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
public class CrafftControllerTest {
    CrafftController instance;
    
    public CrafftControllerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new CrafftController();
        instance.setPuntajeA(0);
    }
    
    @After
    public void tearDown() {
    }
    
    /**
     * Test of serviceChangeA method, of class CrafftController.
     */
    @Test
    public void testServiceChangeA() {
        boolean resp = false;
        instance.serviceChangeA(resp);
        assertEquals(1, instance.getPuntajeA());
    }
    
}
