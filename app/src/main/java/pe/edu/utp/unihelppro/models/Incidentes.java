package pe.edu.utp.unihelppro.models;

import com.backendless.BackendlessUser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.orm.SugarRecord;
import com.orm.dsl.Unique;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.io.Serializable;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Incidentes extends SugarRecord implements Serializable {
    @Unique
    private String objectId = "";
    private int comentarios = 0;
    private int megustas = 0;
    public int getComentarios() {
        return comentarios;
    }

    public void setComentarios(int comentarios) {
        this.comentarios = comentarios;
    }

    public int getMegustas() {
        return megustas;
    }

    public void setMegustas(int megustas) {
        this.megustas = megustas;
    }

    public Incidentes() {
    }

    public Incidentes(String objectId) {
        this.objectId = objectId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public UsuarioBackendless getUsuarioReceptor() {
        return usuarioReceptor;
    }

    public void setUsuarioReceptor(UsuarioBackendless usuarioReceptor) {
        this.usuarioReceptor = usuarioReceptor;
    }

    public void setupUsers( Map map ) {
        UsuarioBackendless ue = getUsuarioEmisor();
        if ( ue != null ) {
            ue.setObjectId( getUsuarioEmisor().getObjectId() );
            ue.setEmail( getUsuarioEmisor().getEmail() );
            for ( Object key : map.keySet() ) {
                if( key.equals("usuarioEmisor" ) ) {
                    ue.setName( ( (BackendlessUser) map.get( key ) ).getProperty("name").toString() );
                    ue.setSocialAccount( ( ( BackendlessUser ) map.get( key ) ).getProperty("socialAccount").toString() );
                    ue.setUserStatus( ( ( BackendlessUser ) map.get( key ) ).getProperty("userStatus").toString() );
                    ue.setTipo( ( ( BackendlessUser ) map.get( key ) ).getProperty("tipo").toString() );
                    ue.setCodigo( ( ( BackendlessUser ) map.get( key ) ).getProperty("codigo").toString() );
                }
            }
            ue.setupUser( null );
        }
        UsuarioBackendless ur = getUsuarioReceptor();
        if ( ur != null ) {
            ur.setObjectId( getUsuarioReceptor().getObjectId() );
            ur.setEmail( getUsuarioReceptor().getEmail() );
            for ( Object key : map.keySet() ) {
                if( key.equals("usuarioReceptor" ) ) {
                    ur.setName( ( ( BackendlessUser ) map.get( key ) ).getProperty("name").toString() );
                    ur.setSocialAccount( ( ( BackendlessUser ) map.get( key ) ).getProperty("socialAccount").toString() );
                    ur.setUserStatus( ( ( BackendlessUser ) map.get( key ) ).getProperty("userStatus").toString() );
                    ur.setTipo( ( ( BackendlessUser ) map.get( key ) ).getProperty("tipo").toString() );
                    ur.setCodigo( ( ( BackendlessUser ) map.get( key ) ).getProperty("codigo").toString() );
                }
            }
            ur.setupUser( null );
        }
    }

    private Boolean publico = false;
    private Boolean activarComentarios = true;
    private String aula = "";
    private String sede = "";
    private String categoria = "";
    private String descripcion = "";
    private String estado = "";
    private String foto = "";
    private String pabellon = "";
    private String fecha = "";
    private UsuarioBackendless usuarioEmisor;
    private UsuarioBackendless usuarioReceptor;

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public UsuarioBackendless getUsuarioEmisor() {
        return usuarioEmisor;
    }

    public void setUsuarioEmisor(UsuarioBackendless usuarioEmisor) {
        this.usuarioEmisor = usuarioEmisor;
    }

    public Boolean getPublico() {
        return publico;
    }

    public void setPublico(Boolean publico) {
        this.publico = publico;
    }

    public Boolean getActivarComentarios() {
        return activarComentarios;
    }

    public void setActivarComentarios(Boolean activarComentarios) {
        this.activarComentarios = activarComentarios;
    }

    public String getAula() {
        return aula;
    }

    public void setAula(String aula) {
        this.aula = aula;
    }

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPabellon() {
        return pabellon;
    }

    public void setPabellon(String pabellon) {
        this.pabellon = pabellon;
    }

    public void getData() {
        Incidentes _real = Select.from(Incidentes.class).where(Condition.prop("OBJECT_ID").eq(getObjectId())).first();
        if( _real != null ) {
            setId( _real.getId() );
        }
    }
}
