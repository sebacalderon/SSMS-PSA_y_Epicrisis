package managedbeans;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import entities.Crafft;
import entities.audit;
import entities.cesfam;
import entities.clap;
import entities.comuna;
import entities.paciente;
import entities.parametros;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import managedbeans.util.JsfUtil;
import managedbeans.util.JsfUtil.PersistAction;
import sessionbeans.clapFacadeLocal;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

@Named("clapController")
@SessionScoped
public class clapController implements Serializable {

    @EJB
    private sessionbeans.clapFacadeLocal ejbFacade;
    private List<clap> items = null;
    private List<clap> itemsPorPaciente = null;
    private List<clap> itemsNoTerminados = null;
    private clap selected;
    private paciente Paciente = null;
    private boolean auditCrafft = false;
    private boolean isAudit = false;
    private boolean isCrafft = false;
    private int puntajeACrafft;
    private UploadedFile imagen = null;
    private StreamedContent chart;
    boolean vive_solo=false;
    boolean vive_en_institucion=false;
    boolean vive_con_madre=false;
    boolean vive_con_padre=false;
    boolean vive_con_otros=false;
    
    boolean actividadElegida=false;

    private int minutos = 10;
    
    @Inject
    private pacienteController pacienteCtrl;
    
    @Inject
    private LoginController loginCtrl;  
    
    @Inject
    private auditController auditCtrl;
    
    @Inject
    private CrafftController crafftCtrl;
    
    @Inject
    private parametrosController parametrosCtrl;
    
    public clapController() {
    }

    public StreamedContent getChart() {
        return chart;
    }

    public void setChart(StreamedContent chart) {
        this.chart = chart;
    }
    
    public int getPuntajeACrafft() {
        return puntajeACrafft;
    }

    public void setPuntajeACrafft(int puntajeACrafft) {
        this.puntajeACrafft = puntajeACrafft;
    }

    public UploadedFile getImagen() {
        return imagen;
    }

    public void setImagen(UploadedFile imagen) {
        this.imagen = imagen;
    }

    public boolean isIsAudit() {
        return isAudit;
    }

    public void setIsAudit(boolean isAudit) {
        this.isAudit = isAudit;
    }

    public List<clap> getItemsPorPaciente(int RUN) {
        itemsPorPaciente = getFacade().findbyPaciente(RUN);
        Date fecha = new java.util.Date();
        Calendar c = Calendar.getInstance();
        c.setTime(fecha);
        c.add(Calendar.DATE, -60);
        fecha = c.getTime();
        List<clap> itemsIncompletos = getFacade().findbyPacienteEstado(RUN, "Incompleto");
        for (clap clap : itemsIncompletos) {
            if (clap.getFecha_consulta().before(fecha)) {
                clap.setEstado("Anulado");
                selected = clap;
                persist(PersistAction.UPDATE,"");
            }
        }
        return itemsPorPaciente;
    }
    
    public List<clap> getItemsPorEstado(int num, cesfam cesfam) {
        //Si es super usuario, no hay filtro por cesfam
        Date fecha = new java.util.Date();
        Calendar c = Calendar.getInstance();
        c.setTime(fecha);
        c.add(Calendar.DATE, -60);
        fecha = c.getTime();
        
        if (loginCtrl.esSuperUsuario()) {
            // 1 = Incompleto
            if (num == 1) {
               itemsNoTerminados = getFacade().findbyEstadoFecha("Incompleto", fecha);
               return itemsNoTerminados;
           }else{
               return itemsNoTerminados;
           }   
        }else{
            if (num == 1) {
                itemsNoTerminados = getFacade().findbyEstadoCesfamFecha("Incompleto", loginCtrl.getUsuarioLogueado().getCESFAM(),fecha);
                return itemsNoTerminados;
            }else{
                return itemsNoTerminados;
            }
        }
    }

    public void setItemsPorPaciente(List<clap> itemsPorPaciente) {
        this.itemsPorPaciente = itemsPorPaciente;
    }
    
    public boolean isIsCrafft() {
        return isCrafft;
    }

    public void setIsCrafft(boolean isCrafft) {
        this.isCrafft = isCrafft;
    }
    
    public paciente getPaciente() {
        return Paciente;
    }

    public void setPaciente(paciente Paciente) {
        this.Paciente = Paciente;
    }
    
    public void upload(FileUploadEvent event) {
        UploadedFile uploadedFile = event.getFile();
        setImagen(uploadedFile);
    }

    public boolean isAuditCrafft() {
        return auditCrafft;
    }
    
    public void setAuditCrafft(boolean auditCrafft) {
        this.auditCrafft = auditCrafft;
    }
    
    public clap getSelected() {
        return selected;
    }

    public int getMinutos() {
        return minutos;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }
    
    public void setSelected(clap selected) {
        vive_solo=false;
        vive_en_institucion=false;
        vive_con_madre=false;
        vive_con_padre=false;
        vive_con_otros=false;
        actividadElegida=false;
        this.selected = selected;
    }
    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private clapFacadeLocal getFacade() {
        return ejbFacade;
    }

    public clap prepareCreate() {
        selected = new clap();
        selected.setRiesgo_cardiovascular(false);
        selected.setRiesgo_nutricional(false);
        selected.setRiesgo_oh_drogas(false);
        selected.setRiesgo_salud_mental(false);
        selected.setRiesgo_social(false);
        selected.setRiesgo_ssr(false);
        selected.setEstado("Incompleto");
        auditCrafft = false;
        initializeEmbeddableKey();
        return selected;
    }
    
    public clap prepareEdit() throws FileNotFoundException{
        selected = getSelected();
        if (selected.getAudit()==null) {
            isAudit = false;
        }else{
            isAudit = true;
        }
        if (selected.getCrafft()==null) {
            isCrafft = false;
        }else{
            isCrafft = true;
            puntajeACrafft = 0;
            if (selected.getCrafft().isA1()) {
                puntajeACrafft +=1;
            }
            if (selected.getCrafft().isA2()) {
                puntajeACrafft +=1;
            }
            if (selected.getCrafft().isA3()) {
                puntajeACrafft +=1;
            }
             
        }
        if (!isAudit && !isCrafft) {
            auditCrafft = false;
        }else{
            auditCrafft = true;
        }
        return selected;
    }
    
    public clap prepareRiesgos(){
        selected = getSelected();
        if (selected.getEstado().equals("Vigente") && pacienteCtrl.getSelected().getEstado().equals("Riesgos no Tratados")) {
            setActividadElegida(false);
        }else{
            setActividadElegida(true);
        }
        return selected;
    }
    
    public StreamedContent cargarImagen() throws FileNotFoundException{
        File chartFile = new File(selected.getDiagrama_familiar());
        setChart(new DefaultStreamedContent(new FileInputStream(chartFile), "image/png"));
        return chart;
    }

