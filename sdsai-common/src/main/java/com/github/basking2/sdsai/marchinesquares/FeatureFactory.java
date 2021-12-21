/**
 * Copyright (c) 2020-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.marchinesquares;

import java.util.UUID;

/**
 * A class that builds new {@link Feature}s.
 *
 * This allows the user to define ways to populate {@link Feature#properties} at creation time instead of looking for the feature after they are created.
 */
@FunctionalInterface
public interface FeatureFactory {
    Feature buildFeature(final LinkedList.Node<Point> points);

    /**
     * A factory that calls new on {@link Feature}. No other changes.
     * @return a factory that calls new on {@link Feature}. No other changes.
     */
    static FeatureFactory defaultFactory() {
        return (points) -> new Feature(points);
    }

    /**
     * A factory that generates a {@link UUID#randomUUID()} and adds it as the property "id" in the new feature.
     *
     *  @return a factory that generates a {@link UUID#randomUUID()} and adds it as the property "id" in the new feature.
     */
    static FeatureFactory uuidProperty() {
        return (points) -> {
            final Feature f = new Feature(points);

            f.properties.put("id", UUID.randomUUID().toString());

            return f;
        };
    }
}
