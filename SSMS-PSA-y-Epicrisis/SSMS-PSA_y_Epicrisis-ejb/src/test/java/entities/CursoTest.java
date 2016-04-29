/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author calde
 */
public class CursoTest {
    private Curso curso;
    
    public CursoTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        curso = new Curso();
        curso.setId(Long.MIN_VALUE);
        curso.setNombre("nombre");
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getNombre method, of class Curso.
     */
    @Test
    public void testGetNombre() {
        System.out.println("getNombre");
        String expResult = "nombre";
        String result = curso.getNombre();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of setNombre method, of class Curso.
     */
    @Test
    public void testSetNombre() {
        System.out.println("setNombre");
        String nombre = "nombre2";
        curso.setNombre(nombre);
        assertNotNull(curso.getNombre());
    }

    /**
     * Test of getId method, of class Curso.
     */
    @Test
    public void testGetId() {
        System.out.println("getId");
        Curso instance = new Curso();
        Long expResult = Long.MIN_VALUE;
        Long result = curso.getId();
        assertEquals(expResult, result);
    }

    /**
     * Test of setId method, of class Curso.
     */
    @Test
    public void testSetId() {
        System.out.println("setId");
        Long id = Long.MAX_VALUE;
        curso.setId(id);
        assertNotNull(curso.getId());
    }

    /**
     * Test of hashCode method, of class Curso.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        int expResult = -2147483648;
        int result = curso.hashCode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of equals method, of class Curso.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object object = null;
        boolean expResult = false;
        boolean result = curso.equals(object);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of toString method, of class Curso.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        String expResult = "entities.Curso[ id=-9223372036854775808 ]";
        String result = curso.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }
    
}
