/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Seba
 */
@Entity
@NamedQueries({
@NamedQuery(name="actividad.findbyPaciente",query="SELECT p FROM actividad p WHERE p.paciente=:paciente")
})
public class actividad implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="codigo_actividad")
    private Long id;
    
    @Temporal(TemporalType.DATE)
    @Column(name="fecha_realizacion_actividad")
    private java.util.Date fecha_realizacion;
    
    @Temporal(TemporalType.DATE)
    @Column(name="fecha_agendamiento_actividad")
    private java.util.Date fecha_agendamiento;

    @Column(name="consejeria_ssr_actividad")
    private boolean consejeria_ssr;

    @Column(name="consejeria_salud_mental_actividad")
    private boolean consejeria_salud_mental;
    
    @Column(name="consejeria_actividad_fisica_actividad")
    private boolean consejeria_actividad_fisica;
    
    @Column(name="consejeria_alimentacion_saludable_actividad")
    private boolean consejeria_alimentacion_saludable;
    
    @Column(name="consejeria_tabaquismo_actividad")
    private boolean consejeria_tabaquismo;
    
    @Column(name="consejeria_consumo_drogas_actividad")
    private boolean consejeria_consumo_drogas;
    
    @Column(name="consejeria_motivacional_actividad")
    private boolean consejeria_motivacional;
    
    @Column(name="consejeria_regulacion_fecundidad_actividad")
    private boolean consejeria_regulacion_fecundidad;
    
    @Column(name="consejeria_vih_its_actividad")
    private boolean consejeria_vih_its;
    
    @Column(name="ingreso_salud_mental_actividad")
    private boolean ingreso_salud_mental;
    
    @Column(name="ingreso_regulacion_fecundidad_actividad")
    private boolean ingreso_regulacion_fecundidad;
    
    @Column(name="ingreso_programa_cardiovascular_actividad")
    private boolean ingreso_programa_cardiovascular;
    
    @Column(name="consejeria_familiares_actividad")
    private boolean consejeria_familiares;
    
    @Column(name="ingreso_control_prenatal_actividad")
    private boolean ingreso_control_prenatal;
    
    @Column(name="ingreso_programa_intervencion_actividad")
    private boolean ingreso_programa_intervencion;
    
    @Column(name="vinculacion_beneficios_sociales_actividad")
    private boolean vinculacion_beneficios_sociales;

    @Column(name="nivelacion_estudios_actividad")
    private boolean nivelacion_estudios;
    
    @JoinColumn(name = "paciente_actividad", referencedColumnName = "codigo_paciente")
    @NotNull(message="Debe ingresar un usuario")
    @ManyToOne(fetch = FetchType.LAZY)
    private paciente paciente;

    @JoinColumn(name = "usuario_actividad", referencedColumnName = "codigo_usuario")
    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;
    
    public Date getFecha_realizacion() {
        return fecha_realizacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setFecha_realizacion(Date fecha_realizacion) {
        this.fecha_realizacion = fecha_realizacion;
    }

    public Date getFecha_agendamiento() {
        return fecha_agendamiento;
    }

    public void setFecha_agendamiento(Date fecha_agendamiento) {
        this.fecha_agendamiento = fecha_agendamiento;
    }

    public boolean isConsejeria_ssr() {
        return consejeria_ssr;
    }

    public void setConsejeria_ssr(boolean consejeria_ssr) {
        this.consejeria_ssr = consejeria_ssr;
    }

    public boolean isConsejeria_salud_mental() {
        return consejeria_salud_mental;
    }

    public void setConsejeria_salud_mental(boolean consejeria_salud_mental) {
        this.consejeria_salud_mental = consejeria_salud_mental;
    }

    public boolean isConsejeria_actividad_fisica() {
        return consejeria_actividad_fisica;
    }

    public void setConsejeria_actividad_fisica(boolean consejeria_actividad_fisica) {
        this.consejeria_actividad_fisica = consejeria_actividad_fisica;
    }

    public boolean isConsejeria_alimentacion_saludable() {
        return consejeria_alimentacion_saludable;
    }

    public void setConsejeria_alimentacion_saludable(boolean consejeria_alimentacion_saludable) {
        this.consejeria_alimentacion_saludable = consejeria_alimentacion_saludable;
    }

    public boolean isConsejeria_tabaquismo() {
        return consejeria_tabaquismo;
    }

    public void setConsejeria_tabaquismo(boolean consejeria_tabaquismo) {
        this.consejeria_tabaquismo = consejeria_tabaquismo;
    }

    public boolean isConsejeria_consumo_drogas() {
        return consejeria_consumo_drogas;
    }

    public void setConsejeria_consumo_drogas(boolean consejeria_consumo_drogas) {
        this.consejeria_consumo_drogas = consejeria_consumo_drogas;
    }

    public boolean isConsejeria_motivacional() {
        return consejeria_motivacional;
    }

    public void setConsejeria_motivacional(boolean consejeria_motivacional) {
        this.consejeria_motivacional = consejeria_motivacional;
    }

    public boolean isConsejeria_regulacion_fecundidad() {
        return consejeria_regulacion_fecundidad;
    }

    public void setConsejeria_regulacion_fecundidad(boolean consejeria_regulacion_fecundidad) {
        this.consejeria_regulacion_fecundidad = consejeria_regulacion_fecundidad;
    }

    public boolean isConsejeria_vih_its() {
        return consejeria_vih_its;
    }

    public void setConsejeria_vih_its(boolean consejeria_vih_its) {
        this.consejeria_vih_its = consejeria_vih_its;
    }

    public boolean isIngreso_salud_mental() {
        return ingreso_salud_mental;
    }

    public void setIngreso_salud_mental(boolean ingreso_salud_mental) {
        this.ingreso_salud_mental = ingreso_salud_mental;
    }

    public boolean isIngreso_regulacion_fecundidad() {
        return ingreso_regulacion_fecundidad;
    }

    public void setIngreso_regulacion_fecundidad(boolean ingreso_regulacion_fecundidad) {
        this.ingreso_regulacion_fecundidad = ingreso_regulacion_fecundidad;
    }

    public boolean isIngreso_programa_cardiovascular() {
        return ingreso_programa_cardiovascular;
    }

    public void setIngreso_programa_cardiovascular(boolean ingreso_programa_cardiovascular) {
        this.ingreso_programa_cardiovascular = ingreso_programa_cardiovascular;
    }

    public boolean isConsejeria_familiares() {
        return consejeria_familiares;
    }

    public void setConsejeria_familiares(boolean consejeria_familiares) {
        this.consejeria_familiares = consejeria_familiares;
    }

    public boolean isIngreso_control_prenatal() {
        return ingreso_control_prenatal;
    }

    public void setIngreso_control_prenatal(boolean ingreso_control_prenatal) {
        this.ingreso_control_prenatal = ingreso_control_prenatal;
    }

    public boolean isIngreso_programa_intervencion() {
        return ingreso_programa_intervencion;
    }

    public void setIngreso_programa_intervencion(boolean ingreso_programa_intervencion) {
        this.ingreso_programa_intervencion = ingreso_programa_intervencion;
    }

    public boolean isVinculacion_beneficios_sociales() {
        return vinculacion_beneficios_sociales;
    }

    public void setVinculacion_beneficios_sociales(boolean vinculacion_beneficios_sociales) {
        this.vinculacion_beneficios_sociales = vinculacion_beneficios_sociales;
    }

    public boolean isNivelacion_estudios() {
        return nivelacion_estudios;
    }

    public void setNivelacion_estudios(boolean nivelacion_estudios) {
        this.nivelacion_estudios = nivelacion_estudios;
    }

    public paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(paciente paciente) {
        this.paciente = paciente;
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
        if (!(object instanceof actividad)) {
            return false;
        }
        actividad other = (actividad) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.actividad[ id=" + id + " ]";
    }
    
}
