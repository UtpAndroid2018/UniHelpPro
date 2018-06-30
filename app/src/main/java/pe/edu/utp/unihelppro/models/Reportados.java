package pe.edu.utp.unihelppro.models;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.marlonlom.utilities.timeago.TimeAgoMessages;
import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

public class Reportados extends SugarRecord implements Serializable {

    @Unique
    private String objectId;

    private String descripcion = "";
    private String fecha = "";
    private int calificacion = 0;
    private Incidentes incidente = null;
    private UsuarioProperties usuarioEmisor;
    private UsuarioProperties usuarioReceptor;

    public int getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Incidentes getIncidente() {
        return incidente;
    }

    public void setIncidente(Incidentes incidente) {
        this.incidente = incidente;
    }

    public UsuarioProperties getUsuarioEmisor() {
        return usuarioEmisor;
    }

    public void setUsuarioEmisor(UsuarioProperties usuarioEmisor) {
        this.usuarioEmisor = usuarioEmisor;
    }

    public UsuarioProperties getUsuarioReceptor() {
        return usuarioReceptor;
    }

    public void setUsuarioReceptor(UsuarioProperties usuarioReceptor) {
        this.usuarioReceptor = usuarioReceptor;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTimeAgo() {
        Locale LocaleBylanguageTag = Locale.forLanguageTag("es");
        TimeAgoMessages messages = new TimeAgoMessages.Builder().withLocale(LocaleBylanguageTag).build();
        String fecha = TimeAgo.using(new Date( this.fecha ).getTime(), messages);
        return fecha;
    }
}
