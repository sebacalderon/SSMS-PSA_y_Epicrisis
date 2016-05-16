/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Seba
 */
@Entity
public class Crafft implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_CRAFFT")
    private Long id;
    
    @NotNull(message="Debe ingresar una respuesta")
    @Column(name="A1")
    private boolean A1;
    
    @NotNull(message="Debe ingresar una respuesta")
    @Column(name="A2")
    private boolean A2;
    
    @NotNull(message="Debe ingresar una respuesta")
    @Column(name="A3")
    private boolean A3;

    @Column(name="B1")
    private boolean B1;
    
    @Column(name="B2")
    private boolean B2;
    
    @Column(name="B3")
    private boolean B3;
    
    @Column(name="B4")
    private boolean B4;
    
    @Column(name="B5")
    private boolean B5;
    
    @Column(name="B6")
    private boolean B6;
    
    @OneToOne
    @JoinColumn(name="codigo_clap")
    private clap clap;

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

    public boolean isA1() {
        return A1;
    }

    public void setA1(boolean A1) {
        this.A1 = A1;
    }

    public boolean isA2() {
        return A2;
    }

    public void setA2(boolean A2) {
        this.A2 = A2;
    }

    public boolean isA3() {
        return A3;
    }

    public void setA3(boolean A3) {
        this.A3 = A3;
    }

    public boolean isB1() {
        return B1;
    }

    public void setB1(boolean B1) {
        this.B1 = B1;
    }

    public boolean isB2() {
        return B2;
    }

    public void setB2(boolean B2) {
        this.B2 = B2;
    }

    public boolean isB3() {
        return B3;
    }

    public void setB3(boolean B3) {
        this.B3 = B3;
    }

    public boolean isB4() {
        return B4;
    }

    public void setB4(boolean B4) {
        this.B4 = B4;
    }

    public boolean isB5() {
        return B5;
    }

    public void setB5(boolean B5) {
        this.B5 = B5;
    }

    public boolean isB6() {
        return B6;
    }

    public void setB6(boolean B6) {
        this.B6 = B6;
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
        if (!(object instanceof Crafft)) {
            return false;
        }
        Crafft other = (Crafft) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.CRAFFT[ id=" + id + " ]";
    }
    
}
