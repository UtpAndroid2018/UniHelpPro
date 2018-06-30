package pe.edu.utp.unihelppro.models;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import java.io.Serializable;

public class PreguntasFrecuentes extends SugarRecord implements Serializable {

    @Unique
    private String objectId;

    private String titulo = "";
    private String descripcion = "";

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
