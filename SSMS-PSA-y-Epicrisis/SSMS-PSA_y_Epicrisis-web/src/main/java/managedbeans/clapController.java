package managedbeans;

import entities.Crafft;
import entities.audit;
import entities.cesfam;
import entities.clap;
import entities.comuna;
import entities.paciente;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import managedbeans.util.JsfUtil;
import managedbeans.util.JsfUtil.PersistAction;
import sessionbeans.clapFacadeLocal;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
        return "/faces/clap/Edit.xhtml";
        
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
            selected.getTanner_genital()!=0
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
                        persist(PersistAction.UPDATE,"");
                    } else if(claps.get(i).getEstado().equals("Nuevo")){
                        setSelected(claps.get(i));
                        selected.setEstado("Vigente");
                        persist(PersistAction.UPDATE,"");
                    }
                }
            }
            else{
                System.out.println("Primer clap ingresado");
                selected.setEstado("Vigente");
                persist(PersistAction.UPDATE,"");
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
        if (imagen!=null && selected.getDiagrama_familiar()==null) {
            
            Path folder = Paths.get("C:/genogramas");
            String filename = "Clap "+selected.getId();
            String extension = FilenameUtils.getExtension(imagen.getFileName());
            Path file = Files.createTempFile(folder, filename + "-", "." + extension);
            
            System.out.println(file.getFileName());

            try (InputStream input = imagen.getInputstream()) {
                Files.copy(input, file, StandardCopyOption.REPLACE_EXISTING);
                selected.setDiagrama_familiar("C:/genogramas/"+file.getFileName());
                getFacade().edit(selected);
            }
            File img = new File("C:/genogramas/"+file.getFileName());
            BufferedImage bimg = ImageIO.read(img);
            int type = bimg.getType() == 0? BufferedImage.TYPE_INT_ARGB : bimg.getType();            
            BufferedImage resizeImagePng = resizeImage(bimg, type);
            ImageIO.write(resizeImagePng, "jpg", new File("C:/genogramas/"+file.getFileName())); 
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

    public void autosave() {
        boolean completo=false;
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
            selected.getTanner_genital()!=0
            ){
            selected.setEstado("Nuevo");
            completo=true;
        }else{
            selected.setEstado("Incompleto");
            completo=false;
        }
        
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
        
        persist(PersistAction.UPDATE,"Autoguardado");
        if(selected.getEstado().equals("Nuevo")){
            List<clap> claps = getItemsPorPaciente(pacienteCtrl.getSelected().getRUN());
            if(claps.size()>1){
                System.out.println("Eziste mas de 1 clap");
                for(int i=0;i<claps.size();i++){
                    if(claps.get(i).getEstado().equals("Vigente")){
                        setSelected(claps.get(i));
                        selected.setEstado("Antiguo");
                        persist(PersistAction.UPDATE,"");
                    } else if(claps.get(i).getEstado().equals("Nuevo")){
                        setSelected(claps.get(i));
                        selected.setEstado("Vigente");
                        persist(PersistAction.UPDATE,"");
                    }
                }
            }
            else{
                System.out.println("Primer clap ingresado");
                selected.setEstado("Vigente");
                persist(PersistAction.UPDATE,"");
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
        
        
    }

    private BufferedImage resizeImage(BufferedImage bimg, int type) {
        BufferedImage resizedImage = new BufferedImage(400, 400, type);
	Graphics2D g = resizedImage.createGraphics();
	g.drawImage(bimg, 0, 0, 400, 400, null);
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
}