package com.mparticle.sdk.generator;

import com.mparticle.sdk.model.Consts;
import com.mparticle.sdk.model.registration.ModuleRegistrationRequest;

import java.util.AbstractMap;
import java.util.Map;

public class ModuleRegistrationRequestSample {
    public static Map.Entry<String, ModuleRegistrationRequest> GenerateMessage() {
        ModuleRegistrationRequest req = new ModuleRegistrationRequest();

        req.setFirehoseVersion(Consts.SDK_VERSION);
        req.setTimestamp(1454693235751L);

        return new AbstractMap.SimpleImmutableEntry<>(req.getClass().getSimpleName(), req);
    }
}
