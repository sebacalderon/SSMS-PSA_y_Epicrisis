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
import static org.mockito.Mockito.mock;

/**
 *
 * @author calde
 */
public class UsuarioTest {
    private Usuario usuario;
    private Curso curso;
    public UsuarioTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        usuario = new Usuario();
        curso = mock(Curso.class);
        usuario.setCorreo("correo");
        usuario.setCurso(curso);
        usuario.setId(Long.MIN_VALUE);
        usuario.setNombre("nombre");
        usuario.setPassword("");
        usuario.setRol("rol");
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getCurso method, of class Usuario.
     */
    @Test
    public void testGetCurso() {
        System.out.println("getCurso");
        Curso expResult = curso;
        Curso result = usuario.getCurso();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of setCurso method, of class Usuario.
     */
    @Test
    public void testSetCurso() {
        System.out.println("setCurso");
        usuario.setCurso(curso);
        assertNotNull(usuario.getCurso());
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of getRol method, of class Usuario.
     */
    @Test
    public void testGetRol() {
        System.out.println("getRol");
        String expResult = "rol";
        String result = usuario.getRol();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of setRol method, of class Usuario.
     */
    @Test
    public void testSetRol() {
        System.out.println("setRol");
        String rol = "";
        usuario.setRol(rol);
        assertNotNull(usuario.getRol());
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of getCorreo method, of class Usuario.
     */
    @Test
    public void testGetCorreo() {
        System.out.println("getCorreo");
        String expResult = "correo";
        String result = usuario.getCorreo();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of setCorreo method, of class Usuario.
     */
    @Test
    public void testSetCorreo() {
        System.out.println("setCorreo");
        String correo = "";
        usuario.setCorreo(correo);
        assertNotNull(usuario.getCorreo());
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of getPassword method, of class Usuario.
     */
    @Test
    public void testGetPassword() {
        System.out.println("getPassword");
        String expResult = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
        String result = usuario.getPassword();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        
    }

    /**
     * Test of setPassword method, of class Usuario.
     */
    @Test
    public void testSetPassword() {
        System.out.println("setPassword");
        String password = "";
        usuario.setPassword(password);
        assertNotNull(usuario.getPassword());
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of cambiarPassword method, of class Usuario.
     */
    @Test
    public void testCambiarPassword() {
        System.out.println("cambiarPassword");
        String old_password = "";
        String new_password = "1";
        boolean expResult = true;
        boolean result = usuario.cambiarPassword(old_password, new_password);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of getNombre method, of class Usuario.
     */
    @Test
    public void testGetNombre() {
        System.out.println("getNombre");
        String expResult = "nombre";
        String result = usuario.getNombre();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of setNombre method, of class Usuario.
     */
    @Test
    public void testSetNombre() {
        System.out.println("setNombre");
        String nombre = "nombre2";
        usuario.setNombre(nombre);
        assertNotNull(usuario.getNombre());
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of getId method, of class Usuario.
     */
    @Test
    public void testGetId() {
        System.out.println("getId");
        Long expResult = Long.MIN_VALUE;
        Long result = usuario.getId();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of setId method, of class Usuario.
     */
    @Test
    public void testSetId() {
        System.out.println("setId");
        Long id = Long.MAX_VALUE;
        usuario.setId(id);
        assertNotNull(usuario.getId());
        
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of hashCode method, of class Usuario.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        int expResult = -2147483648;
        int result = usuario.hashCode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of equals method, of class Usuario.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object object = null;
        boolean expResult = false;
        boolean result = usuario.equals(object);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of toString method, of class Usuario.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        String expResult = "entities.Usuario[ id=-9223372036854775808 ]";
        String result = usuario.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }
    
}
