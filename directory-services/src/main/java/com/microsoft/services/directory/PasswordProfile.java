/*******************************************************************************
**NOTE** This code was generated by a tool and will occasionally be
overwritten. We welcome comments and issues regarding this code; they will be
addressed in the generation tool. If you wish to submit pull requests, please
do so for the templates in that tool.

This code was generated by Vipr (https://github.com/microsoft/vipr) using
the T4TemplateWriter (https://github.com/msopentech/vipr-t4templatewriter).

Copyright (c) Microsoft Open Technologies, Inc. All Rights Reserved.
Licensed under the Apache License 2.0; see LICENSE in the source repository
root for authoritative license information.﻿
******************************************************************************/
package com.microsoft.services.directory;

import com.microsoft.services.orc.core.ODataBaseEntity;


/**
 * The type Password Profile.
*/
public class PasswordProfile extends ODataBaseEntity {

    public PasswordProfile(){
        setODataType("#Microsoft.DirectoryServices.PasswordProfile");
    }

    private String password;

    /**
    * Gets the password.
    *
    * @return the String
    */
    public String getPassword() {
        return this.password; 
    }

    /**
    * Sets the password.
    *
    * @param value the String
    */
    public void setPassword(String value) { 
        this.password = value;
        valueChanged("password", value);

    }

    private Boolean forceChangePasswordNextLogin;

    /**
    * Gets the force Change Password Next Login.
    *
    * @return the Boolean
    */
    public Boolean getForceChangePasswordNextLogin() {
        return this.forceChangePasswordNextLogin; 
    }

    /**
    * Sets the force Change Password Next Login.
    *
    * @param value the Boolean
    */
    public void setForceChangePasswordNextLogin(Boolean value) { 
        this.forceChangePasswordNextLogin = value;
        valueChanged("forceChangePasswordNextLogin", value);

    }
}
