package managedbeans;

import entities.nacionalidad;
import managedbeans.util.JsfUtil;
import managedbeans.util.JsfUtil.PersistAction;
import sessionbeans.nacionalidadFacadeLocal;

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

@Named("nacionalidadController")
@SessionScoped
public class nacionalidadController implements Serializable {

    @EJB
    private sessionbeans.nacionalidadFacadeLocal ejbFacade;
    private List<nacionalidad> items = null;
    private nacionalidad selected;

    private String nacionalidad;

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }
    
    
    public nacionalidadController() {
    }

    public nacionalidad getSelected() {
        return selected;
    }

    public void setSelected(nacionalidad selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private nacionalidadFacadeLocal getFacade() {
        return ejbFacade;
    }

    public nacionalidad prepareCreate() {
        selected = new nacionalidad();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("nacionalidadCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("nacionalidadUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("nacionalidadDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<nacionalidad> getItems() {
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

    public nacionalidad getnacionalidad(java.lang.Long id) {
        System.out.println("ID_Nacionalidad: "+id);
        switch(id.intValue()){
            case 1:
                return getFacade().find(new Long(242));
            case 2:
                return getFacade().find(new Long(204));
            case 3:
                return getFacade().find(new Long(210));
            case 4:
                return getFacade().find(new Long(414));
            case 5:
                return getFacade().find(new Long(241));
            case 6:
                return getFacade().find(new Long(218));
            case 7:
                return getFacade().find(new Long(214));
            case 8:
                return getFacade().find(new Long(402));
            case 9:
                return getFacade().find(new Long(411));
            case 11:
                return getFacade().find(new Long(441));
            case 12:
                return getFacade().find(new Long(433));
            case 13:
                return getFacade().find(new Long(442));
            case 14:
                return getFacade().find(new Long(212));
            case 56:
                return getFacade().find(new Long(213));
            default:
                return getFacade().find(new Long(999));
            
        }
    }

    public List<nacionalidad> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<nacionalidad> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = nacionalidad.class)
    public static class nacionalidadControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            nacionalidadController controller = (nacionalidadController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "nacionalidadController");
            return controller.getnacionalidad(getKey(value));
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
            if (object instanceof nacionalidad) {
                nacionalidad o = (nacionalidad) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), nacionalidad.class.getName()});
                return null;
            }
        }

    }
    
    public List<nacionalidad> completeNacionalidad(String query) {
    List<nacionalidad> allNacionalidades = getItems();
        List<nacionalidad> filteredNacionalidades = new ArrayList<nacionalidad>();
         
        for (int i = 0; i < allNacionalidades.size(); i++) {
            nacionalidad Nacionalidad = allNacionalidades.get(i);
            if(Nacionalidad.getNombre().toLowerCase().startsWith(query.toLowerCase())) {
                filteredNacionalidades.add(Nacionalidad);
            }
        }
         
        return filteredNacionalidades;
    }
}
