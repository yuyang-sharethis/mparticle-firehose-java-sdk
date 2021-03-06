package com.mparticle.sdk.model.registration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mparticle.sdk.model.eventprocessing.Identity;
import com.mparticle.sdk.model.eventprocessing.UserIdentity;


public final class UserIdentityPermission {

    @JsonProperty(value = "type", required = true)
    private final UserIdentity.Type type;

    @JsonProperty(value = "encoding", required = true)
    private final Identity.Encoding encoding;

    @JsonProperty("required")
    private final boolean isRequired;

    /**
     * @return user identity type
     */
    public UserIdentity.Type getType() {
        return type;
    }

    /**
     * @return user identity encoding
     */
    public Identity.Encoding getEncoding() {
        return encoding;
    }

    /**
     * @return true if identity is required
     */
    public boolean isRequired() {
        return isRequired;
    }

    /**
     * @param type       identity type
     * @param encoding   identity encoding
     * @param isRequired if set to true, users missing this identity will be ignored
     */
    @JsonCreator
    public UserIdentityPermission(
            @JsonProperty(value = "type", required = true) UserIdentity.Type type,
            @JsonProperty(value = "encoding", required = true) Identity.Encoding encoding,
            @JsonProperty("required") boolean isRequired) {
        this.type = type;
        this.encoding = encoding;
        this.isRequired = isRequired;
    }

    /**
     * @param type     identity type
     * @param encoding identity encoding
     */
    public UserIdentityPermission(UserIdentity.Type type, Identity.Encoding encoding) {
        this(type, encoding, false);
    }
}