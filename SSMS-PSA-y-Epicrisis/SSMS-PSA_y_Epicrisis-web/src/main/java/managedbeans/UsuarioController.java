package managedbeans;

import entities.Usuario;
import managedbeans.util.JsfUtil;
import managedbeans.util.JsfUtil.PersistAction;
import sessionbeans.UsuarioFacadeLocal;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

@Named("usuarioController")
@SessionScoped
public class UsuarioController implements Serializable {

    @EJB
    private sessionbeans.UsuarioFacadeLocal ejbFacade;
    private List<Usuario> items = null;
    private List<Usuario> todos = null;
    private Usuario selected = null;
    private Usuario antiguovalor;
    private String old_password = "";
    private String new_password = "";
    private String rep_password = "";
    private String pass_repeat;
    private String pass;
    
    @Inject
    private AuditoriaController auditoriaCtrl;

       @Inject
    private LoginController loginCtrl;
    
       public UsuarioController() {
    }
       
    public AuditoriaController getAuditoriaCtrl() {
        return auditoriaCtrl;
    }

    public String getPass_repeat() {
        return pass_repeat;
    }

    public void setPass_repeat(String pass_repeat) {
        this.pass_repeat = pass_repeat;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
   
    public void setAuditoriaCtrl(AuditoriaController auditoriaCtrl) {
        this.auditoriaCtrl = auditoriaCtrl;
    }

    public String getOld_password() {
        return old_password;
    }

    public void setOld_password(String old_password) {
        this.old_password = old_password;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }

    public String getRep_password() {
        return rep_password;
    }

    public void setRep_password(String rep_password) {
        this.rep_password = rep_password;
    }

    public Usuario getAntiguovalor() {
        return antiguovalor;
    }

    public void setAntiguovalor(Usuario antiguovalor) {
        this.antiguovalor = antiguovalor;
    }
    

    public Usuario getSelected() {
        return selected;
    }

    public void setSelected(Usuario selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private UsuarioFacadeLocal getFacade() {
        return ejbFacade;
    }

    public Usuario prepareCreate() {
        selected = new Usuario();
        initializeEmbeddableKey();
        return selected;
    }

    public void prepareUpdate() {
        antiguovalor = (Usuario) selected.clone();
    }
    
    

    
    public String create() {
        FacesContext context = FacesContext.getCurrentInstance();
        selected.setHabilitado(true);
        selected.setLogin_uno(true);
        String rut = selected.getRUT();
        if (selected.getRUT().charAt(0) == '0') {
            String pass = new StringBuilder().append(rut.charAt(1)).append(rut.charAt(3)).append(rut.charAt(4)).append(rut.charAt(5)).toString();
            selected.setPassword(pass);
        }else{
            String pass = new StringBuilder().append(rut.charAt(0)).append(rut.charAt(1)).append(rut.charAt(3)).append(rut.charAt(4)).toString();
            selected.setPassword(pass);
        }
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("UsuarioCreated"));
        auditoriaCtrl.auditUsuario(new Usuario(), selected, "CREATE");
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
        return "/faces/usuario/List.xhtml";
    }
    
    public boolean preparaPerfil(){
        selected=new Usuario(loginCtrl.getUsuarioLogueado());
        return true;
    }
    
    public void cambioPassword(){
        
        if (!rep_password.equals(new_password)) {
            JsfUtil.addErrorMessage("Las contraseñas no coinciden");
        }else{
        
            if (new_password.length() > 0) {
                selected=new Usuario(loginCtrl.getUsuarioLogueado());
                Usuario antiguo=(Usuario)selected.clone();
                boolean success = selected.cambiarPassword(old_password, new_password);
                Usuario nuevo=(Usuario)selected.clone();
                if (success) {
                    JsfUtil.addSuccessMessage("Contraseña cambiada con éxito");
                    persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("UsuarioUpdated"));
                    auditoriaCtrl.auditUsuario(antiguo, nuevo, "UPDATE");

                } else {
                    JsfUtil.addErrorMessage("La contraseña antigua es incorrecta");
                }
            }
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("UsuarioUpdated"));
        auditoriaCtrl.auditUsuario(antiguovalor, selected, "UPDATE");
    }

    public void validaRun(FacesContext context, UIComponent toValidate, Object value) {

        try {
            context = FacesContext.getCurrentInstance();
            FacesMessage message = null;
            String rut = (String) value;
            rut =  rut.toUpperCase();
            rut = rut.replace(".", "");
            rut = rut.replace("-", "");
            int rutAux = Integer.parseInt(rut.substring(0, rut.length() - 1));

            char dv = rut.charAt(rut.length() - 1);

            int m = 0, s = 1;
            for (; rutAux != 0; rutAux /= 10) {
                s = (s + rutAux % 10 * (9 - m++ % 6)) % 11;
            }
            if (dv == (char) (s != 0 ? s + 47 : 75)) {
                ((UIInput) toValidate).setValid(true);
            }else{
                ((UIInput) toValidate).setValid(false);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: El RUN ingresado no es válido.",  "El rut ingresado no es válido.") );
            }

        } catch (java.lang.NumberFormatException e) {
        } catch (Exception e) {
        }
    }
    
    
    public void desactivar() {
        Usuario antiguo=(Usuario)selected.clone();
        selected.setHabilitado(false);
        Usuario nuevo=(Usuario)selected.clone();
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("UsuarioDeleted"));
        auditoriaCtrl.auditUsuario(antiguo, nuevo, "UPDATE");
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void activar() {
        Usuario antiguo=(Usuario)selected.clone();
        selected.setHabilitado(true);
        Usuario nuevo=(Usuario)selected.clone();
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("UsuarioUpdated"));
        auditoriaCtrl.auditUsuario(antiguo, nuevo, "UPDATE");
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }
    
    public boolean isSuperUsuario(){
        if (selected.getId() == null) {
            return true;
        }else{
            return selected.getRol().getNombre().equals("Super-Usuario");
        }
    }
    
    public Usuario findByRUT(String rut) {
        getAllItems();//todos los items
        for (Usuario item : todos) {//para cada item de Preingreso de la bd
            if (rut.equals(item.getRUT())) {//si el objeto a comparar es igual al rut de entrada
                setSelected(item);
                return item;//se retorna
            }
        }
        return null;//si no se encuentra retorna nulo
    }
    
    public List<Usuario> getAllItems() {
        todos = getFacade().findAll();
        if (todos == null) {
            todos = new ArrayList<Usuario>();
        }
        return todos;
    }
    
    public List<Usuario> getItems() {
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

    public Usuario getUsuario(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Usuario> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Usuario> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Usuario.class)
    public static class UsuarioControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UsuarioController controller = (UsuarioController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "usuarioController");
            return controller.getUsuario(getKey(value));
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
            if (object instanceof Usuario) {
                Usuario o = (Usuario) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Usuario.class.getName()});
                return null;
            }
        }

    }
    
    public void login_uno() {
        FacesContext context;
        context = FacesContext.getCurrentInstance();
        FacesMessage message = new FacesMessage();
        if (pass.equals(pass_repeat)) {
            setOld_password(selected.getPassword());
            setNew_password(pass);
            selected.setLogin_uno(false);
            selected.setPassword(pass);
            JsfUtil.addSuccessMessage("Contraseña cambiada con éxito");
            persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("UsuarioUpdated"));
        }else{
            context.addMessage(null, new FacesMessage(message.SEVERITY_ERROR, "Error: Las contraseñas no coinciden", ""));
        }
        loginCtrl.setUsuarioLogueado(selected);
    }
}