package org.iplantc.de.client.services.converters;

import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.AppGroup;
import org.iplantc.de.client.models.apps.AppGroupList;
import org.iplantc.de.client.services.AsyncCallbackConverter;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import java.util.List;

public class AppGroupListCallbackConverter extends AsyncCallbackConverter<String, List<AppGroup>> {

    private final AppAutoBeanFactory factory = GWT.create(AppAutoBeanFactory.class);

    public AppGroupListCallbackConverter(AsyncCallback<List<AppGroup>> callback) {
        super(callback);
    }

    @Override
    protected List<AppGroup> convertFrom(String object) {
        AutoBean<AppGroupList> bean = AutoBeanCodex.decode(factory, AppGroupList.class, object);
        AppGroupList as = bean.as();
        List<AppGroup> groups = as.getGroups();
        return groups;
    }

    @Override
    public void onFailure(Throwable caught) {
        // FIXME JDS Need to move error handling down.
        // ErrorHandler.post(I18N.ERROR.analysisGroupsLoadFailure(), caught);
        super.onFailure(caught);
    }

}
