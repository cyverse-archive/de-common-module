package org.iplantc.de.client.models.search;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

public class SearchModelUtils {

    public static DiskResourceQueryTemplate createDefaultFilter() {
        SearchAutoBeanFactory factory = GWT.create(SearchAutoBeanFactory.class);
        Splittable defFilter = StringQuoter.createSplittable();
        // Need to create full permissions by default in order to function as a "smart folder"
        Splittable permissions = StringQuoter.createSplittable();
        StringQuoter.create(true).assign(permissions, "own");
        StringQuoter.create(true).assign(permissions, "read");
        StringQuoter.create(true).assign(permissions, "write");
        permissions.assign(defFilter, "permissions");
        StringQuoter.create("/savedFilters/").assign(defFilter, "path");

        DiskResourceQueryTemplate dataSearchFilter = AutoBeanCodex.decode(factory, DiskResourceQueryTemplate.class, defFilter).as();
        dataSearchFilter.setCreatedWithin(factory.dateInterval().as());
        dataSearchFilter.setModifiedWithin(factory.dateInterval().as());
        dataSearchFilter.setFileSizeRange(factory.fileSizeRange().as());

        return dataSearchFilter;
    }
}