    public void generarCSV()
    {
        String title = "sabana_de_datos.csv";
        try
        {
            
            FacesContext fc = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();

            response.reset();
            response.setContentType("text/comma-separated-values");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + title + "\"");
            
            OutputStream output = response.getOutputStream();
            
            items=getItems();
            //Nombre de las columnas
            output.write("Número de CLAP,".getBytes());
            output.write("RUN,".getBytes());
            output.write("Nombres,".getBytes());
            output.write("Primer apellido,".getBytes());
            output.write("Segundo apellido,".getBytes());
            output.write("Nombre social,".getBytes());
            output.write("Telefono fijo,".getBytes());
            output.write("Domicilio,".getBytes());
            output.write("Telefono movil,".getBytes());
            output.write("Recados,".getBytes());
            output.write("Region de residencia,".getBytes());
            output.write("Comuna de residencia,".getBytes());
            output.write("Calle dirección,".getBytes());
            output.write("Número direccion,".getBytes());
            output.write("Resto direccion,".getBytes());
            output.write("Fecha de nacimiento,".getBytes());
            output.write("Sexo,".getBytes());
            output.write("Nacionalidad,".getBytes());
            output.write("Correo,".getBytes());
            output.write("Programa social,".getBytes());
            output.write("Prevision,".getBytes());
            output.write("Grupo de FONASA,".getBytes());
            output.write("Estado conyugal,".getBytes());
            output.write("Pueblo originario,".getBytes());
            output.write("CESFAM,".getBytes());
            output.write("Lugar de la consulta,".getBytes());
            output.write("H.C.N,".getBytes());
            output.write("Establecimiento educacional,".getBytes());
            output.write("Fecha consulta,".getBytes());
            output.write("Edad,".getBytes());
            output.write("Acompañante,".getBytes());
            output.write("Motivo de consulta segun adolescente 1,".getBytes());
            output.write("Motivo de consulta segun adolescente 2,".getBytes());
            output.write("Motivo de consulta segun adolescente 3,".getBytes());
            output.write("Motivo de consulta segun acompanante 1,".getBytes());
            output.write("Motivo de consulta segun acompanante 2,".getBytes());
            output.write("Motivo de consulta segun acompanante 3,".getBytes());
            output.write("Descripción motivo de consulta,".getBytes());
            
            output.write("Perinatales normales,".getBytes());
            output.write("Alergias normales,".getBytes());
            output.write("Vacunas completas,".getBytes());
            output.write("Enfermedades importantes,".getBytes());
            output.write("Discapacidad,".getBytes());
            output.write("Accidentes relevantes,".getBytes());
            output.write("Cirugia u hospitalizaciones,".getBytes());
            output.write("Uso medicamentos,".getBytes());
            output.write("Problemas de salud mental,".getBytes());
            output.write("Violencia,".getBytes());
            output.write("Antecedentes judiales,".getBytes());
            output.write("Otros antecedentes personales,".getBytes());
            output.write("Observaciones antecedentes personales,".getBytes());

            output.write("Enfermedades importantes familia,".getBytes());
            output.write("Obesidad familia,".getBytes());
            output.write("Problemas salud mental familia,".getBytes());
            output.write("Violencia intrafamiliar,".getBytes());
            output.write("Alcohol y otras drogas,".getBytes());
            output.write("Padre adolescente,".getBytes());
            output.write("Judiciales,".getBytes());
            output.write("Otros antecedentes familiares,".getBytes());
            output.write("Observaciones antecedentes familiares,".getBytes());
            
            output.write("Vive solo,".getBytes());
            output.write("Vive con madre,".getBytes());
            output.write("Vive con padre,".getBytes());
            output.write("Vive en institucion,".getBytes());
            output.write("Vive con otros,".getBytes());
            output.write("Especificación con quien vive,".getBytes());
            output.write("Comparte cama,".getBytes());
            output.write("Especificacion comparte cama,".getBytes());
            output.write("Nivel de instruccion madre,".getBytes());
            output.write("Nivel de instruccion padre,".getBytes());
            output.write("Nivel de instruccion pareja,".getBytes());
            output.write("Ocupacion madre,".getBytes());
            output.write("Ocupacion padre,".getBytes());
            output.write("Ocupacion pareja,".getBytes());
            output.write("Percepcion familia,".getBytes());
            output.write("Observaciones familia,".getBytes());
            
            output.write("Condiciones sanitarias,".getBytes());
            output.write("Hacinamiento,".getBytes());
            output.write("Observaciones vivienda,".getBytes());

            output.write("Estudia,".getBytes());
            output.write("Nivel de educacion,".getBytes());
            output.write("Curso,".getBytes());
            output.write("Años repetidos,".getBytes());
            output.write("Causa de los años repetidos,".getBytes());
            output.write("Problemas en la escuela,".getBytes());
            output.write("Violencia escolar,".getBytes());
            output.write("Desercion o exclusión,".getBytes());
            output.write("Causa desercion exclusion,".getBytes());
            output.write("Percepcion rendimiento,".getBytes());
            output.write("Observaciones educacion,".getBytes());
            
            output.write("Trabaja,".getBytes());
            output.write("Horas de trabajo,".getBytes());
            output.write("Trabajo infantil,".getBytes());
            output.write("Trabajo juvenil,".getBytes());
            output.write("Peores formas,".getBytes());
            output.write("Servicio domestico no remunerado peligroso,".getBytes());
            output.write("Razon de trabajo,".getBytes());
            output.write("Legalizado,".getBytes());
            output.write("Tipo de trabajo,".getBytes());
            output.write("Observaciones trabajo,".getBytes());
            
            output.write("Aceptacion,".getBytes());
            output.write("Pareja,".getBytes());
            output.write("Edad pareja,".getBytes());
            output.write("Violencia en la pareja,".getBytes());
            output.write("Amigos,".getBytes());
            output.write("Suicidalidad amigos,".getBytes());
            output.write("Horas de actividad fisica,".getBytes());
            output.write("Horas de TV,".getBytes());
            output.write("Horas de computador o consola,".getBytes());
            output.write("Cyberbulling,".getBytes());
            output.write("Grooming,".getBytes());
            output.write("Otras actividades,".getBytes());
            output.write("Especificacion de otras actividades,".getBytes());
            output.write("Observaciones de vida social,".getBytes());
            
            output.write("Sueño normal,".getBytes());
            output.write("Horas de sueño,".getBytes());
            output.write("Alimentacion adecuada,".getBytes());
            output.write("Comidas familia,".getBytes());
            output.write("Alimentacion especial,".getBytes());
            output.write("Especificacion de alimentacion especial,".getBytes());
            output.write("Tabaco,".getBytes());
            output.write("Cigarros al dia,".getBytes());
            output.write("Consumo de alcohol,".getBytes());
            output.write("Consumo de marihuana,".getBytes());
            output.write("Consumo de otra suscancia,".getBytes());
            output.write("Especificacion consumo otra sustancia,".getBytes());
            output.write("Seguridad vial,".getBytes());
            output.write("Observaciones de habitos y consumo,".getBytes());
            
            output.write("Edad menarca o espermarca,".getBytes());
            output.write("Fecha de ultima menstruacion,".getBytes());
            output.write("No conoce fecha ultima menstruacion,".getBytes());
            output.write("Ciclos regulares,".getBytes());
            output.write("Dismenorrea,".getBytes());
            output.write("Flujo secrecion patologico,".getBytes());
            output.write("ITS o VIH,".getBytes());
            output.write("Especificacion de ITS o VIH,".getBytes());
            output.write("Tratamiento,".getBytes());
            output.write("Embarazos,".getBytes());
            output.write("Hijos,".getBytes());
            output.write("Abortos,".getBytes());
            output.write("Observaciones gineco urologico,".getBytes());
            
            output.write("Orientacion sexual,".getBytes());
            output.write("Especificacion de orientacion sexual,".getBytes());
            output.write("Conducta sexual,".getBytes());
            output.write("Edad de inicio de conducta sexual,".getBytes());
            output.write("Relaciones sexuales,".getBytes());
            output.write("Pareja sexual,".getBytes());
            output.write("Dificultades sexuales,".getBytes());
            output.write("Anticoncepcion,".getBytes());
            output.write("Doble proteccion,".getBytes());
            output.write("Especificacion uso MAC,".getBytes());
            output.write("Uso MAC,".getBytes());
            output.write("Especificacion de uso MAC,".getBytes());
            output.write("Razon de no uso MAC,".getBytes());
            output.write("Consejeria de uso MAC,".getBytes());
            output.write("ACO de emergencia,".getBytes());
            output.write("Violencia sexual,".getBytes());
            output.write("Reparacion de abuso sexual,".getBytes());
            output.write("Observaciones sexualidad,".getBytes());
            
            output.write("Imagen corporal,".getBytes());
            output.write("Bienestar emocional,".getBytes());
            output.write("Vida proyecto,".getBytes());
            output.write("Suicidalidad en amigos,".getBytes());
            output.write("Ideacion suicida,".getBytes());
            output.write("Intento suicida,".getBytes());
            output.write("Referente adulto,".getBytes());
            output.write("Nombre referente adulto,".getBytes());
            output.write("Telefono referente adulto,".getBytes());
            output.write("Observacion psico emocional,".getBytes());
            
            output.write("Peso,".getBytes());
            output.write("DE peso,".getBytes());
            output.write("Talla,".getBytes());
            output.write("De talla,".getBytes());
            output.write("Perimetro abdominal,".getBytes());
            output.write("IMC,".getBytes());
            output.write("DE IMC,".getBytes());
            output.write("Presion arterial sistolica,".getBytes());
            output.write("Presion arterial diastolica,".getBytes());
            output.write("Aspecto general,".getBytes());
            output.write("Agudeza visual,".getBytes());
            output.write("Agudeza auditiva,".getBytes());
            output.write("Salud bucal,".getBytes());
            output.write("Tiroides,".getBytes());
            output.write("Cardio pulomones,".getBytes());
            output.write("Abdomen,".getBytes());
            output.write("Columna,".getBytes());
            output.write("Extremidades,".getBytes());
            output.write("Tanner con foto,".getBytes());
            output.write("Tanner mama,".getBytes());
            output.write("Tanner genital,".getBytes());
            output.write("Observaciones examen fisico,".getBytes());
            
            output.write("Impresion diagnostica,".getBytes());
            output.write("Indicaciones interconsultas,".getBytes());
            output.write("Funcionario,".getBytes());
            output.write("Riesgo cardiovascular,".getBytes());
            output.write("Riesgo SSR,".getBytes());
            output.write("Riesgo salud mental,".getBytes());
            output.write("Riesgo alcohol y drogas,".getBytes());
            output.write("Riesgo nutricional,".getBytes());
            output.write("Riesgo social\n".getBytes());
            
            //claps
            for (int i =0;i<items.size();i++) {
                clap item=items.get(i);
                output.write(item.toString().getBytes());
                output.write("\n".getBytes());
            }

            output.flush();
            output.close();
            
            fc.responseComplete();

            
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    public String create() {
        selected = new clap();
        initializeEmbeddableKey();
        Paciente = pacienteCtrl.getSelected();
        selected.setAudit(null);
        selected.setCrafft(null);
        selected.setPaciente(Paciente);
        selected.setRUN(Paciente.getRUN());
        selected.setDV(Paciente.getDV());
        selected.setNombres(Paciente.getNombres());
        selected.setNombre_social(Paciente.getNombre_social());
        selected.setPrimer_apellido(Paciente.getPrimer_apellido());
        selected.setSegundo_apellido(Paciente.getSegundo_apellido());
        selected.setTelefono_fijo(Paciente.getTelefono_fijo());
        selected.setTelefono_movil(Paciente.getTelefono_movil());
        selected.setRegion_residencia(Paciente.getRegion_residencia());
        selected.setComuna_residencia(Paciente.getComuna_residencia());
        selected.setCalle_direccion(Paciente.getCalle_direccion());
        selected.setNumero_direccion(Paciente.getNumero_direccion());
        selected.setResto_direccion(Paciente.getResto_direccion());
        selected.setFecha_nacimiento(Paciente.getFecha_nacimiento());
        selected.setCesfam(Paciente.getCesfam());
        selected.setSexo(Paciente.getSexo());
        selected.setNacionalidad(Paciente.getNacionalidad());
        selected.setCorreo(Paciente.getCorreo());
        selected.setPrograma_social(Paciente.getPrograma_social());
        selected.setPrevision(Paciente.getPrevision());
        selected.setGrupo_fonasa(Paciente.getGrupo_fonasa());
        selected.setEstado_conyugal(Paciente.getEstado_conyugal());
        selected.setPueblo_originario(Paciente.getPueblo_originario());
        selected.setFecha_consulta(new java.util.Date());
        selected.setEdad(selected.getFecha_consulta().getYear()-Paciente.getFecha_nacimiento().getYear());
        selected.setRecados(Paciente.isRecados());
        selected.setDomicilio(Paciente.isDomicilio());
        puntajeACrafft = 0;
        isAudit = false;
        isCrafft = false;
        auditCrafft = false;
        selected.setFuncionario(loginCtrl.getUsuarioLogueado());
        selected.setEstado("Incompleto");        
        selected.setRiesgo_cardiovascular(false);
        selected.setRiesgo_nutricional(false);
        selected.setRiesgo_oh_drogas(false);
        selected.setRiesgo_salud_mental(false);
        selected.setRiesgo_social(false);
        selected.setRiesgo_ssr(false);
         
        //Guarda localmente el clap selected
        clap nuevoClap = getSelected();

        setSelected(nuevoClap);
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("clapCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
        
        int size = getFacade().findbyPaciente(Paciente.getRUN()).size();
        selected = getFacade().findbyPaciente(Paciente.getRUN()).get(size-1);
        return "/faces/clap/edit/usuario.xhtml";
        
    }

    public void setIMC(){
        if(selected.getTalla()!= 0 && selected.getPeso()!=0){
            selected.setImc((float) (selected.getPeso()/Math.pow((float)selected.getTalla()/100,2)));
        }
        
    }

    public boolean isActividadElegida() {
        return actividadElegida;
    }

    public void setActividadElegida(boolean actividadElegida) {
        this.actividadElegida = actividadElegida;
    }
    
    public boolean esIncompleto(){
        if(selected!=null && selected.getId()!=null){
            return selected.getEstado().equals("Incompleto");
        }
        return true;
    }
    
    public boolean esAnulado(){
        if(selected!=null && selected.getId()!=null){
            return selected.getEstado().equals("Anulado");
        }
        return true;
    }
    
    public void setViveCon(){
        if(selected.isVive_con_madre()&&vive_con_madre==false){
            selected.setVive_con_solo(false);
            selected.setVive_en_institucion(false);
        }
        else if(selected.isVive_con_padre()&&vive_con_padre==false){
            selected.setVive_con_solo(false);
            selected.setVive_en_institucion(false);
        }
        else if(selected.isVive_con_otros()&&vive_con_otros==false){
            selected.setVive_con_solo(false);
            selected.setVive_en_institucion(false);
        }
        else if(selected.isVive_en_institucion()&&vive_en_institucion==false){
            selected.setVive_con_madre(false);
            selected.setVive_con_padre(false);
            selected.setVive_con_solo(false);
            selected.setVive_con_otros(false);
        }
        else if(selected.isVive_con_solo()&&vive_solo==false){
            selected.setVive_con_madre(false);
            selected.setVive_con_otros(false);
            selected.setVive_con_padre(false);
            selected.setVive_en_institucion(false);
        }
        vive_con_madre=selected.isVive_con_madre();
        vive_solo=selected.isVive_con_solo();
        vive_con_padre=selected.isVive_con_padre();
        vive_en_institucion=selected.isVive_en_institucion();
        vive_con_otros=selected.isVive_con_otros();
        
    }
    
    public String update() throws IOException {
        boolean completo=false;
        boolean parametro_imagen = true;
        List<parametros> parametrosLista = parametrosCtrl.getItems();
        parametros parametros;
        String ruta;
        if (parametrosLista.isEmpty()) {
            JsfUtil.addErrorMessage("No existe una ruta para guardar la imagen. Contacte al administrador");
            parametro_imagen = false;
        }
        //Verifica si completa el clap
        if( selected.getPerinatales_normales()!=0&&
            selected.getAlergias_normales()!=0&&
            selected.getVacunas_completas()!=0&&
            selected.getEnfermedades_importantes()!=0&&
            selected.getDiscapacidad()!=0&&
            selected.getAccidentes_relevantes()!=0&&
            selected.getCirugia_hospitalizaciones()!=0&&
            selected.getProblemas_salud_mental()!=0&&
            selected.getViolencia()!=0&&
            selected.getAntecedentes_judiciales()!=0&&
            selected.getOtros()!=0&&
            selected.getEnfermedades_importantes_familia()!=0&&
            selected.getObesidad_familia()!=0&&
            selected.getProblemas_salud_mental_familia()!=0&&
            selected.getViolencia_intrafamiliar()!=0&&
            selected.getAlcohol_y_otras_drogas()!=0&&
            selected.getPadre_adolescente()!=0&&
            selected.getJudiciales()!=0&&
            selected.getOtros_antecedentes_familiares()!=0&&
            selected.getPercepcion_familia()!=0&&
            selected.getNivel_educacion()!=0&&
            !selected.getCurso().equals("")&&
            selected.getPercepcion_rendimiento()!=0&&
            selected.getAceptacion()!=0&&
            selected.getHoras_actividad_fisica()!=0&&
            selected.getHoras_tv()!=0&&
            selected.getHoras_computador_consola()!=0&&
            selected.getHoras_sueno()!=0 &&
            selected.getEdad_menarca_espermarca()!=0&&
            selected.getCiclos_regulares()!=0&&
            selected.getDismenorrea()!=0&&
            selected.getOrientacion_sexual()!=0&&
            selected.getConducta_sexual()!=0&&
            selected.getRelaciones_sexuales()!=0&&
            selected.getPareja_sexual()!=0&&
            selected.getDificultades_sexuales()!=0&&
            selected.getAnticoncepcion()!=0&&
            selected.getUso_mac()!=0&&
            selected.getImagen_corporal()!=0&&
            selected.getBienestar_emocional()!=0&&
            selected.getVida_proyecto()!=0&&
            selected.getReferente_adulto()!=0&&
            selected.getPeso()!=0&&
            selected.getTalla()!=0&&
            selected.getPresion_arterial_diastolica()!=0&&
            selected.getPresion_arterial_sistolica()!=0&&
            selected.getPerimetro_abdominal()!=0&&
            selected.getTanner_mama()!=0&&
            selected.getTanner_genital()!=0 &&
            parametro_imagen==true &&
            imagen!=null
            ){
            selected.setEstado("Nuevo");
            completo=true;
        }else{
            selected.setEstado("Incompleto");
            completo=false;
        }
        
        if(selected.getTalla()!= 0 && selected.getPeso()!=0){
            selected.setImc((float) (selected.getPeso()/Math.pow((float)selected.getTalla()/100,2)));
        }
//        System.out.println(
//        (selected.getPerinatales_normales()!=0)+"\n"+
//            (selected.getAlergias_normales()!=0)+"\n"+
//            (selected.getVacunas_completas()!=0)+"\n"+
//            (selected.getEnfermedades_importantes()!=0)+"\n"+
//            (selected.getDiscapacidad()!=0)+"\n"+
//            (selected.getAccidentes_relevantes()!=0)+"\n"+
//            (selected.getCirugia_hospitalizaciones()!=0)+"\n"+
//            (selected.getProblemas_salud_mental()!=0)+"\n"+
//            (selected.getViolencia()!=0)+"\n"+
//            (selected.getAntecedentes_judiciales()!=0)+"\n"+
//            (selected.getOtros()!=0)+"\n"+
//            (selected.getEnfermedades_importantes_familia()!=0)+"\n"+
//            (selected.getObesidad_familia()!=0)+"\n"+
//            (selected.getProblemas_salud_mental_familia()!=0)+"\n"+
//            (selected.getViolencia_intrafamiliar()!=0)+"\n"+
//            (selected.getAlcohol_y_otras_drogas()!=0)+"\n"+
//            (selected.getPadre_adolescente()!=0)+"\n"+
//            (selected.getJudiciales()!=0)+"\n"+
//            (selected.getOtros_antecedentes_familiares()!=0)+"\n"+
//            (selected.getPercepcion_familia()!=0)+"\n"+
//            (selected.getNivel_educacion()!=0)+"\n"+
//            (!selected.getCurso().equals(""))+"\n"+
//            (selected.getPercepcion_rendimiento()!=0)+"\n"+
//            (selected.getAceptacion()!=0)+"\n"+
//            (selected.getHoras_actividad_fisica()!=0)+"\n"+
//            (selected.getHoras_tv()!=0)+"\n"+
//            (selected.getHoras_computador_consola()!=0)+"\n"+
//            (selected.getHoras_sueno()!=1)+"\n"+
//            (selected.getEdad_menarca_espermarca()!=0)+"\n"+
//            (selected.getCiclos_regulares()!=0)+"\n"+
//            (selected.getDismenorrea()!=0)+"\n"+
//            (selected.getOrientacion_sexual()!=0)+"\n"+
//            (selected.getConducta_sexual()!=0)+"\n"+
//            (selected.getRelaciones_sexuales()!=0)+"\n"+
//            (selected.getPareja_sexual()!=0)+"\n"+
//            (selected.getDificultades_sexuales()!=0)+"\n"+
//            (selected.getAnticoncepcion()!=0)+"\n"+
//            (selected.getUso_mac()!=0)+"\n"+
//            (selected.getImagen_corporal()!=0)+"\n"+
//            (selected.getBienestar_emocional()!=0)+"\n"+
//            (selected.getVida_proyecto()!=0)+"\n"+
//            (selected.getReferente_adulto()!=0)+"\n"+
//            (selected.getPeso()!=0)+"\n"+
//            (selected.getTalla()!=0)+"\n"+
//            (selected.getPresion_arterial_diastolica()!=0)+"\n"+
//            (selected.getPresion_arterial_sistolica()!=0)+"\n"+
//            (selected.getPerimetro_abdominal()!=0)+"\n"+
//            (selected.getTanner_mama()!=0)+"\n"+
//            (selected.getTanner_genital()!=0)
     //   );
     
        //Riesgos
        
        selected.setRiesgo_cardiovascular(false);
        selected.setRiesgo_nutricional(false);
        selected.setRiesgo_oh_drogas(false);
        selected.setRiesgo_salud_mental(false);
        selected.setRiesgo_social(false);
        selected.setRiesgo_ssr(false);
        
        //cardiovascular nutricional
        if(selected.getImc()<24){
            selected.setRiesgo_cardiovascular(true);
            selected.setRiesgo_nutricional(true);
        }
        if(selected.isCardio_pulmonar()){
            selected.setRiesgo_cardiovascular(true);
        }
        if(selected.getPresion_arterial_sistolica()>=120||selected.getPresion_arterial_diastolica()>=80){
            selected.setRiesgo_cardiovascular(true);
        }
        if(selected.getPerimetro_abdominal()>88&&selected.getSexo()==2||selected.getPerimetro_abdominal()>102&&selected.getSexo()==3){
            selected.setRiesgo_cardiovascular(true);
            selected.setRiesgo_nutricional(true);
        }
        if(selected.isAlimentacion_adecuada()){
            selected.setRiesgo_nutricional(true);
        }
        //ssr
        if(selected.getConducta_sexual()==3||selected.getEdad_inicio_conducta_sexual()< 14){
            selected.setRiesgo_ssr(true);
        }
        if(selected.getDificultades_sexuales()==2){
            selected.setRiesgo_ssr(true);
        }
        if(selected.getCiclos_regulares()==2||selected.getDismenorrea()==2||selected.isFlujo_secrecion_patologico()){
            selected.setRiesgo_ssr(true);
        }
        if(selected.isIts_vih()||selected.getTratamiento()>1||selected.getTratamiento_contactos()>1){
            selected.setRiesgo_ssr(true);
        }
        if(selected.getEmbarazos()>0||selected.getHijos()>0||selected.getAbortos()>0){
            selected.setRiesgo_ssr(true);
        }
        if(selected.getUso_mac()>1||selected.getAnticoncepcion()>1||selected.isAbuso_sexual()){
            selected.setRiesgo_ssr(true);
        }
        
        //Salud mental
        
        if(selected.getImagen_corporal()>1||selected.getBienestar_emocional()>1){
            selected.setRiesgo_salud_mental(true);
        }
        if(selected.getVida_proyecto()==3||selected.isIntento_suicida()||selected.isIdeacion_suicida()){
            selected.setRiesgo_salud_mental(true);
        }
        
        //Drogas
        
        if(selected.isTabaco()||selected.isConsumo_alcohol()||selected.isConsumo_marihuana()||selected.isConsumo_otra_sustancia()){
            selected.setRiesgo_oh_drogas(true);
        }
        
        //riesgo social
        
        if(selected.getReferente_adulto()==5||selected.getAceptacion()==2||selected.getAceptacion()==3||selected.isAmigos()==false||selected.isSuicidalidad_amigos()==true){
            selected.setRiesgo_social(true);
        }
        if(selected.isCyberbulling()|selected.isGrooming()||selected.isViolencia_escolar()||selected.isViolencia_pareja()){
            selected.setRiesgo_social(true);
        }
        if(selected.isVive_con_solo()||selected.isVive_en_institucion()||selected.getPercepcion_familia()>2||selected.isDesercion_exclusion()){
            selected.setRiesgo_social(true);
        }
//        System.out.println("Riesgo cardiovascular: "+selected.isRiesgo_cardiovascular());
//        System.out.println("Riesgo nutricional: "+selected.isRiesgo_nutricional());
//        System.out.println("Riesgo OH drogas: "+selected.isRiesgo_oh_drogas());
//        System.out.println("Riesgo salud mental: "+selected.isRiesgo_salud_mental());
//        System.out.println("Riesgo social: "+selected.isRiesgo_social());
//        System.out.println("Riesgo ssr: "+selected.isRiesgo_ssr());
//        

        if(completo){
            if(selected.isRiesgo_cardiovascular()||selected.isRiesgo_nutricional()||selected.isRiesgo_oh_drogas()||selected.isRiesgo_salud_mental()||selected.isRiesgo_social()||selected.isRiesgo_ssr()){
                pacienteCtrl.riesgosNoTratados();
            }else{
                pacienteCtrl.sinRiesgo();
            }
        }else{
            pacienteCtrl.clapIncompleto();
        }
        
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("clapUpdated"));
        if(selected.getEstado().equals("Nuevo")){
            List<clap> claps = getItemsPorPaciente(pacienteCtrl.getSelected().getRUN());
            if(claps.size()>1){
                System.out.println("Eziste mas de 1 clap");
                for(int i=0;i<claps.size();i++){
                    if(claps.get(i).getEstado().equals("Vigente")){
                        setSelected(claps.get(i));
                        selected.setEstado("Antiguo");
                        getFacade().edit(selected);
                        //persist(PersistAction.UPDATE,"");
                    } else if(claps.get(i).getEstado().equals("Nuevo")){
                        setSelected(claps.get(i));
                        selected.setEstado("Vigente");
                        getFacade().edit(selected);
                        //persist(PersistAction.UPDATE,"");
                    }
                }
            }
            else{
                System.out.println("Primer clap ingresado");
                selected.setEstado("Vigente");
                getFacade().edit(selected);
                //persist(PersistAction.UPDATE,"");
            }
        }
       
        if (selected.getAudit()!= null) {
            audit audit = auditCtrl.getItemsPorClap(selected).get(0);
            Long id = auditCtrl.getItemsPorClap(selected).get(0).getId();
            audit = selected.getAudit();
            audit.setId(id);
            auditCtrl.setSelected(audit);
            auditCtrl.update();
        }
        
        if (selected.getCrafft()!= null) {
            Crafft crafft = crafftCtrl.getItemsPorClap(selected).get(0);
            Long id = crafftCtrl.getItemsPorClap(selected).get(0).getId();
            crafft = selected.getCrafft();
            crafft.setId(id);
            crafftCtrl.setSelected(crafft);
            crafftCtrl.update();
        }
        
        List<clap> claps = getItemsPorPaciente(pacienteCtrl.getSelected().getRUN());
        selected = claps.get(claps.size()-1);
        ////////////////
        //Imagen
        ////////////////
        if (imagen!=null && selected.getDiagrama_familiar()==null && parametro_imagen == true){
            parametros = parametrosCtrl.getItems().get(parametrosCtrl.getItems().size()-1);
            ruta = parametros.getRuta();
            Path folder = Paths.get(ruta);
            String filename = "Clap "+selected.getId();
            String extension = FilenameUtils.getExtension(imagen.getFileName());
            Path file = Files.createTempFile(folder, filename + "-", "." + extension);
            
            System.out.println(file.getFileName());

            try (InputStream input = imagen.getInputstream()) {
                Files.copy(input, file, StandardCopyOption.REPLACE_EXISTING);
                selected.setDiagrama_familiar(ruta+"/"+file.getFileName());
                getFacade().edit(selected);
            }
            File img = new File(ruta+"/"+file.getFileName());
            BufferedImage bimg = ImageIO.read(img);
            int type = bimg.getType() == 0? BufferedImage.TYPE_INT_ARGB : bimg.getType();            
            BufferedImage resizeImagePng = resizeImage(bimg, type);
            ImageIO.write(resizeImagePng, "png", new File(ruta+"/"+file.getFileName())); 
        }
        
        if(completo){
            return "/faces/clap/Riesgos.xhtml";
        }else{
            return "/faces/paciente/View.xhtml";
        }
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("clapDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void anular() {
        selected.setEstado("Anulado");
        persist(PersistAction.UPDATE, "Clap Anulado");
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }
    
    public List<clap> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }
    
    public boolean esFONASA(){
        if(selected.getPrevision()!=null){
            return selected.getPrevision().getNombre().equals("FONASA");
        }
        return false;
    }

    public int calculoEdad(java.util.Date fecha_nacimiento){
        int edad=0;
        java.util.Date fecha_actual=new java.util.Date();
        edad=fecha_actual.getYear()-fecha_nacimiento.getYear();
        return edad;
    }
    
    public clap getclap(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<clap> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<clap> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    private BufferedImage resizeImage(BufferedImage bimg, int type) {
        BufferedImage resizedImage = new BufferedImage(310, 184, type);
	Graphics2D g = resizedImage.createGraphics();
	g.drawImage(bimg, 0, 0, 310, 184, null);
	g.dispose();
		
	return resizedImage;
    }

    @FacesConverter(forClass = clap.class)
    public static class clapControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            clapController controller = (clapController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "clapController");
            return controller.getclap(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof clap) {
                clap o = (clap) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), clap.class.getName()});
                return null;
            }
        }

    }
    
    public clap prepareClap(paciente Paciente){
        this.Paciente = Paciente;
        selected = new clap();
        initializeEmbeddableKey();
        selected.setAudit(null);
        selected.setCrafft(null);
        selected.setPaciente(Paciente);
        selected.setRUN(Paciente.getRUN());
        selected.setDV(Paciente.getDV());
        selected.setNombres(Paciente.getNombres());
        selected.setNombre_social(Paciente.getNombre_social());
        selected.setPrimer_apellido(Paciente.getPrimer_apellido());
        selected.setSegundo_apellido(Paciente.getSegundo_apellido());
        selected.setTelefono_fijo(Paciente.getTelefono_fijo());
        selected.setTelefono_movil(Paciente.getTelefono_movil());
        selected.setRegion_residencia(Paciente.getRegion_residencia());
        selected.setComuna_residencia(Paciente.getComuna_residencia());
        selected.setCalle_direccion(Paciente.getCalle_direccion());
        selected.setNumero_direccion(Paciente.getNumero_direccion());
        selected.setResto_direccion(Paciente.getResto_direccion());
        selected.setFecha_nacimiento(Paciente.getFecha_nacimiento());
        selected.setCesfam(Paciente.getCesfam());
        selected.setSexo(Paciente.getSexo());
        selected.setNacionalidad(Paciente.getNacionalidad());
        selected.setCorreo(Paciente.getCorreo());
        selected.setPrograma_social(Paciente.getPrograma_social());
        selected.setPrevision(Paciente.getPrevision());
        selected.setGrupo_fonasa(Paciente.getGrupo_fonasa());
        selected.setEstado_conyugal(Paciente.getEstado_conyugal());
        selected.setPueblo_originario(Paciente.getPueblo_originario());
        selected.setDomicilio(Paciente.isDomicilio());
        selected.setRecados(Paciente.isRecados());
        //Para probar con CLAP de hace mas de 2 meses
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.DATE, -61);
//        Date fecha = cal.getTime();
        selected.setFecha_consulta(new java.util.Date());
        selected.setEdad(selected.getFecha_consulta().getYear()-Paciente.getFecha_nacimiento().getYear());
        puntajeACrafft = 0;
        isAudit = false;
        isCrafft = false;
        auditCrafft = false;
        selected.setRiesgo_cardiovascular(false);
        selected.setRiesgo_nutricional(false);
        selected.setRiesgo_oh_drogas(false);
        selected.setRiesgo_salud_mental(false);
        selected.setRiesgo_social(false);
        selected.setRiesgo_ssr(false);
        selected.setEstado("Incompleto");
        return selected;
    }
    
    public void setDatosPaciente(){
        selected.setDV(selected.getPaciente().getDV());
    }
    
    public List<comuna> completeComuna(String query) {
        List<comuna> allComunas = selected.getRegion_residencia().getComunas();
        List<comuna> filteredComunas = new ArrayList<comuna>();
         
        for (int i = 0; i < allComunas.size(); i++) {
            comuna Comuna = allComunas.get(i);
            if(Comuna.getNombre().toLowerCase().startsWith(query.toLowerCase())) {
                filteredComunas.add(Comuna);
            }
        }
         
        return filteredComunas;
    }
    
    public void AuditCrafft(boolean resp){
        int cont = 0;
        if (selected.isConsumo_alcohol()) {
            cont+=1;
        }
        if (selected.isConsumo_marihuana()) {
            cont+=1;
        }
        if (selected.isConsumo_otra_sustancia()) {
            cont+=1;
        }
        if (resp){
            if (cont == 1) {
                auditCrafft = false;
                isCrafft = false;
                isAudit = false;
                selected.setAudit(null);
                selected.setCrafft(null);
            }
        }else{
            if (auditCrafft == false) {
                if (selected.getEdad() > 9 && selected.getEdad() < 15) {
                    if (selected.getAudit() == null) {
                        audit audit = new audit();
                        audit.setClap(selected);
                        selected.setAudit(audit);
                        selected.setCrafft(null);
                    }
                    isAudit = true;
                    isCrafft = false;
                    auditCrafft = true;
                }
                if (selected.getEdad() > 14 && selected.getEdad() < 20) {
                    if (selected.getCrafft()== null) {
                        Crafft crafft = new Crafft();
                        crafft.setClap(selected);
                        selected.setCrafft(crafft);
                        selected.setAudit(null);
                    }
                    isCrafft = true;
                    isAudit = false;
                    auditCrafft = true;
                }
            }
        }
    }
    
    public void serviceChangeA(boolean resp){
       if (resp) {
            if (puntajeACrafft>=1) {
                puntajeACrafft-=1;
            }
            if (puntajeACrafft == 0) {
               selected.getCrafft().setB1(false);
               selected.getCrafft().setB2(false);
               selected.getCrafft().setB3(false);
               selected.getCrafft().setB4(false);
               selected.getCrafft().setB5(false);
               selected.getCrafft().setB6(false);
           }
        }else{
             puntajeACrafft+=1;
        }
    }
    
    public int tipoIntervencion(){
        int puntaje = calculaPuntaje();
        if (puntaje <= 7) {
            return 0;
        }else if (puntaje >= 8 && puntaje <= 15){
            return 1;
        }else{
            return 2;
        }
    }
    
    private int calculaPuntaje() {
        return selected.getAudit().getP1()+selected.getAudit().getP2()+selected.getAudit().getP3()+selected.getAudit().getP4()
                +selected.getAudit().getP5()+selected.getAudit().getP6()+selected.getAudit().getP7()+selected.getAudit().getP8()
                +selected.getAudit().getP9()+selected.getAudit().getP10();
    }
    
    public boolean esIntervencionMinima() {
        int puntaje = selected.getAudit().getP1()+selected.getAudit().getP2()+selected.getAudit().getP3();
        if (selected.getSexo() == 1) {
            if (puntaje <=4){
                selected.getAudit().setP4(0);
                selected.getAudit().setP5(0);
                selected.getAudit().setP6(0);
                selected.getAudit().setP7(0);
                selected.getAudit().setP8(0);
                selected.getAudit().setP9(0);
                selected.getAudit().setP10(0);
                return true;
            }
        }else{
            if (puntaje<=3) {
                selected.getAudit().setP4(0);
                selected.getAudit().setP5(0);
                selected.getAudit().setP6(0);
                selected.getAudit().setP7(0);
                selected.getAudit().setP8(0);
                selected.getAudit().setP9(0);
                selected.getAudit().setP10(0);
                return true;
            }
        }
        return false;
    }
    
    public void createPDF() throws DocumentException, IOException{
        PdfReader pdfTemplate = new PdfReader("http://localhost:8080/SSMS-PSA_y_Epicrisis-web/faces/resources/otros/template2.pdf");
        //Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //PdfWriter.getInstance(document, baos);
        String title = "CLAP "+selected.getId()+" "+selected.getRUN()+"-"+selected.getDV()+".pdf";
        //FileOutputStream fileOutputStream = new FileOutputStream(title);
        PdfStamper stamper = new PdfStamper(pdfTemplate, baos);
        
        AcroFields form = stamper.getAcroFields();
       
        ///////////////////////
        //Seccion de Paciente
        //////////////////////
        form.setField("nombres", selected.getNombres());
        form.setField("apellidos", selected.getPrimer_apellido()+" "+selected.getSegundo_apellido()); 
        form.setField("domicilio", selected.getCalle_direccion()+" "+selected.getNumero_direccion()+", "+selected.getComuna_residencia().getNombre()); 
        form.setField("nombre_social", selected.getNombre_social()); 
        form.setField("centro_salud", selected.getCesfam().getNombre()); 
        form.setField("codigo", "");
        
        //Formato Nuevo
        //Condicional de establecimiento educacional o de salud
        if (true) {
             form.setField("control_educacional", "Yes");
        }else{
            form.setField("control_salud", "Yes");
        }
        //Formato nuevo
        form.setField("establecimiento_educacional", "");
        form.setField("hcn", "");
       //Condicional domicilio
        if (true) {
            form.setField("tel_fijo_domicilio", "Yes");
        }
        if (true) {
            form.setField("cel_recados", "Yes");
        }
        /////////////////////////////////////////
        
        form.setField("tel_fijo", selected.getTelefono_fijo());
        form.setField("cel", selected.getTelefono_movil());
        String pattern = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(selected.getFecha_nacimiento());
        System.out.println(date);
        form.setField("fecha_nacimiento", date);
        form.setField("run", selected.getRUN()+"-"+selected.getDV());
        //Condicional de sexo
        if (selected.getSexo()== 2) {
            form.setField("sexo_mujer", "Yes");
        }else{
            form.setField("sexo_hombre", "Yes");
        }
        //Formato Nuevo
        //Condicional de beneficiario
        if (true) {
            form.setField("beneficiario_si", "Yes");
        }else{
            form.setField("beneficiario_no", "Yes");
        }
        ////////////////
        form.setField("correo", selected.getCorreo());
        form.setField("id_consulta", ""+selected.getId());
        form.setField("fecha", ""+selected.getId());
        date = simpleDateFormat.format(selected.getFecha_consulta());
        form.setField("fecha", date);
        form.setField("anos", ""+selected.getEdad());
        //Formato Nuevo
        //CALCULAR MESES
        form.setField("meses", "");
        /////////////////////
        form.setField("pais", selected.getNacionalidad().getNombre());
        //Condicional de estado civil
        Long estado_civil = selected.getEstado_conyugal().getId();
        if (estado_civil == 1) {
            form.setField("soltero", "Yes");
        }else if(estado_civil == 2){
            form.setField("casado", "Yes");
        }else if(estado_civil == 3){
            form.setField("viudo", "Yes");
        }else if(estado_civil == 4){
            form.setField("separado", "Yes");
        }else if (estado_civil ==5){   
            form.setField("conviviente", "Yes");
        }else{
            form.setField("divorciado", "Yes");            
        }
        //Condicional de pueblo indigena
        if (selected.getPueblo_originario().getId()==10) {
            form.setField("pueblo_indigena_no", "Yes");
        }else{
            form.setField("pueblo_indigena_si", "Yes");
            form.setField("pueblo_indigena", selected.getPueblo_originario().getNombre());
        }
        //Condicional Acompañante
        String acomp = selected.getAcompanante();
        if (acomp!=null){
        if (acomp.equals("solo")) {
            form.setField("solo", "Yes");
        }else if(acomp.equals("madre")){
            form.setField("madre", "Yes");
        }else if(acomp.equals("padre")){
            form.setField("padre", "Yes");
        }else if(acomp.equals("ambos")){
            form.setField("ambos", "Yes");
        }else if (acomp.equals("amigo")){   
            form.setField("amigo", "Yes");
        }else if (acomp.equals("pareja")){
            form.setField("pareja", "Yes");            
        }else if(acomp.equals("pariente")){
            form.setField("pariente", "Yes");
        }else{
            form.setField("otro", "Yes");
        }
        }
        
        form.setField("motivo_adolescente_1", selected.getMotivo_consulta_adolescente_1());
        form.setField("motivo_adolescente_2", selected.getMotivo_consulta_adolescente_2());
        form.setField("motivo_adolescente_3", selected.getMotivo_consulta_adolescente_3());
        
        form.setField("motivo_acom_1", selected.getMotivo_consulta_acompanante_1());
        form.setField("motivo_acom_2", selected.getMotivo_consulta_acompanante_2());
        form.setField("motivo_acom_3", selected.getMotivo_consulta_acompanante_3());
        
        form.setField("descripcion_motivo_consulta", selected.getDescripcion_motivo_consulta());
        
		///////////////////////
        //Seccion de Paciente
        //////////////////////
        
        ///////////////////////
        //Antecedentes personales
        //////////////////////
        //Perinatales
        int perinatales_normales= selected.getPerinatales_normales();
        if (perinatales_normales==1) {
            form.setField("perinatales_si", "Yes");
        }else if(2==perinatales_normales){
            form.setField("perinatales_nose", "Yes");
        }else{
            form.setField("perinatales_no", "Yes");
        }
        
        //Alergias
        int alergias= selected.getAlergias_normales();
        if (alergias==1) {
            form.setField("alergias_si", "Yes");
        }else if(2==alergias){
            form.setField("alergias_nose", "Yes");
        }else{
            form.setField("alergias_no", "Yes");
        }
        
        //Vacunas
        int vacunas= selected.getVacunas_completas();
        if (vacunas==1) {
            form.setField("vacunas_si", "Yes");
        }else if(2==vacunas){
            form.setField("vacunas_nose", "Yes");
        }else{
            form.setField("vacunas_no", "Yes");
        }
        
        //Enfermedades importantes
        int enf_imp= selected.getEnfermedades_importantes();
        if (enf_imp==1) {
            form.setField("enf_imp_si", "Yes");
        }else if(2==enf_imp){
            form.setField("enf_imp_nose", "Yes");
        }else{
            form.setField("enf_imp_no", "Yes");
        }
        
        //Discapacidad
        int discapacidad= selected.getDiscapacidad();
        if (discapacidad==1) {
            form.setField("discapacidad_si", "Yes");
        }else if(2==discapacidad){
            form.setField("discapacidad_nose", "Yes");
        }else{
            form.setField("discapacidad_no", "Yes");
        }
        
        //Accidente Relevante
        int accidente= selected.getAccidentes_relevantes();
        if (accidente==1) {
            form.setField("accidente_si", "Yes");
        }else if(2==accidente){
            form.setField("accidente_nose", "Yes");
        }else{
            form.setField("accidente_no", "Yes");
        }
        
        //Cirugia/hospitalizaciones
        int cirugia_hosp= selected.getCirugia_hospitalizaciones();
        if (cirugia_hosp==1) {
            form.setField("cirugia_hosp_si", "Yes");
        }else if(2==cirugia_hosp){
            form.setField("cirugia_hosp_nose", "Yes");
        }else{
            form.setField("cirugia_hosp_no", "Yes");
        }
        
        //Uso medicamentos
        boolean medicamentos= selected.getUso_medicamentos();
        if (medicamentos==true) {
            form.setField("medicamentos_si", "Yes");
        }else{
            form.setField("medicamentos_no", "Yes");
        }
        
        //Cirugia/hospitalizaciones
        int salud_mental= selected.getProblemas_salud_mental();
        if (salud_mental==1) {
            form.setField("salud_mental_si", "Yes");
        }else if(2==salud_mental){
            form.setField("salud_mental_nose", "Yes");
        }else{
            form.setField("salud_mental_no", "Yes");
        }
        
        //Violencia
        int violencia= selected.getViolencia();
        if (violencia==1) {
            form.setField("violencia_si", "Yes");
        }else if(2==violencia){
            form.setField("violencia_nose", "Yes");
        }else{
            form.setField("violencia_no", "Yes");
        }
        
        //Judiciales
        int judiciales= selected.getJudiciales();
        if (judiciales==1) {
            form.setField("judiciales_si", "Yes");
        }else if(2==judiciales){
            form.setField("judiciales_nose", "Yes");
        }else{
            form.setField("judiciales_no", "Yes");
        }
        
        //Otros
        int otros= selected.getOtros();
        if (otros==1) {
            form.setField("otros_si", "Yes");
        }else if(2==otros){
            form.setField("otros_nose", "Yes");
        }else{
            form.setField("otros_no", "Yes");
        }
        
        form.setField("ant_personales_obs", selected.getObservaciones_antecdentes_personales());
        
        ///////////////////////
        //Antecedentes familiares
        //////////////////////
        //Enfermedades importantes
        int enf_imp_fam= selected.getEnfermedades_importantes_familia();
        if (enf_imp_fam==1) {
            form.setField("enf_imp_fam_si", "Yes");
        }else if(2==enf_imp_fam){
            form.setField("enf_imp_fam_nose", "Yes");
        }else{
            form.setField("enf_imp_fam_no", "Yes");
        }
        
        //Obesidad
        int obesidad_fam= selected.getObesidad_familia();
        if (obesidad_fam==1) {
            form.setField("obesidad_fam_si", "Yes");
        }else if(2==obesidad_fam){
            form.setField("obesidad_fam_nose", "Yes");
        }else{
            form.setField("obesidad_fam_no", "Yes");
        }
        
        //Problemas salud mental
        int salud_mental_fam= selected.getProblemas_salud_mental_familia();
        if (salud_mental_fam==1) {
            form.setField("salud_mental_fam_si", "Yes");
        }else if(2==salud_mental_fam){
            form.setField("salud_mental_fam_nose", "Yes");
        }else{
            form.setField("salud_mental_fam_no", "Yes");
        }
        
        //Violencia intrafamiliar
        int violencia_intrafam= selected.getViolencia_intrafamiliar();
        if (violencia_intrafam==1) {
            form.setField("violencia_intrafam_si", "Yes");
        }else if(2==violencia_intrafam){
            form.setField("violencia_intrafam_nose", "Yes");
        }else{
            form.setField("violencia_intrafam_no", "Yes");
        }
        
        //Alcohol y otras drogas
        int alcohol_droga_fam= selected.getAlcohol_y_otras_drogas();
        if (alcohol_droga_fam==1) {
            form.setField("alcohol_droga_fam_si", "Yes");
        }else if(2==alcohol_droga_fam){
            form.setField("alcohol_droga_fam_nose", "Yes");
        }else{
            form.setField("alcohol_droga_fam_no", "Yes");
        }
        
        //Madre y/o padre adolescente
        int m_p_adolescente_fam= selected.getPadre_adolescente();
        if (m_p_adolescente_fam==1) {
            form.setField("m_p_adolescente_fam_si", "Yes");
        }else if(2==m_p_adolescente_fam){
            form.setField("m_p_adolescente_fam_nose", "Yes");
        }else{
            form.setField("m_p_adolescente_fam_no", "Yes");
        }
        
        //Judiciales
        int judiciales_fam= 0;//agregar al clap
        if (judiciales_fam==1) {
            form.setField("judiciales_fam_si", "Yes");
        }else if(2==judiciales_fam){
            form.setField("judiciales_fam_nose", "Yes");
        }else{
            form.setField("judiciales_fam_no", "Yes");
        }
        
        int otros_fam= selected.getPadre_adolescente();
        if (otros_fam==1) {
            form.setField("otros_fam_si", "Yes");
        }else if(2==otros_fam){
            form.setField("otros_fam_nose", "Yes");
        }else{
            form.setField("otros_fam_no", "Yes");
        }
        
        form.setField("ant_familiares_obs", selected.getObservaciones_antecedentes_familiares());
        
        ///////////////////////
        //Familia
        //////////////////////
        //Vive con
        boolean solo= selected.isVive_con_solo();
        if (solo==true) {
            form.setField("vive_con_solo_si", "Yes");
        }else{
            form.setField("vive_con_solo_no", "Yes");
        }
        boolean madre= selected.isVive_con_madre();
        if (madre==true) {
            form.setField("vive_con_madre_si", "Yes");
        }else{
            form.setField("vive_con_madre_no", "Yes");
        }
        boolean padre= selected.isVive_con_padre();
        if (padre==true) {
            form.setField("vive_con_padre_si", "Yes");
        }else{
            form.setField("vive_con_padre_no", "Yes");
        }
        boolean institucion= selected.isVive_en_institucion();
        if (institucion==true) {
            form.setField("vive_en_institucion_si", "Yes");
        }else{
            form.setField("vive_en_institucion_no", "Yes");
        }
        boolean con_otros= selected.isVive_con_otros();
        if (con_otros==true) {
            form.setField("vive_con_otros_si", "Yes");
            form.setField("otros_especificacion", selected.getVive_con_especificacion());

        }else{
            form.setField("vive_con_otros_no", "Yes");
        }
        
        //nivel de instruccion
        //madre
        int nivel_instruccion_madre= selected.getNivel_instruccion_madre();
        if (nivel_instruccion_madre==1) {
            form.setField("niv_instruc_madre_ninguno", "Yes");
        }else if(2==nivel_instruccion_madre){
            form.setField("niv_instruc_madre_basica", "Yes");
        }else if(3==nivel_instruccion_madre){
            form.setField("niv_instruc_madre_media", "Yes");
        }else if(4==nivel_instruccion_madre){
            form.setField("niv_instruc_madre_superior", "Yes");
        }else{
            form.setField("", "Yes");
        }
        
        //padre
        int nivel_instruccion_padre= selected.getNivel_instruccion_padre();
        if (nivel_instruccion_padre==1) {
            form.setField("niv_instruc_padre_ninguno", "Yes");
        }else if(2==nivel_instruccion_padre){
            form.setField("niv_instruc_padre_basica", "Yes");
        }else if(3==nivel_instruccion_padre){
            form.setField("niv_instruc_padre_media", "Yes");
        }else if(4==nivel_instruccion_padre){
            form.setField("niv_instruc_padre_superior", "Yes");
        }else{
            form.setField("", "Yes");
        }
        
        //pareja
        int nivel_instruccion_pareja= selected.getNivel_instruccion_madre();
        if (nivel_instruccion_pareja==1) {
            form.setField("niv_instruc_pareja_ninguno", "Yes");
        }else if(2==nivel_instruccion_pareja){
            form.setField("niv_instruc_pareja_basica", "Yes");
        }else if(3==nivel_instruccion_pareja){
            form.setField("niv_instruc_pareja_media", "Yes");
        }else if(4==nivel_instruccion_pareja){
            form.setField("niv_instruc_pareja_superior", "Yes");
        }else{
            form.setField("", "Yes");
        }
        
        //Comparte cama
        boolean comparte_cama= selected.isComparte_cama();
        if (comparte_cama==true) {
            form.setField("comparte_cama_si", "Yes");
            form.setField("comparte_cama_desc", selected.getEspecificacion_comparte_cama());
        }else{
            form.setField("comparte_cama_no", "Yes");
        }
        
        //Ocupacion
        form.setField("ocupacion_madre", selected.getOcupacion_madre());
        form.setField("ocupacion_padre", selected.getOcupacion_padre());
        form.setField("ocupacion_pareja", selected.getOcupacion_pareja());

        //Percepcion del adolescente sobre su familia
        int percepcion_fam= selected.getPercepcion_familia();
        if (percepcion_fam==1) {
            form.setField("percepcion_fam_buena", "Yes");
        }else if(2==percepcion_fam){
            form.setField("percepcion_fam_regular", "Yes");
        }else if(3==percepcion_fam){
            form.setField("percepcion_fam_mala", "Yes");
        }else if(4==percepcion_fam){
            form.setField("percepcion_fam_nohay", "Yes");
        }else{
            form.setField("", "Yes");
        }
        /////////////
        //Vivienda
        /////////////
        //Condiciones sanitarias
        boolean cond_sanitarias= selected.isCondiciones_sanitarias();
        if (cond_sanitarias==true) {
            form.setField("condiciones_sanitarias_si", "Yes");
        }else{
            form.setField("condiciones_sanitarias_no", "Yes");
        }
        
        //Hacinamiento
        boolean hacinamiento_vivienda= selected.isHacinamiento();
        if (hacinamiento_vivienda==true) {
            form.setField("hacinamiento_si", "Yes");
        }else{
            form.setField("hacinamiento_no", "Yes");
        }
        form.setField("vivienda_observacion", selected.getObservaciones_vivienda());
        
        /////////////
        //Educacion
        /////////////
        //Estudia
        boolean estudia= selected.isEstudia();
        if (estudia==true) {
            form.setField("estudia_si", "Yes");
        }else{
            form.setField("estudia_no", "Yes");
        }
        
        //Nivel
        int nivel_estudio= selected.getNivel_educacion();
        if (nivel_estudio==1) {
            form.setField("nivel_no_escolarizado", "Yes");
        }else if(2==nivel_estudio){
            form.setField("nivel_educacion_basica", "Yes");
        }else if(3==nivel_estudio){
            form.setField("nivel_educacion_media", "Yes");
        }else if(4==nivel_estudio){
            form.setField("nivel_educacion_superior", "Yes");
        }else{
            form.setField("", "Yes");
        }
        
        //Grado o Curso
        
        //Años Repetidos
        form.setField("anios_repetidos", Integer.toString(selected.getAnos_repetidos()));
        form.setField("causa_repitencia", selected.getCausa_anos_repetidos());

        //problemas en la  escuela
        boolean problemas_en_escuela= selected.isProblemas_escuela();
        if (problemas_en_escuela==true) {
            form.setField("problemas_en_escuela_si", "Yes");
        }else{
            form.setField("problemas_en_escuela_no", "Yes");
        }        
        
        //violencia escolar
        boolean violencia_escolar= selected.isViolencia_escolar();
        if (violencia_escolar==true) {
            form.setField("violencia_escolar_si", "Yes");
        }else{
            form.setField("violencia_escolar_no", "Yes");
        } 
        
        //desercion/exclusion
        boolean desercion_exclusion= selected.isDesercion_exclusion();
        if (desercion_exclusion==true) {
            form.setField("desercion_exclusion_si", "Yes");
        }else{
            form.setField("desercion_exclusion_no", "Yes");
        } 
        form.setField("desercion_exclusion_causa", selected.getCausa_desercion_exclusion());

        
        //Percecpcion del rendimiento respecto a la mayoria de sus compañeros
        
        int percepcion_rendimiento= selected.getNivel_educacion();
        if (percepcion_rendimiento==1) {
            form.setField("percepcion_rendimiento_mejor", "Yes");
        }else if(2==percepcion_rendimiento){
            form.setField("percepcion_rendimiento_peor", "Yes");
        }else if(3==percepcion_rendimiento){
            form.setField("percepcion_rendimiento_igual", "Yes");
        }else{
            form.setField("", "Yes");
        }
        
        /////////////
        //Trabajo
        /////////////
        //trabaja
        boolean trabaja= selected.isTrabaja();
        if (trabaja==true) {
            form.setField("trabaja_si", "Yes");
        }else{
            form.setField("trabaja_no", "Yes");
        } 
        form.setField("trabaja_horas", Integer.toString(selected.getHoras_trabajo()));

        //trabajo
        boolean trabajo_infantil= selected.isTrabajo_infantil();
        if (trabajo_infantil==true) {
            form.setField("trabajo_infantil_si", "Yes");
        }else{
            form.setField("trabajo_infantil_no", "Yes");
        } 
        boolean trabajo_juvenil= selected.isTrabajo_juvenil();
        if (trabajo_juvenil==true) {
            form.setField("trabajo_juvenil_si", "Yes");
        }else{
            form.setField("trabajo_juvenil_no", "Yes");
        }
        
        //peores formas
        boolean peores_formas= selected.isPeores_formas();
        if (peores_formas==true) {
            form.setField("peores_formas_si", "Yes");
        }else{
            form.setField("peores_formas_no", "Yes");
        } 
        
        //servicio doméstico no remunerado peligroso
        boolean servicio_domestico= selected.isSer_dom_no_remu_peligroso();
        if (servicio_domestico==true) {
            form.setField("serv_domestico_si", "Yes");
        }else{
            form.setField("serv_domestico_no", "Yes");
        }
        
        //Razon de trabajo        
        int razon_trabajo= selected.getRazon_de_trabajo();
        if (razon_trabajo==1) {
            form.setField("razon_trabajo_economica", "Yes");
        }else if(2==razon_trabajo){
            form.setField("razon_trabajo_autonomia", "Yes");
        }else if(3==razon_trabajo){
            form.setField("razon_trabajo_megusta", "Yes");
        }else if(4==razon_trabajo){
            form.setField("razon_trabajo_otra", "Yes");
        }else if(5==razon_trabajo){
            form.setField("razon_trabajo_nc", "Yes");
        }else{
            form.setField("", "Yes");
        }
        
        //legalizado
        int legalizado= selected.getLegalizado();
        if (legalizado==1) {
            form.setField("legalizado_si", "Yes");
        }else if(2==legalizado){
            form.setField("legalizado_no", "Yes");
        }else if(3==legalizado){
            form.setField("legalizado_nc", "Yes");
        }else{
            form.setField("", "Yes");
        }
        
        //Tipo de trabajo
        form.setField("tipo_trabajo", selected.getTipo_de_trabajo());
        
        //Trabajo observaciones
        form.setField("trabajo_observaciones", selected.getObservaciones_trabajo());

        //////////////////
        //Vida Social
        /////////////////
        //Aceptacion
        int aceptacion= selected.getLegalizado();
        if (aceptacion==1) {
            form.setField("aceptacion_aceptado", "Yes");
        }else if(2==aceptacion){
            form.setField("aceptacion_ignorado", "Yes");
        }else if(3==aceptacion){
            form.setField("aceptacion_rechazado", "Yes");
        }else if(4==aceptacion){
            form.setField("aceptacion_no_sabe", "Yes");
        }else{
            form.setField("", "Yes");
        }
        
        //Pareja
        boolean pareja= selected.isPareja();
        if (pareja==true) {
            form.setField("pareja_si", "Yes");
        }else{
            form.setField("pareja_no", "Yes");
        }
        
        //edad de pareja en anios y meses(?) faltan los meses
        form.setField("pareja_edad_anios", Integer.toString(selected.getEdad_pareja()));

        //violencia en la Pareja
        boolean violencia_pareja= selected.isViolencia_pareja();
        if (violencia_pareja==true) {
            form.setField("violencia_pareja_si", "Yes");
        }else{
            form.setField("violencia_pareja_no", "Yes");
        }
        
        //amigos
        boolean amigos= selected.isAmigos();
        if (amigos==true) {
            form.setField("amigos_si", "Yes");
        }else{
            form.setField("amigos_no", "Yes");
        }
        
        //actividad fisica
        form.setField("actividad_fisica_horas", Integer.toString(selected.getHoras_actividad_fisica()));

        //tv
        form.setField("tv_horas", Integer.toString(selected.getHoras_tv()));

        //computador/consola y otros
        form.setField("pc_horas", Integer.toString(selected.getHoras_computador_consola()));

        //otras actividades
        boolean otras_actividades= selected.isOtras_actividades();
        if (otras_actividades==true) {
            form.setField("otras_actividades_si", "Yes");
            form.setField("otras_actividades_cuales", selected.getEspecificacion_otras_actividades());
        }else{
            form.setField("otras_actividades_no", "Yes");
        }
        
        //grooming
        boolean grooming= selected.isGrooming();
        if (grooming==true) {
            form.setField("grooming_si", "Yes");
        }else{
            form.setField("grooming_no", "Yes");
        }
        
        //cyberbullyng
        boolean cyberbullyng= selected.isCyberbulling();
        if (cyberbullyng==true) {
            form.setField("cyberbullyng_si", "Yes");
        }else{
            form.setField("cyberbullyng_no", "Yes");
        }
        
        //vida social observaciones
        form.setField("vida_social_observaciones", selected.getObservaciones_vida_social());
        
        //////////////////
        //Habitos y cosnumo
        /////////////////
        //Sueño normal
        boolean suenio_normal= selected.isSueno_normal();
        if (suenio_normal==true) {
            form.setField("suenio_normal_si", "Yes");
        }else{
            form.setField("suenio_normal_no", "Yes");
        }
        //horas de suenio
        form.setField("comida_familia", Integer.toString(selected.getHoras_sueno()));

        //alimentacion adecuada
        boolean alimentacion= selected.isAlimentacion_adecuada();
        if (alimentacion==true) {
            form.setField("alimentacion_adecuada_si", "Yes");
        }else{
            form.setField("alimentacion_adecuada_no", "Yes");
        }
        
        //comidas con la familia
        form.setField("suenio_horas", Integer.toString(selected.getComidas_familia()));

        //alimentacion especial
        boolean alimentacion_especial= selected.isAlimentacion_especial();
        if (alimentacion_especial==true) {
            form.setField("alimentacion_especial_si", "Yes");
            form.setField("alimentacion_especial_cual", selected.getEspecificacion_alimentacion_especial());
        }else{
            form.setField("alimentacion_especial_no", "Yes");
        }
        
        //Alcohol y otras drogas
        //alcohol
        boolean alcohol= selected.isConsumo_alcohol();
        if (alcohol==true) {
            form.setField("alcohol_si", "Yes");
        }else{
            form.setField("alcohol_no", "Yes");
        }
        //marihuana
        boolean marihuana= selected.isConsumo_marihuana();
        if (marihuana==true) {
            form.setField("marihuana_si", "Yes");
        }else{
            form.setField("marihuana_no", "Yes");
        }
        //otras sustancias
        boolean otra_sustancia= selected.isConsumo_otra_sustancia();
        if (otra_sustancia==true) {
            form.setField("otra_sustancia_si", "Yes");
            form.setField("otra_sustancia_cual", selected.getEspecificacion_consumo_otra_sustancia());
        }else{
            form.setField("otra_sustancia_no", "Yes");
        }
        
        //seguridad vial
        boolean seguridad_vial= selected.isSeguridad_vial();
        if (seguridad_vial==true) {
            form.setField("seguridad_vial_si", "Yes");
        }else{
            form.setField("seguridad_vial_no", "Yes");
        }
        
        //habitos y consumo observaciones
        form.setField("habitos_consumo_obs", selected.getObservaciones_habitos_consumo());
        
        ///////////////////
        //gineco/urologica
        ///////////////////
        //menarca/espermarca
        form.setField("menarca_espermarca", Integer.toString(selected.getEdad_menarca_espermarca()));

        //fecha ultima menstruacion
        if (selected.getFecha_ultima_menstruacion()!=null) {
            date = simpleDateFormat.format(selected.getFecha_ultima_menstruacion());
            form.setField("fecha_menstruacion", date);
        }
        
        boolean menstruacion= selected.isNo_conoce_fecha_ultima_menstruacion();
        if (menstruacion==true) {
            form.setField("menstruacion_no_conoce", "Yes");
        }else{
            form.setField("menstruacion_nc", "Yes");
        }
        
        //Ciclos regulares
        int ciclo_regular= selected.getCiclos_regulares();
        if (ciclo_regular==1) {
            form.setField("ciclo_regular_si", "Yes");
        }else if(2==aceptacion){
            form.setField("ciclo_regular_no", "Yes");
        }else if(3==aceptacion){
            form.setField("ciclo_regular_ns", "Yes");
        }else if(4==aceptacion){
            form.setField("ciclo_regular_nc", "Yes");
        }else{
            form.setField("", "Yes");
        }
        
        //dismenorrea
        int dismenorrea= selected.getCiclos_regulares();
        if (dismenorrea==1) {
            form.setField("dismenorrea_si", "Yes");
        }else if(2==dismenorrea){
            form.setField("dismenorrea_no", "Yes");
        }else if(3==dismenorrea){
            form.setField("dismenorrea_nc", "Yes");
        }else{
            form.setField("", "Yes");
        }
        
        //flujo patologico vaginal/secrecion peneana
        boolean flujo_secrecion= selected.isFlujo_secrecion_patologico();
        if (flujo_secrecion==true) {
            form.setField("flujo_secrecion_si", "Yes");
        }else{
            form.setField("flujo_secrecion_no", "Yes");
        }
        
        //ITS/VIH
        boolean its_vih= selected.isIts_vih();
        if (its_vih==true) {
            form.setField("its_vih_si", "Yes");
            form.setField("its_vih_cual", selected.getEspecificacion_its_vih());
        }else{
            form.setField("its_vih_no", "Yes");
        }
        //Tratamiento
        int tratamiento= selected.getTratamiento();
        if (tratamiento==1) {
            form.setField("its_vih_tratamiento_si", "Yes");
        }else if(2==tratamiento){
            form.setField("its_vih_tratamiento_no", "Yes");
        }else if(3==tratamiento){
            form.setField("its_vih_tratamiento_ns", "Yes");
        }else{
            form.setField("", "Yes");
        }
        //Tratamiento contactos
        int tratamiento_contactos= selected.getTratamiento();
        if (tratamiento_contactos==1) {
            form.setField("its_vih_tratamiento_contactos_si", "Yes");
        }else if(2==tratamiento_contactos){
            form.setField("its_vih_tratamiento_contactos_no", "Yes");
        }else if(3==tratamiento_contactos){
            form.setField("its_vih_tratamiento_contactos_ns", "Yes");
        }else{
            form.setField("", "Yes");
        }
        
        //embarazo, hijos. abortos
        int embarazos= selected.getEmbarazos();
        if (embarazos>0) {
            form.setField("embarazos", "Yes");
        }else{
            form.setField("", "Yes");
        }
        int hijos= selected.getHijos();
        if (hijos>0) {
            form.setField("hijos", "Yes");
        }else{
            form.setField("", "Yes");
        }
        int abortos= selected.getAbortos();
        if (abortos>0) {
            form.setField("abortos", "Yes");
        }else{
            form.setField("", "Yes");
        }
        
        //observaciones gineco urologicas
        form.setField("gineco_urologico_obs", selected.getObservaciones_gineco_urologico());
        
        //////////////////
        //Sexualidad
        /////////////////
        //orientacion sexual
        int orient_sexual= selected.getOrientacion_sexual();
        if (orient_sexual==1) {
            form.setField("heterosexual", "Yes");
        }else if(2==orient_sexual){
            form.setField("homosexual", "Yes");
        }else if(3==orient_sexual){
            form.setField("bisexual", "Yes");
        }else if(4==orient_sexual){
            form.setField("orient_sexual_nr", "Yes");
        }else{
            form.setField("orient_sexual_desc", selected.getEspecificacion_orientacion_sexual());
        }
        
        //intencion o conducta sexual
        int conducta_sexual= selected.getConducta_sexual();
        if (conducta_sexual==1) {
            form.setField("postergadora", "Yes");
        }else if(2==conducta_sexual){
            form.setField("anticipadora", "Yes");
        }else if(3==conducta_sexual){
            form.setField("activa", "Yes");
            form.setField("edad_inicio_sexual", Integer.toString(selected.getEdad_inicio_conducta_sexual()));
        }
        
        //relaciones sexuales con
        int relaciones_sexuales= selected.getRelaciones_sexuales();
        if (relaciones_sexuales==1) {
            form.setField("rel_sex_distinto", "Yes");
        }else if(2==relaciones_sexuales){
            form.setField("rel_sex_mismo", "Yes");
        }else if(3==relaciones_sexuales){
            form.setField("rel_sex_ambos", "Yes");
        }else if(4==relaciones_sexuales){
            form.setField("rel_sex_nc", "Yes");
        }else{
            form.setField("","Yes");
        }

        //pareja sexual
        int pareja_sexual= selected.getPareja_sexual();
        if (pareja_sexual==1) {
            form.setField("pareja_sexual_unica", "Yes");
        }else if(2==pareja_sexual){
            form.setField("pareja_sexual_varias", "Yes");
        }else if(3==pareja_sexual){
            form.setField("pareja_sexual_nc", "Yes");
        }else{
            form.setField("","Yes");
        }
        //dificultades en relaciones sexuales
        int dificultad_rel_sex= selected.getPareja_sexual();
        if (dificultad_rel_sex==1) {
            form.setField("dificultad_rel_sex_si", "Yes");
        }else if(2==dificultad_rel_sex){
            form.setField("dificultad_rel_sex_no", "Yes");
        }else if(3==dificultad_rel_sex){
            form.setField("dificultad_rel_sex_nc", "Yes");
        }else{
            form.setField("","Yes");
        }
        
        //Anticoncepcion
        //uso condon
        int anticoncepcion= selected.getAnticoncepcion();
        if (anticoncepcion==1) {
            form.setField("condon_siempre", "Yes");
        }else if(2==anticoncepcion){
            form.setField("condon_aveces", "Yes");
        }else if(3==anticoncepcion){
            form.setField("condon_nunca", "Yes");
        }else{
            form.setField("","Yes");
        }
        
        //doble proteccion 
        /*boolean doble_proteccion= true;
        if (doble_proteccion==true) {
            form.setField("doble_proteccion_si", "Yes");
        }else{
            form.setField("doble_proteccion_no", "Yes");
        }*/
        
        //uso mac
        int uso_mac= selected.getUso_mac();
        if (uso_mac==1) {
            form.setField("uso_mac_si", "Yes");
        }else if(2==uso_mac){
            form.setField("uso_mac_no", "Yes");
        }else if(3==uso_mac){
            form.setField("uso_mac_aveces", "Yes");
        }else{
            form.setField("","Yes");
        }
        form.setField("uso_mac_cual", selected.getEspecificacion_uso_mac());
        form.setField("uso_mac_razon", selected.getRazon_no_uso_mac());
        
        //consejeria uso mac
        boolean consejeria= selected.isConsejeria_uso_mac();
        if (consejeria==true) {
            form.setField("consejeria_mac_si", "Yes");
        }else{
            form.setField("consejeria_mac_no", "Yes");
        }
        
        //aco de emergencia
        boolean aco_emergencia= selected.isAco_emergencia();
        if (aco_emergencia==true) {
            form.setField("aco_emergencia_si", "Yes");
        }else{
            form.setField("aco_emergencia_no", "Yes");
        }
        
        //violencia sexual
        boolean violencia_sexual= selected.isAbuso_sexual();
        if (violencia_sexual==true) {
            form.setField("violencia_sexual_si", "Yes");
        }else{
            form.setField("violencia_sexual_no", "Yes");
        }
        
        //violencia sexual reparacion
        boolean reparacion= selected.isReparacion_abuso_sexual();
        if (reparacion==true) {
            form.setField("violencia_sexual_reparacion_si", "Yes");
        }else{
            form.setField("violencia_sexual_reparacion_no", "Yes");
        }
        
        //sexualidad_observaciones
        form.setField("sexualidad_observaciones", selected.getObservaciones_sexualidad());

        /////////////////
        //situacion sico-emocional
        /////////////////
        //imagen corporal
        int imagen_corporal= selected.getImagen_corporal();
        if (imagen_corporal==1) {
            form.setField("imagen_corporal_conforme", "Yes");
        }else if(2==imagen_corporal){
            form.setField("imagen_corporal_crea_preocupacione", "Yes");
        }else if(3==imagen_corporal){
            form.setField("imagen_corporal_impide", "Yes");
        }else{
            form.setField("","Yes");
        }
        //vida con proyecto
        int vida_con_proyecto= selected.getVida_proyecto();
        if (vida_con_proyecto==1) {
            form.setField("vida_con_proyecto_claro", "Yes");
        }else if(2==vida_con_proyecto){
            form.setField("vida_con_proyecto_confuso", "Yes");
        }else if(3==vida_con_proyecto){
            form.setField("vida_con_proyecto_ausente", "Yes");
        }else{
            form.setField("","Yes");
        }
        
        //bienestar emocional
        int bienestar_emocional= selected.getBienestar_emocional();
        if (bienestar_emocional==1) {
            form.setField("normal", "Yes");
        }else if(2==bienestar_emocional){
            form.setField("deprimido", "Yes");
        }else if(3==bienestar_emocional){
            form.setField("irritable", "Yes");
        }else if(4==bienestar_emocional){
            form.setField("desesperanzado", "Yes");
        }else if(5==bienestar_emocional){
            form.setField("poco_interes", "Yes");
        }else if(6==bienestar_emocional){
            form.setField("euforico", "Yes");
        }else if(7==bienestar_emocional){
            form.setField("ansioso", "Yes");
        }else if(8==bienestar_emocional){
            form.setField("alta_impulsividad", "Yes");
        }else if(9==bienestar_emocional){
            form.setField("autoagresiones", "Yes");
        }else{
            form.setField("","Yes");
        }
        
        //riesgo suicida
        //suicidalidad
        boolean suicidalidad= selected.isSuicidalidad_amigos();
        if (suicidalidad==true) {
            form.setField("suicidalidad_si", "Yes");
        }else{
            form.setField("suicidalidad_no", "Yes");
        }
        //ideacion suicida
        boolean ideacion_suicida= selected.isIdeacion_suicida();
        if (ideacion_suicida==true) {
            form.setField("ideacion_suicida_si", "Yes");
        }else{
            form.setField("ideacion_suicida_no", "Yes");
        }
        //intento suicida
        boolean intento_suicida= selected.isIntento_suicida();
        if (intento_suicida==true) {
            form.setField("intento_suicida_si", "Yes");
        }else{
            form.setField("intento_suicida_no", "Yes");
        }
        
        //referente adulto
        int referente_adulto= selected.getReferente_adulto();
        if (referente_adulto==1) {
            form.setField("referente_adulto_padre", "Yes");
        }else if(2==referente_adulto){
            form.setField("referente_adulto_madre", "Yes");
        }else if(3==referente_adulto){
            form.setField("referente_adulto_familiar", "Yes");
        }else if(4==referente_adulto){
            form.setField("referente_adulto_otro", "Yes");
        }else if(5==referente_adulto){
            form.setField("referente_adulto_ninguno", "Yes");
        }else{
            form.setField("","Yes");
        }
        //referente adulto nombre
        form.setField("referente_adulto_nombre", selected.getNombre_referente_adulto());

        //referente adulto telefono
        form.setField("referente_adulto_telefono", selected.getTelefono_referente_adulto());

        //situacion sico-emocional observaciones
        form.setField("situacion_psico_emocional_obs", selected.getObservaciones_psico_emocional());

        ////////////////
        //Examen fisico
        ///////////////
        //peso
        form.setField("peso", Integer.toString(selected.getPeso()));

        //talla
        form.setField("talla", Integer.toString(selected.getTalla()));
        
        //p_abdominal
        form.setField("p_abdominal", Integer.toString(selected.getPerimetro_abdominal()));
        
        //imc
        form.setField("imc", String.valueOf(selected.getImc()));
        
        //presion_arterial_1
        form.setField("presion_arterial_1", Integer.toString(selected.getPresion_arterial_sistolica()));
        
        //presion_arterial_2
        form.setField("presion_arterial_2", Integer.toString(selected.getPresion_arterial_diastolica()));

        //aspecto general
        boolean aspecto_general= selected.isAspecto_general();
        if (aspecto_general==true) {
            form.setField("aspecto_general_normal", "Yes");
        }else{
            form.setField("aspecto_general_anormal", "Yes");
        }
        
        //agudeza visual
        boolean agudeza_visual= selected.isAgudeza_visual();
        if (agudeza_visual==true) {
            form.setField("agud_visual_normal", "Yes");
        }else{
            form.setField("agud_visual_anormal", "Yes");
        }
        
        //agudeza auditiva
        /*boolean agud_auditiva= selected.is;
        if (aspecto_general==true) {
            form.setField("agud_auditiva_normal", "Yes");
        }else{
            form.setField("agud_auditiva_anormal", "Yes");
        }*/
        
        //salud bucal
        boolean salud_bucal= selected.isSalud_bucal();
        if (salud_bucal==true) {
            form.setField("salud_bucal_normal", "Yes");
        }else{
            form.setField("salud_bucal_anormal", "Yes");
        }
        
        //tiroides
        /*boolean tiroides= selected.is;
        if (aspecto_general==true) {
            form.setField("aspecto_general_normal", "Yes");
        }else{
            form.setField("aspecto_general_anormal", "Yes");
        }*/
        
        //cardio pulmonar
        boolean cardiopulmonar= selected.isCardio_pulmonar();
        if (cardiopulmonar==true) {
            form.setField("cardiopulmonar_normal", "Yes");
        }else{
            form.setField("cardiopulmonar_anormal", "Yes");
        }
        
        //abdomen
        boolean abdomen= selected.isAbdomen();
        if (abdomen==true) {
            form.setField("abdomen_normal", "Yes");
        }else{
            form.setField("abdomen_anormal", "Yes");
        }
        
        //columna
        boolean columna= selected.isColumna();
        if (columna==true) {
            form.setField("columna_normal", "Yes");
        }else{
            form.setField("columna_anormal", "Yes");
        }
        
        //extremidades
        boolean extremidades= selected.isExtremidades();
        if (extremidades==true) {
            form.setField("extremidades_normal", "Yes");
        }else{
            form.setField("extremidades_anormal", "Yes");
        }
        
        //tanner
        boolean tanner= selected.isTanner_con_foto();
        if (tanner==true) {
            form.setField("tanner_con_foto", "Yes");
        }else{
            form.setField("", "Yes");
        }
        
        //tanner mama
        int tanner_mama= selected.getTanner_mama();
        if (tanner_mama>0) {
            form.setField("tanner_mama", "Yes");
        }else{
            form.setField("", "Yes");
        }
        
        //tanner genital
        int tanner_genital= selected.getTanner_genital();
        if (tanner_genital>0) {
            form.setField("tanner_genital", "Yes");
        }else{
            form.setField("", "Yes");
        }
        
        //examen fisico observacion
        form.setField("examen_fisico_obs", selected.getObservaciones_examen_fisico());

        //impresion diagnostica integral
        form.setField("impresion_diagnostica_integral", selected.getImpresion_diagnostica());
        
        //indicaciones e interconsultas
        form.setField("indicaciones_interconsultas", selected.getIndicaciones_interconsultas());
        
        
        //Riesgos
        boolean riesgo_oh_drogas= selected.isRiesgo_oh_drogas();
        if (riesgo_oh_drogas==true) {
            form.setField("riesgo_oh_drogas", "Yes");
        }else{
            form.setField("", "Yes");
        }
        boolean riesgo_ssr= selected.isRiesgo_ssr();
        if (riesgo_ssr==true) {
            form.setField("riesgo_ssr", "Yes");
        }else{
            form.setField("", "Yes");
        }
        boolean riesgo_nutricional= selected.isRiesgo_nutricional();
        if (riesgo_nutricional==true) {
            form.setField("riesgo_nutricional", "Yes");
        }else{
            form.setField("", "Yes");
        }
        
        boolean riesgo_cardio= selected.isRiesgo_cardiovascular();
        boolean riesgo_metal= selected.isRiesgo_salud_mental();
        boolean riesgo_social= selected.isRiesgo_social();
        
        if (riesgo_cardio==true||riesgo_metal==true||riesgo_social==true) {
            form.setField("riesgo_otro", "Yes");
            if(riesgo_cardio==true){
                form.setField("riesgo_otro_desc", "cardiovascular");
            }
            if(riesgo_metal==true){
                form.setField("riesgo_otro_desc", "salud mental");
            }
            if(riesgo_social==true){
                form.setField("riesgo_otro_desc", "social");
            } 
        }else{
            form.setField("", "Yes");
        }
        
        if (selected.getDiagrama_familiar() != null) {
            Image img = Image.getInstance(selected.getDiagrama_familiar());
            img.setAbsolutePosition(298, 164);
            stamper.getOverContent(1).addImage(img);
        }
        
        
        stamper.close();
        pdfTemplate.close();
        
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.responseReset();
        ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + title + "\"");
        InputStream input = new ByteArrayInputStream(baos.toByteArray());
        OutputStream output = ec.getResponseOutputStream();
        IOUtils.copy(input, output);
        fc.responseComplete();
    }

    public String aUsuario(){
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("clapUpdated"));
        return "/faces/clap/edit/usuario.xhtml";
    }
    
    public String aDatos(){
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("clapUpdated"));
        return "/faces/clap/edit/datos_antecedentes.xhtml";
    }
    
    public String aVivienda(){
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("clapUpdated"));
        return "/faces/clap/edit/vivienda_educacion.xhtml";
    }
    
    public String aFamilia(){
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("clapUpdated"));
        return "/faces/clap/edit/familia.xhtml";
    }
    
    public String aTrabajo(){
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("clapUpdated"));
        return "/faces/clap/edit/trabajo_vidasocial.xhtml";
    }

    public String aHabitos(){
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("clapUpdated"));
        return "/faces/clap/edit/habitos_gineco.xhtml";
    }
    
    public String aSexualidad(){
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("clapUpdated"));
        return "/faces/clap/edit/sexualidad_psicoemocional.xhtml";
    }
    
    public String aExamen(){
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("clapUpdated"));
        return "/faces/clap/edit/examen_fisico.xhtml";
    }
}