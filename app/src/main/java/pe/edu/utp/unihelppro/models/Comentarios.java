package pe.edu.utp.unihelppro.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.marlonlom.utilities.timeago.TimeAgoMessages;
import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Comentarios extends SugarRecord implements Serializable {
    @Unique
    private String objectId;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    private String descripcion = "";
    private Comentarios comentario = null;
    private Incidentes incidente = null;
    private String estado = "";
    private String fecha = "";
    private UsuarioProperties usuario;

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

    public Comentarios getComentario() {
        return comentario;
    }

    public void setComentario(Comentarios comentario) {
        this.comentario = comentario;
    }

    public Incidentes getIncidente() {
        return incidente;
    }

    public void setIncidente(Incidentes incidente) {
        this.incidente = incidente;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public UsuarioProperties getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioProperties usuario) {
        this.usuario = usuario;
    }

    public String getTimeAgo() {
        Locale LocaleBylanguageTag = Locale.forLanguageTag("es");
        TimeAgoMessages messages = new TimeAgoMessages.Builder().withLocale(LocaleBylanguageTag).build();
        String fecha = TimeAgo.using(new Date( this.fecha ).getTime(), messages);
        return fecha;
    }
}
