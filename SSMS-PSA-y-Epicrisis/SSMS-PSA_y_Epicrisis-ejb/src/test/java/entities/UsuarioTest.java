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
public class UsuarioTest {
    private Usuario usuario;
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
        usuario.setPassword("");
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of cambiarPassword method, of class Usuario.
     */
    @Test
    public void testCambiarPassword() {
        String old_password = "1234";
        String new_password = "1324";
        boolean expResult = true;
        usuario.setRUT("05.893.349-k");
        boolean result = usuario.cambiarPassword(old_password, new_password);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }
    
}
