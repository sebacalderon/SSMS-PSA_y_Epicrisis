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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Seba
 */
@Entity
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

    @Column(name="actividad_realizada")
    private boolean realizada;

    @NotNull(message="Debe ingresar una actividad")
    @Column(name="nombre_actividad")
    private String actividad;
    
    @JoinColumn(name = "clap_actividad", referencedColumnName = "codigo_clap")
    @NotNull(message="Debe ingresar un clap")
    @ManyToOne(fetch = FetchType.LAZY)
    private clap clap;

    public Date getFecha_realizacion() {
        return fecha_realizacion;
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

    public boolean isRealizada() {
        return realizada;
    }

    public void setRealizada(boolean realizada) {
        this.realizada = realizada;
    }

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    public clap getClap() {
        return clap;
    }

    public void setClap(clap clap) {
        this.clap = clap;
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
