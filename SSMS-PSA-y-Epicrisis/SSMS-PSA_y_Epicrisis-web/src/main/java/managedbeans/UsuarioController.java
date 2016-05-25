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
import javax.faces.component.UIComponent;
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
    
    
       @Inject
    private AuditoriaController auditoriaCtrl;

       @Inject
    private LoginController loginCtrl;
       
    public AuditoriaController getAuditoriaCtrl() {
        return auditoriaCtrl;
    }

    public void setAuditoriaCtrl(AuditoriaController auditoriaCtrl) {
        this.auditoriaCtrl = auditoriaCtrl;
    }
       
    public UsuarioController() {
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
        System.out.println("prepare Create");
        selected = new Usuario();
        initializeEmbeddableKey();
        return selected;
    }

    public void prepareUpdate() {
        antiguovalor = new Usuario();
        antiguovalor.setNombres(selected.getNombres());
        antiguovalor.setCorreo(selected.getCorreo());
        antiguovalor.setPassword(selected.getPassword());
        antiguovalor.setRol(selected.getRol());
    }
    
    
    public void auditoria(String antiguo, String nuevo, String operacion) {
        auditoriaCtrl.prepareCreate();
        auditoriaCtrl.getSelected().setAntiguoValor(antiguo);
        auditoriaCtrl.getSelected().setNuevoValor(nuevo);
        auditoriaCtrl.getSelected().setOperacion(operacion);
        auditoriaCtrl.getSelected().setTabla("Usuario");
        auditoriaCtrl.ObtenerCorreo();
        Date date = new Date();
        long time = date.getTime();
        Timestamp ts = new Timestamp(time);
        auditoriaCtrl.getSelected().setFecha(ts);
        auditoriaCtrl.create();
    }
    
    public String create() {
        FacesContext context = FacesContext.getCurrentInstance();
        selected.setHabilitado(true);
        System.out.println("Rol: "+selected.getRol().getNombre());
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("UsuarioCreated"));
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
        
        if (new_password.length() > 0) {
            boolean success = selected.cambiarPassword(old_password, new_password);

            if (success) {
                JsfUtil.addSuccessMessage("Contraseña cambiada con éxito");
            } else {
                JsfUtil.addErrorMessage("No se pudo cambiar la contraseña");
            }
        }
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("UsuarioUpdated"));
    }

    public void update() {
        prepareUpdate();
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("UsuarioUpdated"));
    }

    public void desactivar() {
        selected.setHabilitado(false);
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("UsuarioDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void activar() {
        String antiguo,nuevo;
        nuevo = " ( Nombre: " + selected.getNombres() + " , Correo: " + selected.getCorreo() + " , Contraseña: " + selected.getPassword()+ " , Rol: " + selected.getRol()+", Activo: "+selected.isHabilitado()+ " )";
        selected.setHabilitado(true);
        antiguo = " ( Nombre: " + selected.getNombres() + " , Correo: " + selected.getCorreo() + " , Contraseña: " + selected.getPassword()+ " , Rol: " + selected.getRol()+", Activo: "+selected.isHabilitado()+ " )";
        auditoria(antiguo, nuevo, "Activar");
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("UsuarioUpdated"));
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
}