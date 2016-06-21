package managedbeans;

import entities.cesfam;
import entities.comuna;
import managedbeans.util.JsfUtil;
import managedbeans.util.JsfUtil.PersistAction;
import sessionbeans.cesfamFacadeLocal;

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

@Named("cesfamController")
@SessionScoped
public class cesfamController implements Serializable {

    @EJB
    private sessionbeans.cesfamFacadeLocal ejbFacade;
    private List<cesfam> items = null;
    private cesfam selected;
    
    @Inject
    comunaController comunaCtrl;

    public cesfamController() {
    }

    public cesfam getSelected() {
        return selected;
    }

    public void setSelected(cesfam selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private cesfamFacadeLocal getFacade() {
        return ejbFacade;
    }

    public cesfam prepareCreate() {
        selected = new cesfam();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("cesfamCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("cesfamUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("cesfamDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<cesfam> getItems() {
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

    public cesfam getcesfam(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<cesfam> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<cesfam> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = cesfam.class)
    public static class cesfamControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            cesfamController controller = (cesfamController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "cesfamController");
            return controller.getcesfam(getKey(value));
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
            if (object instanceof cesfam) {
                cesfam o = (cesfam) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), cesfam.class.getName()});
                return null;
            }
        }

    }
    
    public List<comuna> completeComuna(String query) {
        List<comuna> allComunas = comunaCtrl.getItems();
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
