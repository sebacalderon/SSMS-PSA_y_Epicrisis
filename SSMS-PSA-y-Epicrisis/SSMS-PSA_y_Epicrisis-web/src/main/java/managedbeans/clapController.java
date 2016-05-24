package managedbeans;

import entities.Crafft;
import entities.audit;
import entities.clap;
import entities.comuna;
import entities.paciente;
import managedbeans.util.JsfUtil;
import managedbeans.util.JsfUtil.PersistAction;
import sessionbeans.clapFacadeLocal;

import java.io.Serializable;
import java.util.ArrayList;
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
    private clap selected;
    private paciente Paciente = null;
    private boolean auditCrafft = false;
    private boolean isAudit = false;
    private boolean isCrafft = false;
    private audit audit;
    private Crafft crafft;
    private int puntajeACrafft;

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
        auditCrafft = false;
        initializeEmbeddableKey();
        return selected;
    }
    
    public clap prepareEdit() {
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
        pacienteCtrl.update();
        
        if (selected.getPaciente().getCLAPS() == null){
            selected.getPaciente().setCLAPS(new ArrayList<clap>());
            selected.getPaciente().getCLAPS().add(selected);
        }else{
            selected.getPaciente().getCLAPS().add(selected);
        }
        
        for (int i = 0; i < selected.getPaciente().getCLAPS().size(); i++) {
            selected.getPaciente().getCLAPS().get(0).getPaciente().getNombres();
        }
        
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
    
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("clapCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
        return "/faces/clap/List.xhtml";
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("clapUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("clapDeleted"));
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
                return true;
            }
        }else{
            if (puntaje<=3) {
                return true;
            }
        }
        return false;
    }
}