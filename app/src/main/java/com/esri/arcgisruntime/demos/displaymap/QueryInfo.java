package com.esri.arcgisruntime.demos.displaymap;

import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.geometry.Envelope;

public class QueryInfo {
    private String name;
    private Feature feature;
    private Envelope envelope;

    public QueryInfo(String name, Feature feature, Envelope envelope) {
        this.name = name;
        this.feature = feature;
        this.envelope = envelope;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }

    public Envelope getEnvelope() {
        return envelope;
    }

    public void setEnvelope(Envelope envelope) {
        this.envelope = envelope;
    }
}
