package com.cxense.cxensesdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Group object for item in {@link UserProfile}
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
@SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
public final class UserProfileGroup {
    @JsonProperty("group")
    String group;
    @JsonProperty("count")
    int count;
    @JsonProperty("weight")
    double weight;

    /**
     * Gets the category or type of information.
     *
     * @return category
     */
    public String getGroup() {
        return group;
    }

    /**
     * Gets count
     *
     * @return count
     */
    public int getCount() {
        return count;
    }

    /**
     * Gets the relative prominence of the item/group combination.
     *
     * @return weight value
     */
    public double getWeight() {
        return weight;
    }
}