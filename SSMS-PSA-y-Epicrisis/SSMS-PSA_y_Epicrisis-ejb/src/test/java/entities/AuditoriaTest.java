/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.sql.Timestamp;
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
public class AuditoriaTest {
    
    public AuditoriaTest() {
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
     * Test of getTabla method, of class Auditoria.
     */
    @Test
    public void testGetTabla() {
        System.out.println("getTabla");
        Auditoria instance = new Auditoria();
        String expResult = null;
        String result = instance.getTabla();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of setTabla method, of class Auditoria.
     */
    @Test
    public void testSetTabla() {
        System.out.println("setTabla");
        String tabla = "";
        Auditoria instance = new Auditoria();
        instance.setTabla(tabla);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getOperacion method, of class Auditoria.
     */
    @Test
    public void testGetOperacion() {
        System.out.println("getOperacion");
        Auditoria instance = new Auditoria();
        String expResult = null;
        String result = instance.getOperacion();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of setOperacion method, of class Auditoria.
     */
    @Test
    public void testSetOperacion() {
        System.out.println("setOperacion");
        String operacion = "";
        Auditoria instance = new Auditoria();
        instance.setOperacion(operacion);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getAntiguoValor method, of class Auditoria.
     */
    @Test
    public void testGetAntiguoValor() {
        System.out.println("getAntiguoValor");
        Auditoria instance = new Auditoria();
        String expResult = null;
        String result = instance.getAntiguoValor();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of setAntiguoValor method, of class Auditoria.
     */
    @Test
    public void testSetAntiguoValor() {
        System.out.println("setAntiguoValor");
        String antiguoValor = "";
        Auditoria instance = new Auditoria();
        instance.setAntiguoValor(antiguoValor);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getNuevoValor method, of class Auditoria.
     */
    @Test
    public void testGetNuevoValor() {
        System.out.println("getNuevoValor");
        Auditoria instance = new Auditoria();
        String expResult = null;
        String result = instance.getNuevoValor();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of setNuevoValor method, of class Auditoria.
     */
    @Test
    public void testSetNuevoValor() {
        System.out.println("setNuevoValor");
        String nuevoValor = "";
        Auditoria instance = new Auditoria();
        instance.setNuevoValor(nuevoValor);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getFecha method, of class Auditoria.
     */
    @Test
    public void testGetFecha() {
        System.out.println("getFecha");
        Auditoria instance = new Auditoria();
        Timestamp expResult = null;
        Timestamp result = instance.getFecha();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of setFecha method, of class Auditoria.
     */
    @Test
    public void testSetFecha() {
        System.out.println("setFecha");
        Timestamp fecha = null;
        Auditoria instance = new Auditoria();
        instance.setFecha(fecha);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getCorreo method, of class Auditoria.
     */
    @Test
    public void testGetCorreo() {
        System.out.println("getCorreo");
        Auditoria instance = new Auditoria();
        String expResult = null;
        String result = instance.getCorreo();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of setCorreo method, of class Auditoria.
     */
    @Test
    public void testSetCorreo() {
        System.out.println("setCorreo");
        String correo = "";
        Auditoria instance = new Auditoria();
        instance.setCorreo(correo);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getId method, of class Auditoria.
     */
    @Test
    public void testGetId() {
        System.out.println("getId");
        Auditoria instance = new Auditoria();
        Long expResult = null;
        Long result = instance.getId();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of setId method, of class Auditoria.
     */
    @Test
    public void testSetId() {
        System.out.println("setId");
        Long id = null;
        Auditoria instance = new Auditoria();
        instance.setId(id);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of hashCode method, of class Auditoria.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        Auditoria instance = new Auditoria();
        int expResult = 0;
        int result = instance.hashCode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class Auditoria.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object object = null;
        Auditoria instance = new Auditoria();
        boolean expResult = false;
        boolean result = instance.equals(object);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class Auditoria.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Auditoria instance = new Auditoria();
        String expResult = "";
        String result = instance.toString();
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
}
