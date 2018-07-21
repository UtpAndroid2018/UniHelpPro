package pe.edu.utp.unihelppro.models;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import java.io.Serializable;

public class Likes extends SugarRecord implements Serializable {
    @Unique
    private String objectId;
    private Incidentes incidente = null;
    private UsuarioProperties usuario;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Incidentes getIncidente() {
        return incidente;
    }

    public void setIncidente(Incidentes incidente) {
        this.incidente = incidente;
    }

    public UsuarioProperties getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioProperties usuario) {
        this.usuario = usuario;
    }
}
