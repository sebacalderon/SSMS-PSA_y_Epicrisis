package managedbeans;

import entities.cesfam;
import entities.comuna;
import entities.ley_social;
import entities.nacionalidad;
import entities.paciente;
import entities.prevision;
import entities.region;
import java.io.IOException;
import java.io.OutputStream;
import managedbeans.util.JsfUtil;
import managedbeans.util.JsfUtil.PersistAction;
import sessionbeans.pacienteFacadeLocal;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

//fonasa
import ws.cl.gov.fonasa.certificadorprevisional.WsBuscarCmdFoService;
import model.CmdDatosFo;

@Named("pacienteController")
@SessionScoped
public class pacienteController implements Serializable {

    @EJB
    private sessionbeans.pacienteFacadeLocal ejbFacade;
    private List<paciente> items = null;
    private paciente selected;
    private int RUN;
    private String DV;
    private cesfam cesfam = null;
    private List<paciente> itemsRiesgo = null;
    private List<paciente> itemsCESFAM = null;
    
    @Inject
    private LoginController loginCtrl;
    
    @Inject
    private clapController clapCtrl;
    
    @Inject
    private comunaController comunaCtrl;
    
    @Inject
    private previsionController previsionCtrl;
    
    @Inject
    private nacionalidadController nacionalidadCtrl;
    
    @Inject
    private regionController regionCtrl;
    
    @Inject
    private ley_socialController ley_socialCtrl;
    
    public pacienteController() {
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

    public List<paciente> getItemsCESFAM() {
        return itemsCESFAM;
    }

    public void setItemsCESFAM(List<paciente> itemsCESFAM) {
        this.itemsCESFAM = itemsCESFAM;
    }
    
    public cesfam getCesfam() {
        return cesfam;
    }

    public void setCesfam(cesfam cesfam) {
        this.cesfam = cesfam;
    }
 
    public paciente getSelected() {
        return selected;
    }

    public void setSelected(paciente selected) {
        this.selected = selected; 
   }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private pacienteFacadeLocal getFacade() {
        return ejbFacade;
    }

    public boolean esFONASA(){
        if(selected.getPrevision()!=null){
            return selected.getPrevision().getNombre().equals("FONASA");
        }
        return false;
    }
    
    public boolean regionSelected(){
        if(selected.getRegion_residencia()!=null){
            return true;
        }
        return false;
    }
    
    public paciente prepareCreate() {
        selected = new paciente();
        initializeEmbeddableKey();
        selected.setRUN(RUN);
        selected.setDV(DV);
        selected.setCesfam(loginCtrl.getUsuarioLogueado().getCESFAM());
        return selected;
    }

    public String create() {
        FacesContext context;
        context = FacesContext.getCurrentInstance();
        FacesMessage message = null; 
        
        boolean flag = verificaRun();
                
        if (flag) {
            selected.setEstado("Ingresado");
            selected.setFecha_estado(new java.util.Date());
            persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("pacienteCreated"));
            if (!JsfUtil.isValidationFailed()) {
                items = null;    // Invalidate list of items to trigger re-query.
                selected = getFacade().findbyRUN(selected.getRUN()).get(0);
                clapCtrl.setSelected(null);
                return "/faces/paciente/View.xhtml";
            }else{
                return "/faces/paciente/Create.xhtml";
            }
        }else{
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: el RUN ingresado no es válido",  "El rut ingresado no es válido.") );
            return "/faces/paciente/Create.xhtml";
        }
    }

