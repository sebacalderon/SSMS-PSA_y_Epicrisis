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
    
    private Auditoria instance;
    
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
        instance =  new Auditoria();
        instance.setOperacion("operacion");
        instance.setAntiguoValor("antiguo");
        instance.setCorreo("correo");
        instance.setFecha(Timestamp.valueOf("2007-09-23 01:00:00"));
        instance.setId(Long.MIN_VALUE);
        instance.setNuevoValor("nuevoValor");
        instance.setTabla("tabla");
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
        String expResult = "tabla";
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
        instance.setTabla("tabla2");
        assertNotNull(instance.getTabla());
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getOperacion method, of class Auditoria.
     */
    @Test
    public void testGetOperacion() {
        System.out.println("getOperacion");
        String expResult = "operacion";
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
        String operacion = "operacion2";
        instance.setOperacion(operacion);
        assertNotNull(instance.getOperacion());
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getAntiguoValor method, of class Auditoria.
     */
    @Test
    public void testGetAntiguoValor() {
        System.out.println("getAntiguoValor");
        String expResult = "antiguo";
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
        instance.setAntiguoValor(antiguoValor);
        assertNotNull(instance.getAntiguoValor());
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getNuevoValor method, of class Auditoria.
     */
    @Test
    public void testGetNuevoValor() {
        System.out.println("getNuevoValor");
        String expResult = "nuevoValor";
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
        instance.setNuevoValor(nuevoValor);
        assertNotNull(instance.getNuevoValor());
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getFecha method, of class Auditoria.
     */
    @Test
    public void testGetFecha() {
        System.out.println("getFecha");
        Timestamp expResult = Timestamp.valueOf("2007-09-23 01:00:00");
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
        instance.setFecha(Timestamp.valueOf("2007-09-23 01:00:00"));
        assertNotNull(instance.getFecha());
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getCorreo method, of class Auditoria.
     */
    @Test
    public void testGetCorreo() {
        System.out.println("getCorreo");
        String expResult = "correo";
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
        instance.setCorreo(correo);
        assertNotNull(instance.getCorreo());
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getId method, of class Auditoria.
     */
    @Test
    public void testGetId() {
        System.out.println("getId");
        Long expResult = Long.MIN_VALUE;
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
        Long id = Long.MAX_VALUE;
        instance.setId(id);
        assertNotNull(instance.getId());
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of hashCode method, of class Auditoria.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        int expResult = -2147483648;
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
