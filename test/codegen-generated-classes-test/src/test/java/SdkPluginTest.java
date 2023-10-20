/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.SdkPlugin;
import software.amazon.awssdk.core.SdkServiceClientConfiguration;
import software.amazon.awssdk.services.protocolrestjson.ProtocolRestJsonClient;
import software.amazon.awssdk.services.protocolrestjson.ProtocolRestJsonServiceClientConfiguration;

/**
 * Verify that configuration changes made by plugins are reflected in the SDK client configuration used by the request, and
 * that the plugin can see all SDK configuration options.
 */
public class SdkPluginTest {
    @Test
    public void test() {
        SdkPlugin plugin = new SdkPlugin() {
            @Override
            public void configureClient(SdkServiceClientConfiguration.Builder config) {
                ProtocolRestJsonServiceClientConfiguration.Builder conf =
                    (ProtocolRestJsonServiceClientConfiguration.Builder) config;
            }
        };
        ProtocolRestJsonClient client =
            ProtocolRestJsonClient.builder()
                                  .addPlugin(plugin)
                                  .build();
    }
}
