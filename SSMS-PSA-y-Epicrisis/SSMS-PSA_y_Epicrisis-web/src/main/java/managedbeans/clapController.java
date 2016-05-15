package managedbeans;

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

    @Inject
    private pacienteController pacienteCtrl;
    
    @Inject
    private LoginController loginCtrl;    
    
    public clapController() {
    }

    public paciente getPaciente() {
        return Paciente;
    }

    public void setPaciente(paciente Paciente) {
        this.Paciente = Paciente;
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
        initializeEmbeddableKey();
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
        System.out.println("Edad: "+selected.getEdad()+"\n año paciente:"+Paciente.getFecha_nacimiento().getYear()+"\n año consulta:"+selected.getFecha_consulta().getYear());
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

}
