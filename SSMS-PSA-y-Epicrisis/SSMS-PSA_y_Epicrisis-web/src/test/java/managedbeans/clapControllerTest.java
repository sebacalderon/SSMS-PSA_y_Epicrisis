/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbeans;

import entities.audit;
import entities.clap;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
public class clapControllerTest {
    
    public clapControllerTest() {
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
     * Test of calculoEdad method, of class clapController.
     */
    @Test
    public void testCalculoEdad() throws ParseException {
        SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd-MM-yyyy");
        String strFecha = "10-09-1992";
        Date fecha_nacimiento = formatoDelTexto.parse(strFecha);
        clapController instance = new clapController();
        int expResult = 24;
        int result = instance.calculoEdad(fecha_nacimiento);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of serviceChangeA method, of class clapController.
     */
    @Test
    public void testServiceChangeTrueA() {
        boolean resp = true;
        clapController instance = new clapController();
        instance.setPuntajeACrafft(2);
        instance.serviceChangeA(resp);
        assertEquals(1, instance.getPuntajeACrafft());
    }

    @Test
    public void testServiceChangeFalseA() {
        boolean resp = false;
        clapController instance = new clapController();
        instance.setPuntajeACrafft(2);
        instance.serviceChangeA(resp);
        assertEquals(3, instance.getPuntajeACrafft());
    }
    
    /**
     * Test of tipoIntervencion method, of class clapController.
     */
    @Test
    public void testTipoIntervencionMinima() {
        clapController instance = new clapController();
        audit audit = new audit();
        audit.setP1(7);
        instance.setAudit(audit);
        int expResult = 0;
        int result = instance.tipoIntervencion();
        assertEquals(expResult, result);
    }

    @Test
    public void testTipoIntervencionBreve() {
        clapController instance = new clapController();
        audit audit = new audit();
        audit.setP1(15);
        instance.setAudit(audit);
        int expResult = 1;
        int result = instance.tipoIntervencion();
        assertEquals(expResult, result);
    }
    
    @Test
    public void testTipoIntervencionAsistida() {
        clapController instance = new clapController();
        audit audit = new audit();
        audit.setP1(20);
        instance.setAudit(audit);
        int expResult = 2;
        int result = instance.tipoIntervencion();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of esIntervencionMinima method, of class clapController.
     */
    @Test
    public void testEsIntervencionMinimaHombre() {
        clapController instance = new clapController();
        clap clap = new clap();
        audit audit = new audit();
        audit.setP1(4);
        instance.setAudit(audit);
        clap.setSexo(1);
        instance.setSelected(clap);
        boolean expResult = true;
        boolean result = instance.esIntervencionMinima();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }
    
    @Test
    public void testEsIntervencionMinimaMujer() {
        clapController instance = new clapController();
        clap clap = new clap();
        audit audit = new audit();
        audit.setP1(3);
        instance.setAudit(audit);
        clap.setSexo(0);
        instance.setSelected(clap);
        boolean expResult = true;
        boolean result = instance.esIntervencionMinima();
        assertEquals(expResult, result);
    }
    
    @Test
    public void testNoEsIntervencionMinimaHombre() {
        clapController instance = new clapController();
        clap clap = new clap();
        audit audit = new audit();
        audit.setP1(5);
        instance.setAudit(audit);
        clap.setSexo(1);
        instance.setSelected(clap);
        boolean expResult = false;
        boolean result = instance.esIntervencionMinima();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }
    
    @Test
    public void testNoEsIntervencionMinimaMujer() {
        clapController instance = new clapController();
        clap clap = new clap();
        audit audit = new audit();
        audit.setP1(4);
        instance.setAudit(audit);
        clap.setSexo(0);
        instance.setSelected(clap);
        boolean expResult = false;
        boolean result = instance.esIntervencionMinima();
        assertEquals(expResult, result);
    }
}
