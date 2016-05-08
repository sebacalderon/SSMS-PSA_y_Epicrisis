/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.security.MessageDigest;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 *
 * @author obi
 */
@Entity
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="codigo_usuario")
    private Long id;
    
    @NotNull(message="Debe ingresar un nombre")
    @Column(name="nombres_usuario",length=50)
    private String nombres;
    
    @NotNull(message="Debe ingresar el primer apellido")
    @Column(name="primer_apellido_usuario",length=30)
    private String primer_apellido;
    
    @NotNull(message="Debe ingresar el segundo apellido")
    @Column(name="segundo_apellido_usuario",length=30)
    private String segundo_apellido;
    
    @NotNull(message="Debe ingresar un run")
    @Column(name="run_usuario",length=8)
    private int RUN;
    
    @NotNull(message="Debe ingresar un dígito verificador")
    @Column(name="dv_usuario",length=1)
    private String DV;
    
    @ManyToOne
    @JoinColumn(name = "cesfam_usuario")
    private cesfam cesfam;
    
    @NotNull(message="Debe ingresar un correo")
    @Column(name="correo_usuario")
    private String correo;
    
    @NotNull(message="Debe ingresar una contraseña")
    @Column(name="password_usuario")
    private String password; 

    @NotNull(message="Debe ingresar un rol")
    @Column(name="rol_usuario")
    private String rol;  

    @Column(name="habilitado_usuario")
    private boolean habilitado;
    
    public Usuario(Usuario usuario){
        this.id = usuario.id;
        this.correo= usuario.correo;
        this.nombres= usuario.nombres;
        this.primer_apellido=usuario.primer_apellido;
        this.segundo_apellido=usuario.segundo_apellido;
        this.cesfam=usuario.cesfam;
        this.RUN=usuario.RUN;
        this.DV=usuario.DV;
        this.password= usuario.password;
        this.rol = usuario.rol;
        this.habilitado=true;
    }

    public Usuario(){
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }
    
    
    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
    
    
    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = this.sha256(password);
    }

    public boolean cambiarPassword(String old_password, String new_password) {
        if (sha256(old_password).compareTo(this.password) == 0) {
            setPassword(new_password);
            return true;
        }
        return false;
    }

    public String getPrimer_apellido() {
        return primer_apellido;
    }

    public void setPrimer_apellido(String primer_apellido) {
        this.primer_apellido = primer_apellido;
    }

    public String getSegundo_apellido() {
        return segundo_apellido;
    }

    public void setSegundo_apellido(String segundo_apellido) {
        this.segundo_apellido = segundo_apellido;
    }

    public int getRUN() {
        return RUN;
    }

    public void setRUN(int RUN) {
        this.RUN = RUN;
    }

    public String getDV() {
        return DV;
    }

    public void setDV(String DV) {
        this.DV = DV;
    }

    public cesfam getCESFAM() {
        return cesfam;
    }

    public void setCESFAM(cesfam CESFAM) {
        this.cesfam = CESFAM;
    }
    
    public String getNombres() {
        return nombres;
    }

    public void setNombre(String nombre) {
        this.nombres = nombre;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Usuario)) {
            return false;
        }
        Usuario other = (Usuario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Usuario[ id=" + id + " ]";
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
}
