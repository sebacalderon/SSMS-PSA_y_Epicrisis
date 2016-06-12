/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 *
 * @author obi
 */
@Entity
@NamedQueries({
    @NamedQuery(name="paciente.findbyRUN",query="SELECT p FROM paciente p WHERE p.RUN=:RUN"),
    @NamedQuery(name="paciente.findbyEstadoFecha",query="SELECT p FROM paciente p WHERE p.estado=:estado AND p.fecha_estado >= :fecha"),
    @NamedQuery(name="paciente.findbyEstadoCesfamFecha",query="SELECT p FROM paciente p WHERE p.estado=:estado AND p.cesfam=:cesfam AND p.fecha_estado >= :fecha")
})
public class paciente implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="codigo_paciente")
    private Long id;

    @NotNull(message="Debe ingresar un nombre")
    @Column(name="nombres_paciente",length=50)
    private String nombres;
    
    @NotNull(message="Debe ingresar el primer apellido")
    @Column(name="primer_apellido_paciente",length=30)
    private String primer_apellido;
    
    @NotNull(message="Debe ingresar el segundo apellido")
    @Column(name="segundo_apellido_paciente",length=30)
    private String segundo_apellido;
    
    @Column(name="nombre_social_paciente")
    private String nombre_social;
    
    @NotNull(message="Debe ingresar una región de residencia")    
    @ManyToOne
    @JoinColumn(name="region_residencia_paciente")
    private region region_residencia;
    
    @NotNull(message="Debe ingresar una comuna de residencia")
    @ManyToOne
    @JoinColumn(name="comuna_residencia_paciente")
    private comuna comuna_residencia;
    
    @NotNull(message="Debe ingresar una calle o avenida o pasaje de residencia")
    @Column(name="calle_direccion_paciente",length = 50)
    private String calle_direccion;
    
    @NotNull(message="Debe ingresar el numero de dirección de residencia")
    @Column(name="numero_direccion_paciente",length = 8)
    private String numero_direccion;
    
    @Column(name="resto_direccion_paciente",length = 50)
    private String resto_direccion;
    
    @NotNull(message="Debe ingresar una fecha de nacimiento")
    @Temporal(TemporalType.DATE)
    @Column(name="fecha_nacimiento_paciente")
    private java.util.Date fecha_nacimiento;
    
    @NotNull(message="Debe ingresar un run")
    @Column(name="run_paciente",length=8,unique=true)
    private int RUN;
    
    @NotNull(message="Debe ingresar un dígito verificador")
    @Column(name="dv_paciente",length=1)
    private String DV;

    @ManyToOne
    @JoinColumn(name="cesfam_paciente")
    private cesfam cesfam;      
    
    @Column(name="telefono_fijo_paciente")
    private String telefono_fijo;
    
    @NotNull
    @Column(name="domicilio_paciente")
    private boolean domicilio;
    
    @Column(name="telefono_movil_paciente")
    private String telefono_movil;
    
    @NotNull
    @Column(name="recados_paciente")
    private boolean recados;
    
    @NotNull(message="Debe ingresar un sexo")
    @Column(name="sexo_usuario")
    private int sexo;
    
    @ManyToOne
    @JoinColumn(name="programa_social_paciente")
    private ley_social programa_social;
    
    @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\."
    + "[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
    + "(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
    message = "Debe ser un mail valido")
    @NotNull(message="Debe ingresar un correo de contacto")
    @Column(name="correo_paciente")
    private String correo;
    
    @NotNull(message="Debe ingresar un tipo de previsión")
    @ManyToOne
    @JoinColumn(name="prevision_paciente")
    private prevision prevision;
    
    @Column(name="grupo_fonasa_paciente")
    private int grupo_fonasa;    
    
    @ManyToOne
    @NotNull(message="Debe ingresar una nacionalidad")
    @JoinColumn(name="nacionalidad_paciente")
    private nacionalidad nacionalidad;
    
    @ManyToOne
    @JoinColumn(name="estado_conyugal_paciente")
    private estado_civil estado_conyugal;
    
    @ManyToOne
    @JoinColumn(name="pueblo_originario_paciente")
    private pueblo_originario pueblo_originario;
    
    @Column(name="estado_paciente")
    private String estado;
    
    @Temporal(TemporalType.DATE)
    @Column(name="fecha_estado_paciente")
    private java.util.Date fecha_estado;
    
    @OneToMany(mappedBy = "paciente", fetch = FetchType.EAGER)
    private List<clap> CLAPS;

    public List<clap> getCLAPS() {
        return CLAPS;
    }

    public void setCLAPS(List<clap> CLAPS) {
        this.CLAPS = CLAPS;
    }
    
    public String getNombre_social() {
        return nombre_social;
    }
    
    public void setNombre_social(String nombre_social) {
        this.nombre_social = nombre_social;
    }

    public java.util.Date getFecha_estado() {
        return fecha_estado;
    }

    public void setFecha_estado(java.util.Date fecha_estado) {
        this.fecha_estado = fecha_estado;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public nacionalidad getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(nacionalidad nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public int getRUN() {
        return RUN;
    }

    public void setRUN(int RUN) {
        this.RUN = RUN;
    }

    public boolean isDomicilio() {
        return domicilio;
    }

    public void setDomicilio(boolean domicilio) {
        this.domicilio = domicilio;
    }

    public boolean isRecados() {
        return recados;
    }

    public void setRecados(boolean recados) {
        this.recados = recados;
    }
    
    public String getDV() {
        return DV;
    }

    public void setDV(String DV) {
        this.DV = DV;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
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

    public int getSexo() {
        return sexo;
    }

    public void setSexo(int sexo) {
        this.sexo = sexo;
    }

    public Date getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(Date fecha_nacimiento) {
        this.fecha_nacimiento =fecha_nacimiento;
    }

    public region getRegion_residencia() {
        return region_residencia;
    }

    public void setRegion_residencia(region region_residencia) {
        this.region_residencia = region_residencia;
    }

    public comuna getComuna_residencia() {
        return comuna_residencia;
    }

    public void setComuna_residencia(comuna comuna_residencia) {
        this.comuna_residencia = comuna_residencia;
    }

    public String getCalle_direccion() {
        return calle_direccion;
    }

    public void setCalle_direccion(String calle_direccion) {
        this.calle_direccion = calle_direccion;
    }

    public String getNumero_direccion() {
        return numero_direccion;
    }

    public void setNumero_direccion(String numero_direccion) {
        this.numero_direccion = numero_direccion;
    }

    public String getResto_direccion() {
        return resto_direccion;
    }

    public void setResto_direccion(String resto_direccion) {
        this.resto_direccion = resto_direccion;
    }

    public String getTelefono_fijo() {
        return telefono_fijo;
    }

    public void setTelefono_fijo(String telefono_fijo) {
        this.telefono_fijo = telefono_fijo;
    }

    public String getTelefono_movil() {
        return telefono_movil;
    }

    public void setTelefono_movil(String telefono_movil) {
        this.telefono_movil = telefono_movil;
    }

    public ley_social getPrograma_social() {
        return programa_social;
    }

    public void setPrograma_social(ley_social programa_social) {
        this.programa_social = programa_social;
    }

    public prevision getPrevision() {
        return prevision;
    }

    public void setPrevision(prevision prevision) {
        this.prevision = prevision;
    }

    public estado_civil getEstado_conyugal() {
        return estado_conyugal;
    }

    public void setEstado_conyugal(estado_civil estado_conyugal) {
        this.estado_conyugal = estado_conyugal;
    }

    public int getGrupo_fonasa() {
        return grupo_fonasa;
    }

    public void setGrupo_fonasa(int grupo_fonasa) {
        this.grupo_fonasa = grupo_fonasa;
    }

    public pueblo_originario getPueblo_originario() {
        return pueblo_originario;
    }

    public void setPueblo_originario(pueblo_originario pueblo_originario) {
        this.pueblo_originario = pueblo_originario;
    }

    public cesfam getCesfam() {
        return cesfam;
    }

    public void setCesfam(cesfam cesfam) {
        this.cesfam = cesfam;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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
        if (!(object instanceof paciente)) {
            return false;
        }
        paciente other = (paciente) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.paciente[ id=" + id + " ]";
    }
    
}
