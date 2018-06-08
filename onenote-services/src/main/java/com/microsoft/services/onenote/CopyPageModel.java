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
package com.microsoft.services.onenote;

import com.microsoft.services.orc.core.ODataBaseEntity;


/**
 * The type Copy Page Model.
*/
public class CopyPageModel extends ODataBaseEntity {

    public CopyPageModel(){
        setODataType("#Microsoft.OneNote.Api.CopyPageModel");
    }

    private CopySectionModel parentSection;

    /**
    * Gets the parent Section.
    *
    * @return the CopySectionModel
    */
    public CopySectionModel getParentSection() {
        return this.parentSection; 
    }

    /**
    * Sets the parent Section.
    *
    * @param value the CopySectionModel
    */
    public void setParentSection(CopySectionModel value) { 
        this.parentSection = value;
        valueChanged("parentSection", value);

    }

    private CopyNotebookModel parentNotebook;

    /**
    * Gets the parent Notebook.
    *
    * @return the CopyNotebookModel
    */
    public CopyNotebookModel getParentNotebook() {
        return this.parentNotebook; 
    }

    /**
    * Sets the parent Notebook.
    *
    * @param value the CopyNotebookModel
    */
    public void setParentNotebook(CopyNotebookModel value) { 
        this.parentNotebook = value;
        valueChanged("parentNotebook", value);

    }

    private String title;

    /**
    * Gets the title.
    *
    * @return the String
    */
    public String getTitle() {
        return this.title; 
    }

    /**
    * Sets the title.
    *
    * @param value the String
    */
    public void setTitle(String value) { 
        this.title = value;
        valueChanged("title", value);

    }

    private String createdByAppId;

    /**
    * Gets the created By App Id.
    *
    * @return the String
    */
    public String getCreatedByAppId() {
        return this.createdByAppId; 
    }

    /**
    * Sets the created By App Id.
    *
    * @param value the String
    */
    public void setCreatedByAppId(String value) { 
        this.createdByAppId = value;
        valueChanged("createdByAppId", value);

    }

    private PageLinks links;

    /**
    * Gets the links.
    *
    * @return the PageLinks
    */
    public PageLinks getLinks() {
        return this.links; 
    }

    /**
    * Sets the links.
    *
    * @param value the PageLinks
    */
    public void setLinks(PageLinks value) { 
        this.links = value;
        valueChanged("links", value);

    }

    private String contentUrl;

    /**
    * Gets the content Url.
    *
    * @return the String
    */
    public String getContentUrl() {
        return this.contentUrl; 
    }

    /**
    * Sets the content Url.
    *
    * @param value the String
    */
    public void setContentUrl(String value) { 
        this.contentUrl = value;
        valueChanged("contentUrl", value);

    }

    private java.util.Calendar lastModifiedTime;

    /**
    * Gets the last Modified Time.
    *
    * @return the java.util.Calendar
    */
    public java.util.Calendar getLastModifiedTime() {
        return this.lastModifiedTime; 
    }

    /**
    * Sets the last Modified Time.
    *
    * @param value the java.util.Calendar
    */
    public void setLastModifiedTime(java.util.Calendar value) { 
        this.lastModifiedTime = value;
        valueChanged("lastModifiedTime", value);

    }

    private String id;

    /**
    * Gets the id.
    *
    * @return the String
    */
    public String getId() {
        return this.id; 
    }

    /**
    * Sets the id.
    *
    * @param value the String
    */
    public void setId(String value) { 
        this.id = value;
        valueChanged("id", value);

    }

    private String self;

    /**
    * Gets the self.
    *
    * @return the String
    */
    public String getSelf() {
        return this.self; 
    }

    /**
    * Sets the self.
    *
    * @param value the String
    */
    public void setSelf(String value) { 
        this.self = value;
        valueChanged("self", value);

    }

    private java.util.Calendar createdTime;

    /**
    * Gets the created Time.
    *
    * @return the java.util.Calendar
    */
    public java.util.Calendar getCreatedTime() {
        return this.createdTime; 
    }

    /**
    * Sets the created Time.
    *
    * @param value the java.util.Calendar
    */
    public void setCreatedTime(java.util.Calendar value) { 
        this.createdTime = value;
        valueChanged("createdTime", value);

    }
}
