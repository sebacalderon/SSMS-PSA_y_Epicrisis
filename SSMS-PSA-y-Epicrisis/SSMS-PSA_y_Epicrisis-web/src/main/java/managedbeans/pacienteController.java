package managedbeans;

import entities.comuna;
import entities.nacionalidad;
import entities.paciente;
import entities.prevision;
import entities.region;
import managedbeans.util.JsfUtil;
import managedbeans.util.JsfUtil.PersistAction;
import sessionbeans.pacienteFacadeLocal;

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
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("pacienteController")
@SessionScoped
public class pacienteController implements Serializable {

    @EJB
    private sessionbeans.pacienteFacadeLocal ejbFacade;
    private List<paciente> items = null;
    private paciente selected;
    
    
    public pacienteController() {
    }

    public paciente getSelected() {
        return selected;
    }

    public void setSelected(paciente selected) {
        this.selected = selected; 
   }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private pacienteFacadeLocal getFacade() {
        return ejbFacade;
    }

    public boolean esFONASA(){
        if(selected.getPrevision()!=null){
            return selected.getPrevision().getNombre().equals("FONASA");
        }
        return false;
    }
    
    public boolean regionSelected(){
        if(selected.getRegion_residencia()!=null){
            return true;
        }
        return false;
    }
    
    public paciente prepareCreate() {
        selected = new paciente();
        initializeEmbeddableKey();
        selected.setNombres("Sebastian");
        selected.setPrimer_apellido("Calderon");
        selected.setSegundo_apellido("Diaz");
        selected.setCalle_direccion("sadas");
        selected.setNumero_direccion("123");
        selected.setFecha_nacimiento(new java.util.Date());
        selected.setRUN(18293486);
        selected.setDV("0");
        selected.setCorreo("sebastian@algo.com");
        selected.setSexo(1);
        region Region = new region();
        long id = 1;
        Region.setId(id);
        Region.setNombre("Región de Tarapacá");
        selected.setRegion_residencia(Region);
        comuna Comuna = new comuna();
        id = 1101;
        Comuna.setId(id);
        Comuna.setNombre("Iquique");
        Comuna.setRegion(Region);
        selected.setComuna_residencia(Comuna);
        prevision Prevision = new prevision();
        id = 2;
        Prevision.setId(id);
        Prevision.setNombre("Isapre");
        selected.setPrevision(Prevision);
        nacionalidad Nacionalidad = new nacionalidad();
        id = 213;
        Nacionalidad.setId(id);        
        Nacionalidad.setNombre("Chile");
        selected.setNacionalidad(Nacionalidad);
        return selected;
    }

    public String create() {
        selected.setEstado("Ingresado");
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("pacienteCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
        return "/faces/paciente/List.xhtml";
    }

    public String update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("pacienteUpdated"));
        return "/faces/paciente/List.xhtml";
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("pacienteDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<paciente> getItems() {
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

    public paciente getpaciente(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<paciente> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<paciente> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    
    
    @FacesConverter(forClass = paciente.class)
    public static class pacienteControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            pacienteController controller = (pacienteController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "pacienteController");
            return controller.getpaciente(getKey(value));
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
            if (object instanceof paciente) {
                paciente o = (paciente) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), paciente.class.getName()});
                return null;
            }
        }

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
    
    public paciente prepareEdit() {
        selected = getSelected();
        System.out.println(selected.getNombres());
        return selected;
    }
    
    public List<paciente> completePaciente(String query) {
        List<paciente> allPacientes = getItems();
        List<paciente> filteredPacientes = new ArrayList<paciente>();
         
        for (int i = 0; i < allPacientes.size(); i++) {
            paciente Paciente = allPacientes.get(i);
            String rut = String.valueOf(Paciente.getRUN());
            if(rut.startsWith(query)) {
                filteredPacientes.add(Paciente);
            }
        }
        return filteredPacientes;
    }
}