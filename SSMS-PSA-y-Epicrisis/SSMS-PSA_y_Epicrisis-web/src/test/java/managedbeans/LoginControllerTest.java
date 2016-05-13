/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbeans;

import entities.Rol;
import entities.Usuario;
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
public class LoginControllerTest {
    LoginController instance;
    Usuario usuario;
    
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
        instance = new LoginController();
        usuario = new Usuario();
        instance.setUsuarioLogueado(usuario);
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testEsEmpleadoMunicipal() {
        System.out.println("esEmpleadoMunicipal");
        Rol rol = new Rol();
        rol.setNombres("Empleado Municipal");
        instance.getUsuarioLogueado().setRol(rol);
        boolean expResult = true;
        boolean result = instance.esEmpleadoMunicipal();
        assertEquals(expResult, result);
    }

    /**
     * Test of esFuncionario method, of class LoginController.
     */
    @Test
    public void testEsFuncionario() {
        System.out.println("esFuncionarioCESFAM");
        Rol rol = new Rol();
        long id = 2;
        rol.setId(id);
        rol.setNombres("Funcionario CESFAM");
        instance.getUsuarioLogueado().setRol(rol);
        boolean expResult = true;
        boolean result = instance.esFuncionario();
        assertEquals(expResult, result);
    }

    /**
     * Test of esSuperUsuario method, of class LoginController.
     */
    @Test
    public void testEsSuperUsuario() {
        System.out.println("esSuperUsuario");
        Rol rol = new Rol();
        rol.setNombres("Super Usuario");
        instance.getUsuarioLogueado().setRol(rol);
        boolean expResult = true;
        boolean result = instance.esSuperUsuario();
        assertEquals(expResult, result);
    }
}
