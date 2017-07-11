package com.cxense.cxensesdk.model;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Request object for getting user from server
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
@SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
public final class UserDataRequest extends UserIdentity {
    @JsonProperty("groups")
    List<String> groups;
    @JsonProperty("recent")
    Boolean isRecent;
    @JsonProperty("identityTypes")
    List<String> identityTypes;

    public UserDataRequest(@NonNull UserIdentity identity, List<String> groups, Boolean recent, List<String> identityTypes) {
        super(identity);
        isRecent = recent;
        if (groups != null)
            this.groups = new ArrayList<>(groups);
        if (identityTypes != null)
            this.identityTypes = new ArrayList<>(identityTypes);
    }
}