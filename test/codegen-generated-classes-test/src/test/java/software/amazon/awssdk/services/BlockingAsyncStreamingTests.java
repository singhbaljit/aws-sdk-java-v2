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

package software.amazon.awssdk.services;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.put;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.protocolrestjson.ProtocolRestJsonAsyncClient;
import software.amazon.awssdk.services.protocolrestjson.model.StreamingInputOperationRequest;

public class BlockingAsyncStreamingTests {
    private ProtocolRestJsonAsyncClient client;
    private WireMockServer wireMock = new WireMockServer(0);

    @BeforeEach
    public void setup() {
        wireMock.start();
        client = ProtocolRestJsonAsyncClient.builder()
                                            .region(Region.US_WEST_2)
                                            .credentialsProvider(AnonymousCredentialsProvider.create())
                                            .endpointOverride(URI.create("http://localhost:" + wireMock.port()))
                                            .build();
        wireMock.stubFor(put(anyUrl()).willReturn(aResponse().withStatus(200).withBody("{}")));
    }

    @Test
    public void blockingWithExecutorTest() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            client.streamingInputOperation(StreamingInputOperationRequest.builder().build(),
                                           AsyncRequestBody.fromIn)
        } finally {
            executorService.shutdownNow();
        }
    }
}
