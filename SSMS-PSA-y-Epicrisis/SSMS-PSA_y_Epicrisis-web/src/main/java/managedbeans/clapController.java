package managedbeans;

import entities.Crafft;
import entities.audit;
import entities.cesfam;
import entities.clap;
import entities.comuna;
import entities.paciente;
import managedbeans.util.JsfUtil;
import managedbeans.util.JsfUtil.PersistAction;
import sessionbeans.clapFacadeLocal;

import java.io.Serializable;
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
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

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
    private audit audit;
    private Crafft crafft;
    private int puntajeACrafft;

    boolean vive_solo=false;
    boolean vive_en_institucion=false;
    boolean vive_con_madre=false;
    boolean vive_con_padre=false;
    boolean vive_con_otros=false;
    
    boolean actividadElegida=false;

    
    @Inject
    private pacienteController pacienteCtrl;
    
    @Inject
    private LoginController loginCtrl;    
    
    public clapController() {
    }

    public int getPuntajeACrafft() {
        return puntajeACrafft;
    }

    public void setPuntajeACrafft(int puntajeACrafft) {
        this.puntajeACrafft = puntajeACrafft;
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

    public Crafft getCrafft() {
        return crafft;
    }

    public void setCrafft(Crafft crafft) {
        this.crafft = crafft;
    }

    public boolean isAuditCrafft() {
        return auditCrafft;
    }

    public audit getAudit() {
        return audit;
    }

    public void setAudit(audit audit) {
        this.audit = audit;
    }
    
    public void setAuditCrafft(boolean auditCrafft) {
        this.auditCrafft = auditCrafft;
    }
    
    public clap getSelected() {
        return selected;
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
    
    public clap prepareEdit() {
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
        return selected;
    }

    public String create() {
        selected.setFuncionario(loginCtrl.getUsuarioLogueado());
        pacienteCtrl.setSelected(selected.getPaciente());
        pacienteCtrl.getSelected().setRUN(selected.getRUN());
        pacienteCtrl.getSelected().setDV(selected.getDV());
        pacienteCtrl.getSelected().setNombres(selected.getNombres());
        pacienteCtrl.getSelected().setNombre_social(selected.getNombre_social());
        pacienteCtrl.getSelected().setPrimer_apellido(selected.getPrimer_apellido());
        pacienteCtrl.getSelected().setSegundo_apellido(selected.getSegundo_apellido());
        pacienteCtrl.getSelected().setTelefono_fijo(selected.getTelefono_fijo());
        pacienteCtrl.getSelected().setTelefono_movil(selected.getTelefono_movil());
        pacienteCtrl.getSelected().setRegion_residencia(selected.getRegion_residencia());
        pacienteCtrl.getSelected().setComuna_residencia(selected.getComuna_residencia());
        pacienteCtrl.getSelected().setCalle_direccion(selected.getCalle_direccion());
        pacienteCtrl.getSelected().setNumero_direccion(selected.getNumero_direccion());
        pacienteCtrl.getSelected().setResto_direccion(selected.getResto_direccion());
        pacienteCtrl.getSelected().setFecha_nacimiento(selected.getFecha_nacimiento());
        pacienteCtrl.getSelected().setCesfam(selected.getCesfam());
        pacienteCtrl.getSelected().setSexo(selected.getSexo());
        pacienteCtrl.getSelected().setNacionalidad(selected.getNacionalidad());
        pacienteCtrl.getSelected().setCorreo(selected.getCorreo());
        pacienteCtrl.getSelected().setPrograma_social(selected.getPrograma_social());
        pacienteCtrl.getSelected().setPrevision(selected.getPrevision());
        pacienteCtrl.getSelected().setGrupo_fonasa(selected.getGrupo_fonasa());
        pacienteCtrl.getSelected().setEstado_conyugal(selected.getEstado_conyugal());
        pacienteCtrl.getSelected().setPueblo_originario(selected.getPueblo_originario());
        //Manejo de datos para estados de paciente y riesgos
//        pacienteCtrl.getSelected().setCLAPS(null);
//        pacienteCtrl.update();
//        
//
//        selected.getPaciente().setCLAPS(new ArrayList<clap>());
//        selected.getPaciente().getCLAPS().add(selected);
//   
//        for (int i = 0; i < selected.getPaciente().getCLAPS().size(); i++) {
//            System.out.println(selected.getPaciente().getCLAPS().get(i).getPaciente().getNombres());
//        }
        
        if (auditCrafft) {
            if (isAudit) {
                audit.setClap(selected);
                selected.setAudit(audit);
                selected.setCrafft(null);
            }else{
                crafft.setClap(selected);
                selected.setCrafft(crafft);
                selected.setAudit(null);
            }
        }
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
            selected.setEstado("Vigente");
            completo=true;
        }else{
            selected.setEstado("Incompleto");
            completo=false;
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
//        );
        
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
        //Guarda localmente el clap selected
        clap nuevoClap = getSelected();

        List<clap> claps = getItemsPorPaciente(pacienteCtrl.getSelected().getRUN());
        if(claps.size()>=1){
            for(int i=0;i<claps.size();i++){
                if(claps.get(i).getEstado().equals("Vigente")){
                    setSelected(claps.get(i));
                    selected.setEstado("Antiguo");
                    persist(PersistAction.UPDATE,"");
                }
            }
        }
        setSelected(nuevoClap);
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("clapCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
        if(completo){

            return "/faces/clap/Riesgos.xhtml";
        }else{
            return "/faces/paciente/View.xhtml";
        }
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
    
    public String update() {
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
        System.out.println("Riesgo cardiovascular: "+selected.isRiesgo_cardiovascular());
        System.out.println("Riesgo nutricional: "+selected.isRiesgo_nutricional());
        System.out.println("Riesgo OH drogas: "+selected.isRiesgo_oh_drogas());
        System.out.println("Riesgo salud mental: "+selected.isRiesgo_salud_mental());
        System.out.println("Riesgo social: "+selected.isRiesgo_social());
        System.out.println("Riesgo ssr: "+selected.isRiesgo_ssr());
        
        
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
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("UsuarioDeleted"));
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
        //Para probar con CLAP de hace mas de 2 meses
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.DATE, -61);
//        Date fecha = cal.getTime();
        selected.setFecha_consulta(new java.util.Date());
        selected.setEdad(selected.getFecha_consulta().getYear()-Paciente.getFecha_nacimiento().getYear());
        puntajeACrafft = 0;
        audit = new audit();
        crafft = new Crafft();
        isAudit = false;
        isCrafft = false;
        auditCrafft = false;
        
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
            }
        }else{
            if (auditCrafft == false) {
                if (selected.getEdad() > 9 && selected.getEdad() < 15) {
                    isAudit = true;
                    isCrafft = false;
                    auditCrafft = true;
                }
                if (selected.getEdad() > 14 && selected.getEdad() < 20) {
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
               crafft.setB1(false);
               crafft.setB2(false);
               crafft.setB3(false);
               crafft.setB4(false);
               crafft.setB5(false);
               crafft.setB6(false);
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
        return audit.getP1()+audit.getP2()+audit.getP3()+audit.getP4()+audit.getP5()+audit.getP6()+audit.getP7()+audit.getP8()+audit.getP9()+audit.getP10();
    }
    
    public boolean esIntervencionMinima() {
        int puntaje = audit.getP1()+audit.getP2()+audit.getP3();
        if (selected.getSexo() == 1) {
            if (puntaje <=4){
                audit.setP4(0);
                audit.setP5(0);
                audit.setP6(0);
                audit.setP7(0);
                audit.setP8(0);
                audit.setP9(0);
                audit.setP10(0);
                return true;
            }
        }else{
            if (puntaje<=3) {
                audit.setP4(0);
                audit.setP5(0);
                audit.setP6(0);
                audit.setP7(0);
                audit.setP8(0);
                audit.setP9(0);
                audit.setP10(0);
                return true;
            }
        }
        return false;
    }
}