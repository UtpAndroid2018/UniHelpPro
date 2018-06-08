package pe.edu.utp.unihelppro.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown=true)
public class UBackendless extends SugarRecord implements Serializable {
    @Unique
    private String objectId;
    private String email;
    private String name;
    private String socialAccount;
    private String userStatus;
    private String tipo;
    private String codigo;

    public UBackendless(String objectId) {
        this.objectId = objectId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSocialAccount() {
        return socialAccount;
    }

    public void setSocialAccount(String socialAccount) {
        this.socialAccount = socialAccount;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
