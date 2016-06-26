package managedbeans;

import entities.Auditoria;
import entities.Usuario;
import managedbeans.util.JsfUtil;
import managedbeans.util.JsfUtil.PersistAction;
import sessionbeans.AuditoriaFacadeLocal;

import java.io.Serializable;
import java.sql.Timestamp;
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

@Named("auditoriaController")
@SessionScoped
public class AuditoriaController implements Serializable {
    @Inject
    private LoginController loginCtrl;
    @EJB
    private AuditoriaFacadeLocal ejbFacade;
    private List<Auditoria> items = null;
    private Auditoria selected;

    public AuditoriaController() {
    }

    
    public LoginController getLoginCtrl() {
        return loginCtrl;
    }

    public void setLoginCtrl(LoginController loginCtrl) {
        this.loginCtrl = loginCtrl;
    }

    public Auditoria getSelected() {
        return selected;
    }

    public void setSelected(Auditoria selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private AuditoriaFacadeLocal getFacade() {
        return ejbFacade;
    }

    public Auditoria prepareCreate() {
        selected = new Auditoria();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("AuditoriaCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("AuditoriaUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("AuditoriaDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Auditoria> getItems() {
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

    public void auditUsuario(Usuario antiguo, Usuario nuevo, String operacion) {
        selected=new Auditoria();
        String string_antiguo,string_nuevo;
        if(operacion.equals("UPDATE")){
            string_antiguo = " ( ID: "+antiguo.getId()+" , Nombres: " + antiguo.getNombres() +" , Primer apellido: " + antiguo.getPrimer_apellido()+" , Segundo apellido: " + antiguo.getSegundo_apellido()+ " , Correo: " + antiguo.getCorreo()+" , RUT: " + antiguo.getRUT()+" , Cesfam: " + antiguo.getCESFAM()+ " , Contraseña: " + antiguo.getPassword()+ " , Rol: " + antiguo.getRol()+", Habilitado: "+antiguo.isHabilitado()+ " , Login uno: " + antiguo.isLogin_uno()+" )";
//            System.out.println(string_antiguo);
            selected.setAntiguoValor(string_antiguo);
        }
        string_nuevo = " ( ID: "+nuevo.getId()+" , Nombres: " + nuevo.getNombres() +" , Primer apellido: " + nuevo.getPrimer_apellido()+" , Segundo apellido: " + nuevo.getSegundo_apellido()+ " , Correo: " + nuevo.getCorreo()+" , RUT: " + nuevo.getRUT()+" , Cesfam: " + nuevo.getCESFAM()+ " , Contraseña: " + nuevo.getPassword()+ " , Rol: " + nuevo.getRol()+", Habilitado: "+nuevo.isHabilitado()+ " , Login uno: " + nuevo.isLogin_uno()+" )";
        selected.setNuevoValor(string_nuevo);
        selected.setOperacion(operacion);
        selected.setTabla("Usuario");
        selected.setRut_usuario(loginCtrl.getUsuarioLogueado().getRUT());
        Date date = new Date();
        long time = date.getTime();
        Timestamp ts = new Timestamp(time);
        selected.setFecha(ts);
        getFacade().create(selected);
    }
    
    public Auditoria getAuditoria(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Auditoria> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Auditoria> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Auditoria.class)
    public static class AuditoriaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AuditoriaController controller = (AuditoriaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "auditoriaController");
            return controller.getAuditoria(getKey(value));
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
            if (object instanceof Auditoria) {
                Auditoria o = (Auditoria) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Auditoria.class.getName()});
                return null;
            }
        }

    }

}
