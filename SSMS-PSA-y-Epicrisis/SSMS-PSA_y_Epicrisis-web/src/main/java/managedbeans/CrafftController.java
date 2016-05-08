package managedbeans;

import entities.Crafft;
import managedbeans.util.JsfUtil;
import managedbeans.util.JsfUtil.PersistAction;
import sessionbeans.CrafftFacadeLocal;

import java.io.Serializable;
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

@Named("crafftController")
@SessionScoped
public class CrafftController implements Serializable {

    @EJB
    private sessionbeans.CrafftFacadeLocal ejbFacade;
    private List<Crafft> items = null;
    private Crafft selected;
    private int puntajeA;
    private int puntajeB;

    public int getPuntajeA() {
        return puntajeA;
    }

    public void setPuntajeA(int puntajeA) {
        this.puntajeA = puntajeA;
    }

    public CrafftController() {
    }

    public Crafft getSelected() {
        return selected;
    }

    public void setSelected(Crafft selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private CrafftFacadeLocal getFacade() {
        return ejbFacade;
    }

    public Crafft prepareCreate() {
        puntajeA = 0;
        puntajeB = 0;
        selected = new Crafft();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("CrafftCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("CrafftUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("CrafftDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Crafft> getItems() {
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

    public Crafft getCrafft(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Crafft> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Crafft> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Crafft.class)
    public static class CrafftControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CrafftController controller = (CrafftController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "crafftController");
            return controller.getCrafft(getKey(value));
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
            if (object instanceof Crafft) {
                Crafft o = (Crafft) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Crafft.class.getName()});
                return null;
            }
        }
    }
    public void serviceChangeA(boolean resp){
        System.out.println("Change A to: " + !resp);
       if (resp) {
            if (puntajeA>=1) {
                puntajeA-=1;
            }
        }else{
             puntajeA+=1;
        }
        System.out.println("Puntaje A = " + puntajeA);
}
    
    public void serviceChangeB(boolean resp){
        System.out.println("Change B1 to: " + !resp);
        if (resp) {
            if (puntajeB>=1) {
                puntajeB-=1;
            }
        }else{
             puntajeB+=1;
        }
        System.out.println("Puntaje B = " + puntajeB);
    }
}
