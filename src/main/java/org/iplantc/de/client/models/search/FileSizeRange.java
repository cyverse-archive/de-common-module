package org.iplantc.de.client.models.search;

public interface FileSizeRange {

    Double getMax();

    Double getMin();

    boolean isInclusive();

    void setInclusive(boolean inclusive);

    void setMax(Double max);

    void setMin(Double min);

}
