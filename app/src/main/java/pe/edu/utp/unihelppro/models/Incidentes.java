package pe.edu.utp.unihelppro.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Incidentes extends SugarRecord implements Serializable {
    @Unique
    private String objectId;

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

    private Boolean publico = false;
    private Boolean activarComentarios = true;
    private String aula = "";
    private String sede = "";
    private String categoria = "";
    private String descripcion = "";
    private String estado = "";
    private String pabellon = "";
    private UsuarioBackendless usuarioEmisor;
    private UsuarioBackendless usuarioReceptor;

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


}
