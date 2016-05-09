/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbeans;

import entities.Usuario;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author Seba
 */
public class LoginControllerTest {
    
    public LoginControllerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
       LoginController instance = mock(LoginController.class);
       when(instance.getCorreo()).thenReturn("correo");
       when(instance.getPassword()).thenReturn("correo");
       //when(instance.getUserCtrl()).thenReturn();
       //when(instance.getUsuarioLogueado()).thenReturn();
    }
    
    @After
    public void tearDown() {
    }
    /**
     * Test of login method, of class LoginController.
     */
    @Test
    public void testLogin() {
        System.out.println("login");
        LoginController instance = new LoginController();
        String expResult = "";
        String result = instance.login();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of logout method, of class LoginController.
     */
    @Test
    public void testLogout() {
        System.out.println("logout");
        LoginController instance = new LoginController();
        String expResult = "";
        String result = instance.logout();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of verifyLogin method, of class LoginController.
     */
    @Test
    public void testVerifyLogin() {
        System.out.println("verifyLogin");
        LoginController instance = new LoginController();
        boolean expResult = false;
        boolean result = instance.verifyLogin();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of esEmpleadoMunicipal method, of class LoginController.
     */
    @Test
    public void testEsEmpleadoMunicipal() {
        System.out.println("esEmpleadoMunicipal");
        LoginController instance = new LoginController();
        boolean expResult = false;
        boolean result = instance.esEmpleadoMunicipal();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of esFuncionario method, of class LoginController.
     */
    @Test
    public void testEsFuncionario() {
        System.out.println("esFuncionario");
        LoginController instance = new LoginController();
        boolean expResult = false;
        boolean result = instance.esFuncionario();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of esSuperUsuario method, of class LoginController.
     */
    @Test
    public void testEsSuperUsuario() {
        System.out.println("esSuperUsuario");
        LoginController instance = new LoginController();
        boolean expResult = false;
        boolean result = instance.esSuperUsuario();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
