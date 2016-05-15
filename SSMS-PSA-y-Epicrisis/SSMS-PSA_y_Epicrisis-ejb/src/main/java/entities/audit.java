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
import javax.validation.constraints.NotNull;

/**
 *
 * @author Seba
 */
@Entity
public class audit implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_Audit")
    private Long id;
    
    @NotNull(message="Debe ingresar una respuesta")
    private int P1;
    
    @NotNull(message="Debe ingresar una respuesta")
    private int P2;
    
    @NotNull(message="Debe ingresar una respuesta")
    private int P3;

    private int P4;
    
    private int P5;
    
    private int P6;
    
    private int P7;
    
    private int P8;
    
    private int P9;
    
    private int P10;

    public int getP1() {
        return P1;
    }

    public void setP1(int P1) {
        this.P1 = P1;
    }

    public int getP2() {
        return P2;
    }

    public void setP2(int P2) {
        this.P2 = P2;
    }

    public int getP3() {
        return P3;
    }

    public void setP3(int P3) {
        this.P3 = P3;
    }

    public int getP4() {
        return P4;
    }

    public void setP4(int P4) {
        this.P4 = P4;
    }

    public int getP5() {
        return P5;
    }

    public void setP5(int P5) {
        this.P5 = P5;
    }

    public int getP6() {
        return P6;
    }

    public void setP6(int P6) {
        this.P6 = P6;
    }

    public int getP7() {
        return P7;
    }

    public void setP7(int P7) {
        this.P7 = P7;
    }

    public int getP8() {
        return P8;
    }

    public void setP8(int P8) {
        this.P8 = P8;
    }

    public int getP9() {
        return P9;
    }

    public void setP9(int P9) {
        this.P9 = P9;
    }

    public int getP10() {
        return P10;
    }

    public void setP10(int P10) {
        this.P10 = P10;
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
        if (!(object instanceof audit)) {
            return false;
        }
        audit other = (audit) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.audit[ id=" + id + " ]";
    }
    
}
