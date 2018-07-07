package pe.edu.utp.unihelppro.models;

import com.backendless.BackendlessUser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
public class UsuarioBackendless extends SugarRecord implements Serializable {
    @Ignore
    private UBackendless uBackendless;

    private String objectId = "";
    private String email = "";
    private String name = "";
    private String socialAccount = "";
    private String userStatus = "";
    private String tipo = "";
    private String codigo = "";

    public UsuarioBackendless() {
    }

    public UsuarioBackendless(String objectId) {
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

    public void setupUser( BackendlessUser registeredUser ) {
        //uBackendless = new UBackendless(getObjectId());
        //uBackendless.setObjectId( getObjectId() );
        //uBackendless.setEmail( getEmail() );
        //uBackendless.setName( getProperty("name").toString() );
        //uBackendless.setSocialAccount( getProperty("socialAccount").toString() );
        //uBackendless.setUserStatus( getProperty("userStatus").toString() );
        //uBackendless.setTipo( getProperty("tipo").toString() );
        //uBackendless.setCodigo( getProperty("codigo").toString() );
        if( getEmail().equals("") || getName().equals("") || getCodigo().equals("") || getTipo().equals("") || getUserStatus().equals("") ) {
            if( registeredUser != null ) {
                if( !registeredUser.getProperty("name").toString().equals("") ) {
                    setName( registeredUser.getProperty("name").toString() );
                }
                if( !registeredUser.getProperty("codigo").toString().equals("") ) {
                    setCodigo( registeredUser.getProperty("codigo").toString() );
                }
                if( !registeredUser.getProperty("email").toString().equals("") ) {
                    setEmail( registeredUser.getProperty("email").toString() );
                }
                if( !registeredUser.getProperty("tipo").toString().equals("") ) {
                    setTipo( registeredUser.getProperty("tipo").toString() );
                }
                if( !registeredUser.getProperty("userStatus").toString().equals("") ) {
                    setUserStatus( registeredUser.getProperty("userStatus").toString() );
                }
            } else {
                UsuarioBackendless _real = Select.from(UsuarioBackendless.class).where(Condition.prop("OBJECT_ID").eq(getObjectId())).first();
                if( _real != null ) {
                    setId( _real.getId() );
                    if( getName().equals("") ) {
                        setName( _real.getName() );
                    }
                    if( getCodigo().equals("") ) {
                        setCodigo( _real.getCodigo() );
                    }
                    if( getEmail().equals("") ) {
                        setEmail( _real.getEmail() );
                    }
                    if( getTipo().equals("") ) {
                        setTipo( _real.getTipo() );
                    }
                    if( getUserStatus().equals("") ) {
                        setUserStatus( _real.getUserStatus() );
                    }
                }
            }
        }

        /*
        List<UsuarioBackendless> _reals = UsuarioBackendless.listAll(UsuarioBackendless.class);
        if( _reals.size() > 0 ) {
        }
        */
        this.save();
    }

    public UBackendless getuBackendless() {
        return uBackendless;
    }

    public void setuBackendless(UBackendless uBackendless) {
        this.uBackendless = uBackendless;
    }
}