    public void riesgosNoTratados() {
        selected.setEstado("Riesgos no Tratados");
        selected.setFecha_estado(new java.util.Date());
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("pacienteUpdated"));
    }

    public void tratarRiesgo(){
        selected.setEstado("Riesgos tratados o en tratamiento");
        selected.setFecha_estado(new java.util.Date());
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("pacienteUpdated"));
        clapCtrl.setActividadElegida(true);
    }
    
    public void sinRiesgo(){
        selected.setEstado("Sin riesgo");
        selected.setFecha_estado(new java.util.Date());
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("pacienteUpdated"));
    }
    
    public void clapIncompleto(){
        selected.setEstado("Clap Incompleto");
        selected.setFecha_estado(new java.util.Date());
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("pacienteUpdated"));
    }
    
    public String update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("pacienteUpdated"));
        return "/faces/paciente/View.xhtml?faces-redirect=true";
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("pacienteDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<paciente> getItemsRiesgoHome(int num) {
        //Si es super usuario, no hay filtro por cesfam
        Date fecha = new java.util.Date();
        Calendar c = Calendar.getInstance();
        c.setTime(fecha);
        c.add(Calendar.DATE, -60);
        fecha = c.getTime();
        
        if (loginCtrl.esSuperUsuario()) {
            if (num == 1) {
               itemsRiesgo = getFacade().findbyEstadoFecha("Riesgos no Tratados", fecha);
               return itemsRiesgo;
           }else{
               return itemsRiesgo;
           }   
        }else{
            if (num == 1) {
                itemsRiesgo = getFacade().findbyEstadoFecha("Riesgos no Tratados",fecha);
                return itemsRiesgo;
            }else{
                return itemsRiesgo;
            }
        }
    }
    
    public List<paciente> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                    if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public paciente getpaciente(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<paciente> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<paciente> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public boolean verificaRun(){
        String rut2 = "";

        rut2 = String.valueOf(selected.getRUN());
 
        rut2 = Integer.toString(selected.getRUN());
        
        boolean flag = false; 
        String rut = rut2.trim(); 

        String posibleVerificador = selected.getDV().trim(); 
        int cantidad = rut.length(); 
        int factor = 2; 
        int suma = 0; 
        String verificador = ""; 

        for(int i = cantidad; i > 0; i--) 
        { 
            if(factor > 7) 
            { 
                factor = 2; 
            } 
            suma += (Integer.parseInt(rut.substring((i-1), i)))*factor; 
            factor++; 

        } 
        verificador = String.valueOf(11 - suma%11); 
        if(verificador.equals(posibleVerificador)) 
        { 
            flag = true; 
        } 
        else 
        { 
            if((verificador.equals("10")) && (posibleVerificador.toLowerCase().equals("k"))) 
            { 
                flag = true; 
            } 
            else 
            { 
                if((verificador.equals("11") && posibleVerificador.equals("0"))) 
                { 
                    flag = true; 
                } 
                else 
                { 
                    flag = false; 
                } 
            } 
        }
        return flag;
    }

    
    @FacesConverter(forClass = paciente.class)
    public static class pacienteControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            pacienteController controller = (pacienteController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "pacienteController");
            return controller.getpaciente(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof paciente) {
                paciente o = (paciente) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), paciente.class.getName()});
                return null;
            }
        }

    }
    
    public List<comuna> completeComuna(String query) {
        List<comuna> allComunas = selected.getRegion_residencia().getComunas();
        List<comuna> filteredComunas = new ArrayList<comuna>();
         
        for (int i = 0; i < allComunas.size(); i++) {
            comuna Comuna = allComunas.get(i);
            if(Comuna.getNombre().toLowerCase().startsWith(query.toLowerCase())) {
                filteredComunas.add(Comuna);
            }
        }
         
        return filteredComunas;
    }
    
    public paciente prepareEdit() {
        selected = getSelected();
        return selected;
    }
    
    public List<paciente> completePaciente(String query) {
        List<paciente> allPacientes = getItems();
        List<paciente> filteredPacientes = new ArrayList<paciente>();
         
        for (int i = 0; i < allPacientes.size(); i++) {
            paciente Paciente = allPacientes.get(i);
            String rut = String.valueOf(Paciente.getRUN());
            if(rut.startsWith(query)) {
                filteredPacientes.add(Paciente);
            }
        }
        return filteredPacientes;
    }
    

    public String buscarPorRUN(){
        selected = new paciente();
        selected.setRUN(RUN);
        selected.setDV(DV);
        boolean flag = verificaRun();
        selected = null;
        if (!flag) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "RUN Invalido",  null);
            FacesContext.getCurrentInstance().addMessage(null, message);
            return "/faces/paciente/Buscar.xhtml";
        }

        List<paciente> pacientes = getFacade().findbyRUN(RUN);      
        //Si no existe el paciente en el sistema, primero se busca en FONASA
        if (pacientes.isEmpty()) {
            CmdDatosFo datos = null;
            WsBuscarCmdFoService mifo = new WsBuscarCmdFoService();
            datos = mifo.getWsBuscarCmdFo().buscarDatosBeneficiarios(RUN, DV, 4);
            /////////////////
            //PARTE DE FONASA
            ////////////////
            FacesMessage message;
            int estado = datos.getEstado();
            System.out.println("El cod de estado es: "+estado);
            switch (estado) {
                //Fallo Ataque
                case -21:
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Fonasa: Fallo Ataque",  null);
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return "/faces/paciente/Buscar.xhtml";
                //Fallo Llamada
                case -22:
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Fonasa: Fallo Llamada",  null);
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return "/faces/paciente/Buscar.xhtml";
                //Fallo Conexión
                case -23:
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Fonasa: Fallo Conexión",  null);
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return "/faces/paciente/Buscar.xhtml";
                //Fallo Autenticación 
                case -31:
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Fonasa: Fallo Autenticación",  null);
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return "/faces/paciente/Buscar.xhtml";
                //Fallo Numero de Peticiones
                case -32:
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Fonasa: Fallo Número de Peticiones",  null);
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return "/faces/paciente/Buscar.xhtml";
                //Fallo de Horario    
                case -33:
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Fonasa: Fallo de Horario",  null);
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return "/faces/paciente/Buscar.xhtml";
                //Exito en la ejecucion    
                case 0:
                    
                    if (datos.getEstadoFallecido().equals("2") || datos.getCodcybl().equals("1902")) {
                        message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuario Fallecido.",  null);
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        return "/faces/paciente/Buscar.xhtml";
                    }else{
                    //Se setean datos del usuario encontrado
                        prepareCreate();
                        selected.setNombres(datos.getNombres());
                        selected.setPrimer_apellido(datos.getApell1());
                        selected.setSegundo_apellido(datos.getApell2());
                        SimpleDateFormat formatoDelTexto = new SimpleDateFormat("yyyy-MM-dd");
                        String fecha_nacimiento = datos.getFechaNacimiento();
                        try {
                            Date fecha = formatoDelTexto.parse(fecha_nacimiento);
                            selected.setFecha_nacimiento(fecha);
                        }catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                        long l = Long.parseLong(datos.getCdgComuna().trim());
                        // System.out.println("Comuna codigo: "+datos.getCdgComuna());
                        System.out.println("Comuna codigo: "+ l);


                        System.out.println("Comuna : "+datos.getDesComuna());
                        comuna comuna = comunaCtrl.getcomuna(l);
                        if (comuna != null) {
                            selected.setComuna_residencia(comuna);
                        }

                        if (datos.getGenero().equals("M")) {
                            selected.setSexo(1);
                        }else{
                            selected.setSexo(2);
                        }

                       //fonasa
                        if (datos.getTramoFo()!=null||datos.getCodcybl().equals("00131") || datos.getCodcybl().equals("00110")) {
                            System.out.println("Tramo "+datos.getTramoFo());
                            if (datos.getTramoFo().trim().equals("A")) {
                                selected.setGrupo_fonasa(1);
                            }else if (datos.getTramoFo().trim().equals("B")){
                                selected.setGrupo_fonasa(2);                    
                            }else if (datos.getTramoFo().trim().equals("C")){
                                selected.setGrupo_fonasa(3);
                            }else if (datos.getTramoFo().trim().equals("D")){
                                selected.setGrupo_fonasa(4);
                            }
                            prevision prevision = previsionCtrl.getprevision(Long.parseLong("999".trim()));
                            selected.setPrevision(prevision);
                        }
                        //isapre
                        
                        if(datos.getCodcybl().equals("01901")){
                            System.out.println("Codigo Prevision: "+datos.getCdgIsapre());
                            prevision prevision = previsionCtrl.getprevision(Long.parseLong(datos.getCdgIsapre().trim()));
                            selected.setPrevision(prevision);
                        }
                        //capredena
                        if(datos.getCodcybl().equals("01911")){
                            prevision prevision = previsionCtrl.getprevision(Long.parseLong("998".trim()));
                            selected.setPrevision(prevision);
                        }

                        // dipreca -bh -i
                         if(datos.getCodcybl().equals("01972")){
                            System.out.println("Codigo Prevision: "+datos.getCdgIsapre());
                            prevision prevision = previsionCtrl.getprevision(Long.parseLong("997".trim()));
                            selected.setPrevision(prevision);
                        }
                         
                        //otros casos - asignar otros 
                        if(datos.getCodcybl().equals(" ")||datos.getCodcybl()==null||datos.getCodcybl().equals("01903")){
                            prevision prevision = previsionCtrl.getprevision(Long.parseLong("1000".trim()));
                            selected.setPrevision(prevision);
                        }
                        
                        
                        
                        l = Long.parseLong(datos.getCdgNacionalidad().trim());
                        System.out.println("Nacionalidad codigo: "+datos.getCdgNacionalidad());
                        System.out.println("Nacionalidad : "+datos.getDesNacionalidad());
                        nacionalidad nacionalidad = nacionalidadCtrl.getnacionalidad(l);
                        if (nacionalidad != null) {
                            selected.setNacionalidad(nacionalidad);
                        }

                        //prais
                        if(datos.getCodigoprais().trim().equals("111")){
                            ley_social ley_social = ley_socialCtrl.getley_social(Long.parseLong("5".trim()));
                            selected.setPrograma_social(ley_social);
                        }else{ //por ahora se asigna 10 como otro
                            ley_social ley_social = ley_socialCtrl.getley_social(Long.parseLong("10".trim()));
                            selected.setPrograma_social(ley_social);
                        }
                        
                        selected.setCalle_direccion(datos.getDireccion());

                        //l = Long.parseLong(datos.getCdgRegion().trim()); 
                        l=new Long(13);
                        region region = regionCtrl.getregion(l);
                        selected.setRegion_residencia(region);

                        System.out.println(datos.getTelefono());
                        selected.setTelefono_fijo(datos.getTelefono());

                        System.out.println("Prevision: "+datos.getDesIsapre());
                        System.out.println("Codigo Prevision: "+datos.getCdgIsapre());
                        System.out.println("Tramo: "+datos.getTramoFo());
                        System.out.println("CYBL: "+datos.getCodcybl());
                        System.out.println("COD DESC: "+datos.getCoddesc());
                        System.out.println("PRAIS: "+datos.getDescprais());
                        

                        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Usuario Encontrado en FONASA.",  null);
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        return "/faces/paciente/Create.xhtml";
                    }
                //XML no valido
                case 1:
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Fonasa: XML no válido",  null);
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return "/faces/paciente/Buscar.xhtml";
                //Error del servicio
                case 9:
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Fonasa: Error del Servicio",  null);
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return "/faces/paciente/Buscar.xhtml";
                //Error en conectividad con servicio externo
                case -1:
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Fonasa: Error en conectividad con servicio externo",  null);
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return "/faces/paciente/Buscar.xhtml";
                //Error interno    
                case -2:
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Fonasa: Error interno",  null);
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return "/faces/paciente/Buscar.xhtml";
                //El rut no es beneficiario
                case -3:
                    //Si no es beneficiario, existen datos ??
                    return "";
                //-4 No existe rut en la bd    
                default:
                    //Si es funcionario o encargado se puede crear
                    if (loginCtrl.esFuncionario() || loginCtrl.esEncargadoPrograma()) {
                        prepareCreate();
                        message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuario no Encontrado en FONASA. Cree el registro",  null);
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        return "/faces/paciente/Create.xhtml";
                    //Si es SuperUsuario, no puede crear
                    }else{
                        message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuario no Encontrado",  null);
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        return "/faces/paciente/Buscar.xhtml";
                    }
            }
        }else{
            paciente paciente = pacientes.get(0);
            if (!paciente.getDV().equals(DV)) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "RUN Invalido",  null);
                FacesContext.getCurrentInstance().addMessage(null, message);
                return "/faces/paciente/Buscar.xhtml";
            }else{
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Usuario Encontrado",  null);
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    selected = paciente;
                    return "/faces/paciente/View.xhtml";    
            }
        }
    }
    
    public String pacienteClap(paciente paciente){
        setSelected(paciente);
        return "/paciente/View?faces-redirect=true";
    }
    
    public String reporteCESFAM(){
        setItemsCESFAM(getFacade().findbyCESFAM(cesfam));
        return "/faces/reportes/resultado_poblacion.xhtml";
    }
    
    public int getTamano(){
        if (itemsCESFAM==null) {
            itemsCESFAM = getFacade().findbyCESFAM(loginCtrl.getUsuarioLogueado().getCESFAM());
        }
        return itemsCESFAM.size();        
    }
    
    public void generarCSV(int condicion)
    {
        String nombreCesfam;
        String title;
        if (condicion == 0 ) {
            title = "poblacion.csv";
        }else{
            if (cesfam == null) {
                nombreCesfam = loginCtrl.getUsuarioLogueado().getCESFAM().getNombre();
                title = nombreCesfam+".csv";
            }else{
                nombreCesfam = getCesfam().getNombre();
                title = nombreCesfam+".csv";
            }
        }
        
        try
        {
            
            FacesContext fc = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();

            response.reset();
            response.setContentType("text/comma-separated-values");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + title + "\"");
            
            OutputStream output = response.getOutputStream();
            
            //Nombre de las columnas
            output.write("RUN;".getBytes());
            output.write("Nombre".getBytes());
            output.write("\n".getBytes());
            
            if (condicion == 0) {
                items = getItems();
                for (int i =0;i<items.size();i++) {
                    paciente item =items.get(i);
                    String fila = item.getRUN()+"-"+item.getDV()+";"+item.getNombres()+" "+item.getPrimer_apellido()+" "+item.getSegundo_apellido();
                    output.write(fila.getBytes());
                    output.write("\n".getBytes());
                }
            }else{
                for (int i =0;i<itemsCESFAM.size();i++) {
                    paciente item =itemsCESFAM.get(i);
                    String fila = item.getRUN()+"-"+item.getDV()+";"+item.getNombres()+" "+item.getPrimer_apellido()+" "+item.getSegundo_apellido();
                    output.write(fila.getBytes());
                    output.write("\n".getBytes());
                }
            }

            output.flush();
            output.close();
            
            fc.responseComplete();

            
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
}