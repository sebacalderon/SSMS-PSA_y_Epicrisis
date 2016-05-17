/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbeans;

import entities.Usuario;
import java.util.List;
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
public class UsuarioControllerTest {
    
    public UsuarioControllerTest() {
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

    
    @Test
    public void testDesactivar() {
        System.out.println("desactivar");
        UsuarioController instance = new UsuarioController();
        Usuario usuario = new Usuario();
        usuario.setHabilitado(true);
        instance.setSelected(usuario);
        instance.desactivar();
        assertEquals(false, instance.getSelected().isHabilitado());
    }

    /**
     * Test of activar method, of class UsuarioController.
     */
    @Test
    public void testActivar() {
        System.out.println("activar");
        UsuarioController instance = new UsuarioController();
        instance.activar();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }   
}
