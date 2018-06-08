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
package com.microsoft.services.directory.fetchers;

import com.microsoft.services.directory.*;
import com.google.common.util.concurrent.*;
import com.microsoft.services.orc.core.*;
import com.microsoft.services.orc.core.Readable;

/**
 * The type  User
 .
 */
public class UserFetcher extends OrcEntityFetcher<User,UserOperations> 
                                     implements Readable<User> {

     /**
     * Instantiates a new UserFetcher.
     *
     * @param urlComponent the url component
     * @param parent the parent
     */
     public UserFetcher(String urlComponent, OrcExecutable parent) {
        super(urlComponent, parent, User.class, UserOperations.class);
    }

     /**
     * Add parameter.
     *
     * @param name the name
     * @param value the value
     * @return the fetcher
     */
    public UserFetcher addParameter(String name, Object value) {
        addCustomParameter(name, value);
        return this;
    }

     /**
     * Add header.
     *
     * @param name the name
     * @param value the value
     * @return the fetcher
     */
    public UserFetcher addHeader(String name, String value) {
        addCustomHeader(name, value);
        return this;
    }

        
     /**
     * Gets app role assignments.
     *
     * @return the app role assignments
     */
    public OrcCollectionFetcher<AppRoleAssignment, AppRoleAssignmentFetcher, AppRoleAssignmentCollectionOperations> getAppRoleAssignments() {
        return new OrcCollectionFetcher<AppRoleAssignment, AppRoleAssignmentFetcher, AppRoleAssignmentCollectionOperations>("appRoleAssignments", this, AppRoleAssignment.class, AppRoleAssignmentCollectionOperations.class);
    }

    /**
     * Gets app role assignment.
     *
     * @return the app role assignment
     */
    public AppRoleAssignmentFetcher getAppRoleAssignment(String id){
         return new OrcCollectionFetcher<AppRoleAssignment, AppRoleAssignmentFetcher, AppRoleAssignmentCollectionOperations>("appRoleAssignments", this, AppRoleAssignment.class, AppRoleAssignmentCollectionOperations.class).getById(id);
    }

     /**
     * Gets oauth2permission grants.
     *
     * @return the oauth2permission grants
     */
    public OrcCollectionFetcher<OAuth2PermissionGrant, OAuth2PermissionGrantFetcher, OAuth2PermissionGrantCollectionOperations> getOauth2PermissionGrants() {
        return new OrcCollectionFetcher<OAuth2PermissionGrant, OAuth2PermissionGrantFetcher, OAuth2PermissionGrantCollectionOperations>("oauth2PermissionGrants", this, OAuth2PermissionGrant.class, OAuth2PermissionGrantCollectionOperations.class);
    }

    /**
     * Gets oauth2permission grant.
     *
     * @return the oauth2permission grant
     */
    public OAuth2PermissionGrantFetcher getOauth2PermissionGrant(String id){
         return new OrcCollectionFetcher<OAuth2PermissionGrant, OAuth2PermissionGrantFetcher, OAuth2PermissionGrantCollectionOperations>("oauth2PermissionGrants", this, OAuth2PermissionGrant.class, OAuth2PermissionGrantCollectionOperations.class).getById(id);
    }

     /**
     * Gets owned devices.
     *
     * @return the owned devices
     */
    public OrcCollectionFetcher<DirectoryObject, DirectoryObjectFetcher, DirectoryObjectCollectionOperations> getOwnedDevices() {
        return new OrcCollectionFetcher<DirectoryObject, DirectoryObjectFetcher, DirectoryObjectCollectionOperations>("ownedDevices", this, DirectoryObject.class, DirectoryObjectCollectionOperations.class);
    }

    /**
     * Gets owned device.
     *
     * @return the owned device
     */
    public DirectoryObjectFetcher getOwnedDevice(String id){
         return new OrcCollectionFetcher<DirectoryObject, DirectoryObjectFetcher, DirectoryObjectCollectionOperations>("ownedDevices", this, DirectoryObject.class, DirectoryObjectCollectionOperations.class).getById(id);
    }

     /**
     * Gets registered devices.
     *
     * @return the registered devices
     */
    public OrcCollectionFetcher<DirectoryObject, DirectoryObjectFetcher, DirectoryObjectCollectionOperations> getRegisteredDevices() {
        return new OrcCollectionFetcher<DirectoryObject, DirectoryObjectFetcher, DirectoryObjectCollectionOperations>("registeredDevices", this, DirectoryObject.class, DirectoryObjectCollectionOperations.class);
    }

    /**
     * Gets registered device.
     *
     * @return the registered device
     */
    public DirectoryObjectFetcher getRegisteredDevice(String id){
         return new OrcCollectionFetcher<DirectoryObject, DirectoryObjectFetcher, DirectoryObjectCollectionOperations>("registeredDevices", this, DirectoryObject.class, DirectoryObjectCollectionOperations.class).getById(id);
    }

}
