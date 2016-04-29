package entities;

import java.sql.Timestamp;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-04-28T23:01:57")
@StaticMetamodel(Auditoria.class)
public class Auditoria_ { 

    public static volatile SingularAttribute<Auditoria, Timestamp> fecha;
    public static volatile SingularAttribute<Auditoria, String> nuevoValor;
    public static volatile SingularAttribute<Auditoria, String> antiguoValor;
    public static volatile SingularAttribute<Auditoria, String> correo;
    public static volatile SingularAttribute<Auditoria, String> tabla;
    public static volatile SingularAttribute<Auditoria, Long> id;
    public static volatile SingularAttribute<Auditoria, String> operacion;

}