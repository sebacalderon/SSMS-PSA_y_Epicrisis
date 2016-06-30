/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbeans;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import entities.Crafft;
import entities.audit;
import entities.cesfam;
import entities.clap;
import entities.comuna;
import entities.paciente;
import entities.region;

/**
 *
 * @author obi
 */
  public class MyExclusionStrategy implements ExclusionStrategy {
//    private final Class<?> typeToSkip;

//    public MyExclusionStrategy(Class<?> typeToSkip) {
//      this.typeToSkip = typeToSkip;
//    }

//    public boolean shouldSkipClass(Class<?> clazz) {
//      return (clazz == typeToSkip);
//    }
    
    public boolean shouldSkipClass(Class<?> clazz) {
      return false;
    }
    
    public boolean shouldSkipField(FieldAttributes f) {
        return (f.getDeclaringClass() ==  paciente.class && f.getName().equals("CLAPS"))||
        (f.getDeclaringClass() == clap.class && f.getName().equals("paciente"))||
        (f.getDeclaringClass() == region.class && f.getName().equals("comunas"))||
        (f.getDeclaringClass() == comuna.class && f.getName().equals("region"))||
        (f.getDeclaringClass() == cesfam.class && f.getName().equals("comuna"))||
        (f.getDeclaringClass() == Crafft.class && f.getName().equals("clap"))||
        (f.getDeclaringClass() == audit.class && f.getName().equals("clap"))||
        f.getName().equals("row")||
        f.getName().startsWith("_");
        
    }
  }