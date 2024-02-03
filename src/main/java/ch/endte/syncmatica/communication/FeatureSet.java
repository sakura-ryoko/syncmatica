package ch.endte.syncmatica.communication;

import ch.endte.syncmatica.Feature;

import java.util.*;

// a class representing what kind of features a syncmatica instance supports
// or has enabled/disabled

public class FeatureSet {

    private static final Map<String, FeatureSet> versionFeatures;
    private final Collection<Feature> features;

    public static FeatureSet fromVersionString(String version) {
        if (version.matches("^\\d+(\\.\\d+){2,4}$")) {
            final int minSize = version.indexOf(".");
            while (version.length() > minSize) {
                if (versionFeatures.containsKey(version)) {
                    return versionFeatures.get(version);
                }
                final int lastDot = version.lastIndexOf(".");
                version = version.substring(0, lastDot);
            }
        }
        return null;
    }

    public static FeatureSet fromString(final String features) {
        final FeatureSet featureSet = new FeatureSet(new ArrayList<>());
        for (final String feature : features.split("\n")) {
            final Feature f = Feature.fromString(feature);
            if (f != null) {
                featureSet.features.add(f);
            }
        }
        return featureSet;
    }

    @Override
    public String toString() {
        final StringBuilder output = new StringBuilder();
        boolean b = false;
        for (final Feature feature : features) {
            output.append(b ? "\n" + feature.toString() : feature.toString());
            b = true;
        }
        return output.toString();
    }

    public FeatureSet(final Collection<Feature> features) {
        this.features = features;
    }

    public boolean hasFeature(final Feature f) {
        return features.contains(f);
    }

    static {
        versionFeatures = new HashMap<>();
        versionFeatures.put("0.1", new FeatureSet(Collections.singletonList(Feature.CORE)));
    }

}
