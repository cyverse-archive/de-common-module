package org.iplantc.de.client.models.apps;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

public interface DataSourceList {

    @PropertyName("data_sources")
    List<DataSource> getDataSources();
}
