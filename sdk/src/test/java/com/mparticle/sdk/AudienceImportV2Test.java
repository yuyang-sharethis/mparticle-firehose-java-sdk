package com.mparticle.sdk;

import com.mparticle.sdk.model.MessageSerializer;
import com.mparticle.sdk.model.audienceprocessing.Audience;
import com.mparticle.sdk.model.audienceprocessing.AudienceMembershipChangeRequest;
import com.mparticle.sdk.model.audienceprocessing.UserAttributeAudienceEvent;
import com.mparticle.sdk.model.audienceprocessing.UserProfile;
import com.mparticle.sdk.model.eventprocessing.PartnerIdentity;
import com.mparticle.sdk.model.eventprocessing.UserIdentity;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class AudienceImportV2Test {

    private String partnerIdValue = "partnerIdValue";
    private String partnerIdType = "somePartnerId";

    //region Test JSON
    private String audienceJson = "{\n" +
            "  \"type\": \"audience_membership_change_request\",\n" +
            "  \"firehose_version\":\"9.9.0\",\n" +
            "  \"id\": \"8e88afe4-e5fc-42c8-aa61-cb2a170293cd\",\n" +
            "  \"timestamp_ms\": 1454693235751,\n" +
            "  \"account\": {\n" +
            "    \"account_id\": 123456,\n" +
            "    \"account_settings\": {\n" +
            "      \"Example String Setting\": \"Example Setting Value\",\n" +
            "      \"Example Boolean Setting\": \"false\",\n" +
            "      \"Example Integer Setting\": \"123\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"user_profiles\": [\n" +
            "    {\n" +
            "      \"user_identities\": [\n" +
            "        {\n" +
            "          \"type\": \"email\",\n" +
            "          \"encoding\": \"md5\",\n" +
            "          \"value\": \"e179e95c00e7718ab4a23840f992ea63\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"partner_identities\":[\n" +
            "         {\n" +
            "          \"type\":\""+partnerIdType+"\",\n" +
            "          \"encoding\": \"raw\",\n" +
            "          \"value\": \"" + partnerIdValue + "\"\n" +
            "         }\n" +
            "      ],\n" +
            "      \"device_identities\": [\n" +
            "        {\n" +
            "          \"type\": \"google_advertising_id\",\n" +
            "          \"encoding\": \"raw\",\n" +
            "          \"value\": \"31a22ef0-f119-48d4-b009-a217a26a862a\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\": \"android_id\",\n" +
            "          \"encoding\": \"raw\",\n" +
            "          \"value\": \"a0504a8cfa15ce2c\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"audiences\": [\n" +
            "        {\n" +
            "          \"audience_id\": 456,\n" +
            "          \"action\": \"add\",\n" +
            "          \"audience_name\": \"Add Audience Name\",\n" +
            "          \"audience_subscription_settings\": {\n" +
            "            \"Example Audience-specific setting\": \"Example value\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"audience_id\": 555,\n" +
            "          \"action\": \"add\",\n" +
            "          \"audience_name\": \"Add Audience With Attrs\",\n" +
            "          \"audience_subscription_settings\": {\n" +
            "            \"Example Audience-specific setting\": \"Example value 5\"\n" +
            "          },\n" +
            "          \"user_attributes\": [\n" +
            "            {\n" +
            "              \"key\": \"Score\",\n" +
            "              \"value\": \"100\",\n" +
            "              \"action\": \"upsert\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"audience_id\": 789,\n" +
            "          \"action\": \"delete\",\n" +
            "          \"audience_name\": \"Remove Audience Name 2\",\n" +
            "          \"audience_subscription_settings\": {\n" +
            "            \"Example Audience-specific setting\": \"Example value 2\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"audience_id\": 432,\n" +
            "          \"action\": \"attribute_update\",\n" +
            "          \"audience_name\": \"Update Audience Name 3\",\n" +
            "          \"audience_subscription_settings\": {\n" +
            "            \"Example Audience-specific setting\": \"Example value 3\"\n" +
            "          },\n" +
            "          \"user_attributes\": [\n" +
            "            {\n" +
            "              \"key\": \"Highest level\",\n" +
            "              \"value\": \"10\",\n" +
            "              \"action\": \"upsert\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"key\": \"ElementsCompleted\",\n" +
            "              \"list_value\": [ \"grass\", \"fire\", \"water\" ],\n" +
            "              \"action\": \"upsert\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"key\": \"Is Commuter\",\n" +
            "              \"value\": null,\n" +
            "              \"action\": \"upsert\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"key\": \"Churn risk\",\n" +
            "              \"action\": \"delete\"\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    //endregion

    @Test
    public void TestAudienceV2() {
        MessageSerializer ser = new MessageSerializer();
        try {
            AudienceMembershipChangeRequest req = ser.deserialize(audienceJson, AudienceMembershipChangeRequest.class);
            assertNotNull(req);

            assertEquals("9.9.0", req.getFirehoseVersion());

            assertEquals(1, req.getUserProfiles().size());
            for (UserProfile profile : req.getUserProfiles()) {
                // Extract out the identities
                UserIdentity userIdentity = profile.getUserIdentities().get(0);
                List<PartnerIdentity> partnerIdentities = profile.getPartnerIdentities();

                assertEquals(UserIdentity.Type.EMAIL, userIdentity.getType());
                assertEquals(1, partnerIdentities.size());
                PartnerIdentity partnerIdentity = partnerIdentities.iterator().next();
                assertEquals(partnerIdType, partnerIdentity.getType());
                assertEquals(partnerIdValue, partnerIdentity.getValue());

                boolean aud1 = false, aud2 = false, aud3 = false, aud4 = false;
                // Now check the audiences.
                for (Audience aud : profile.getAudiences()) {

                    // Check the ADD audience without attrs
                    if (aud.getAudienceId() == 456) {
                        aud1 = true;
                        assertEquals(Audience.AudienceAction.ADD, aud.getAudienceAction());
                        assertEquals("Add Audience Name", aud.getAudienceName());
                        assertNull(aud.getUserAttributes());
                    }

                    // Check the ADD audience with attrs
                    if (aud.getAudienceId() == 555) {
                        aud2 = true;
                        assertEquals(Audience.AudienceAction.ADD, aud.getAudienceAction());
                        assertEquals("Add Audience With Attrs", aud.getAudienceName());
                        assertNotNull(aud.getUserAttributes());
                        assertEquals(1, aud.getUserAttributes().size());

                        UserAttributeAudienceEvent attr = aud.getUserAttributes().get(0);
                        assertNotNull(attr);
                        assertEquals(UserAttributeAudienceEvent.AttributeAction.UPSERT, attr.getAction());
                        assertEquals("Score", attr.getKey());
                        assertEquals("100", attr.getValue());
                        assertNull(attr.getListValue());
                    }

                    // Check the DELETE audience
                    if (aud.getAudienceId() == 789) {
                        aud3 = true;
                        assertEquals(Audience.AudienceAction.DELETE, aud.getAudienceAction());
                        assertEquals("Remove Audience Name 2", aud.getAudienceName());
                        assertNull(aud.getUserAttributes());
                    }

                    // Check the ATTRIBUTE_UPDATE audience
                    if (aud.getAudienceId() == 432) {
                        aud4 = true;
                        assertEquals(Audience.AudienceAction.ATTRIBUTE_UPDATE, aud.getAudienceAction());
                        assertEquals("Update Audience Name 3", aud.getAudienceName());
                        assertNotNull(aud.getUserAttributes());
                        assertEquals(4, aud.getUserAttributes().size());

                        // Check the list one
                        UserAttributeAudienceEvent attr = aud.getUserAttributes().get(1);
                        assertNotNull(attr);
                        assertEquals(UserAttributeAudienceEvent.AttributeAction.UPSERT, attr.getAction());
                        assertEquals("ElementsCompleted", attr.getKey());
                        assertNull(attr.getValue());
                        assertNotNull(attr.getListValue());
                        assertEquals(3, attr.getListValue().size());
                        assertEquals("fire", attr.getListValue().get(1));

                        // Check the tag one
                        UserAttributeAudienceEvent tag = aud.getUserAttributes().get(2);
                        assertNotNull(tag);
                        assertEquals(UserAttributeAudienceEvent.AttributeAction.UPSERT, tag.getAction());
                        assertEquals("Is Commuter", tag.getKey());
                        assertNull(tag.getValue());
                        assertNull(tag.getListValue());

                        // Check the deletion one
                        UserAttributeAudienceEvent del = aud.getUserAttributes().get(3);
                        assertNotNull(del);
                        assertEquals(UserAttributeAudienceEvent.AttributeAction.DELETE, del.getAction());
                        assertEquals("Churn risk", del.getKey());
                        assertNull(del.getValue());
                        assertNull(del.getListValue());
                    }
                }

                assertTrue(aud1 && aud2 && aud3 && aud4);
            }


        }
        catch (IOException e) {
            fail(e.getMessage());
        }
    }

}
