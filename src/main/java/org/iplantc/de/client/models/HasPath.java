package org.iplantc.de.client.models;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

public interface HasPath {

    @PropertyName("path")
    public String getPath();
    
}
