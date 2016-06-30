/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author obi
 */
@Entity
public class region implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="codigo_region")
    private Long id;

    @Column(name="nombre_region")
    private String nombre;
   
    @OneToMany(mappedBy = "region", fetch = FetchType.LAZY)
    private List<comuna> comunas;
    
    public List<comuna> getComunas() {
        return comunas;
    }
 
    public void setComunas(List<comuna> comunas) {
        this.comunas = comunas;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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
        if (!(object instanceof region)) {
            return false;
        }
        region other = (region) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.region[ id=" + id + " ]";
    }
        @Override
    public Object clone()
    {
        Object clone = null;
        try
        {
            clone = super.clone();
        } 
        catch(CloneNotSupportedException e)
        {
            // No deberia suceder
        }
        return clone;
    }
}
