/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbeans;

import entities.Usuario;
import java.io.Serializable;
import java.security.MessageDigest;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author obi
 */
@Named(value = "loginController")
@SessionScoped
public class LoginController implements Serializable{

    private String rut;
    private String password;
    @Inject
    private UsuarioController userCtrl;
    
    private Usuario usuarioLogueado=null;

    public LoginController() {
        //Constructor
    }
    
    public UsuarioController getUserCtrl() {
        return userCtrl;
    }

    public void setUserCtrl(UsuarioController userCtrl) {
        this.userCtrl = userCtrl;
    }

    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public void setUsuarioLogueado(Usuario usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
    }
    
    @PostConstruct
    public void Init(){
        //Autogenerado
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
    public String login(){
        try{
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
            Usuario usuario;
            usuario = userCtrl.findByRUT(this.rut);
            System.out.println(usuario.getRol());
            if (!usuario.isHabilitado()) {
                context.addMessage(null, new FacesMessage("El funcionnario está deshabilitado"));
                return "/faces/index.xhtml";
            }else{
                if (request.getRemoteUser() == null) {
                    try {
                        request.login(this.rut, this.password);
                        usuarioLogueado = new Usuario(usuario);
                        context.addMessage(null, new FacesMessage("Funcionario autentificado correctamente"));
                    } catch (ServletException e) {
                        context.addMessage(null, new FacesMessage("El RUT y la contraseña ingresados no coinciden"));
                        return "/faces/index.xhtml";
                    }
                } else {
                    //usuario ya logueado
                    context.addMessage(null, new FacesMessage("Funcionario ya autentificado"));
                }
                return "/faces/home.xhtml";
            }
        }catch(Exception e){
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("El usuario no existe"));
            return "/faces/index.xhtml";
        }
    }
    
    public String logout(){
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try{
            request.logout();
            usuarioLogueado = null;
            return "/faces/index.xhtml";
        }catch(ServletException e){
            context.addMessage(null,new FacesMessage("Logout failed."));
            return "/faces/home.xhtml";
        }
    }

    public boolean verifyLogin(){
        boolean verify= false;
        try {//prueba
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) 
            context.getExternalContext().getRequest();//obtengo el contexto en el servidor
            
             if(request.getRemoteUser() == null){//si el usuario no esta logueado
                 verify=false;//retorna falso
             }
             else{//sino, esta logueado y retorna verdadero
                 verify=true;
             }
             
        } catch (Exception e) {//si hay algun error
            verify = false;//retorna falso
        }
        return verify;
    }
    
    private String sha256(String base){
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();
            
            for(int i =0 ;i<hash.length;i++){
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length()==1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
    
    public boolean esEmpleadoMunicipal(){
        return usuarioLogueado.getRol().getNombre().equals("Empleado-Municipal");
    }
    
    public boolean esFuncionario(){
        return usuarioLogueado.getRol().getNombre().equals("Funcionario-CESFAM");
    }
    
    public boolean esSuperUsuario(){
        return usuarioLogueado.getRol().getNombre().equals("Super-Usuario");
    }
    
    public boolean esPrimerLogin(){
        if (usuarioLogueado.isLogin_uno()== true) {
            return true;
        }else{
            return false;
        }
    }
}

