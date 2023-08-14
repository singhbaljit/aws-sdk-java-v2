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

package software.amazon.awssdk.services.endpointproviders;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.AwsRequest;
import software.amazon.awssdk.awscore.endpoints.AwsEndpointAttribute;
import software.amazon.awssdk.awscore.endpoints.authscheme.EndpointAuthScheme;
import software.amazon.awssdk.awscore.endpoints.authscheme.SigV4AuthScheme;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.client.config.SdkAdvancedClientOption;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.core.interceptor.InterceptorContext;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.endpoints.Endpoint;
import software.amazon.awssdk.endpoints.EndpointAttributeKey;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.restjsonendpointproviders.RestJsonEndpointProvidersAsyncClient;
import software.amazon.awssdk.services.restjsonendpointproviders.RestJsonEndpointProvidersAsyncClientBuilder;
import software.amazon.awssdk.services.restjsonendpointproviders.RestJsonEndpointProvidersClient;
import software.amazon.awssdk.services.restjsonendpointproviders.RestJsonEndpointProvidersClientBuilder;
import software.amazon.awssdk.services.restjsonendpointproviders.auth.scheme.RestJsonEndpointProvidersAuthSchemeProvider;
import software.amazon.awssdk.services.restjsonendpointproviders.endpoints.RestJsonEndpointProvidersEndpointProvider;
import software.amazon.awssdk.services.restjsonendpointproviders.endpoints.internal.RestJsonEndpointProvidersResolveEndpointInterceptor;

public class EndpointInterceptorTests {

    @Test
    public void sync_hostPrefixInjectDisabled_hostPrefixNotAdded() {
        CapturingInterceptor interceptor = new CapturingInterceptor();
        RestJsonEndpointProvidersClient client = syncClientBuilder()
            .overrideConfiguration(o -> o.addExecutionInterceptor(interceptor)
                                         .putAdvancedOption(SdkAdvancedClientOption.DISABLE_HOST_PREFIX_INJECTION, true))
            .build();

        assertThatThrownBy(() -> client.operationWithHostPrefix(r -> {}))
            .hasMessageContaining("stop");

        Endpoint endpoint = interceptor.executionAttributes().getAttribute(SdkInternalExecutionAttribute.RESOLVED_ENDPOINT);

        assertThat(endpoint.url().getHost()).isEqualTo("restjson.us-west-2.amazonaws.com");
    }

    @Test
    public void async_hostPrefixInjectDisabled_hostPrefixNotAdded() {
        CapturingInterceptor interceptor = new CapturingInterceptor();
        RestJsonEndpointProvidersAsyncClient client = asyncClientBuilder()
            .overrideConfiguration(o -> o.addExecutionInterceptor(interceptor)
                                         .putAdvancedOption(SdkAdvancedClientOption.DISABLE_HOST_PREFIX_INJECTION, true))
            .build();

        assertThatThrownBy(() -> client.operationWithHostPrefix(r -> {}).join())
            .hasMessageContaining("stop");

        Endpoint endpoint = interceptor.executionAttributes().getAttribute(SdkInternalExecutionAttribute.RESOLVED_ENDPOINT);

        assertThat(endpoint.url().getHost()).isEqualTo("restjson.us-west-2.amazonaws.com");
    }

    @Test
    public void sync_clientContextParamsSetOnBuilder_includedInExecutionAttributes() {
        CapturingInterceptor interceptor = new CapturingInterceptor();
        RestJsonEndpointProvidersClient client = syncClientBuilder()
            .overrideConfiguration(o -> o.addExecutionInterceptor(interceptor))
            .build();

        assertThatThrownBy(() -> client.operationWithNoInputOrOutput(r -> {
        })).hasMessageContaining("stop");

        Endpoint endpoint = interceptor.executionAttributes().getAttribute(SdkInternalExecutionAttribute.RESOLVED_ENDPOINT);
        assertThat(endpoint).isNotNull();
    }

    @Test
    public void async_clientContextParamsSetOnBuilder_includedInExecutionAttributes() {
        CapturingInterceptor interceptor = new CapturingInterceptor();
        RestJsonEndpointProvidersAsyncClient client = asyncClientBuilder()
            .overrideConfiguration(o -> o.addExecutionInterceptor(interceptor))
            .build();

        assertThatThrownBy(() -> client.operationWithNoInputOrOutput(r -> {
        }).join()).hasMessageContaining("stop");

        Endpoint endpoint = interceptor.executionAttributes().getAttribute(SdkInternalExecutionAttribute.RESOLVED_ENDPOINT);
        assertThat(endpoint).isNotNull();
    }

    @Test
    public void sync_endpointProviderReturnsHeaders_includedInHttpRequest() {
        RestJsonEndpointProvidersEndpointProvider defaultProvider = RestJsonEndpointProvidersEndpointProvider.defaultProvider();

        CapturingInterceptor interceptor = new CapturingInterceptor();
        RestJsonEndpointProvidersClient client = syncClientBuilder()
            .overrideConfiguration(o -> o.addExecutionInterceptor(interceptor))
            .endpointProvider(r -> defaultProvider.resolveEndpoint(r)
                                                  .thenApply(e -> e.toBuilder()
                                                                   .putHeader("TestHeader", "TestValue")
                                                                   .build()))
            .build();

        assertThatThrownBy(() -> client.operationWithHostPrefix(r -> {}))
            .hasMessageContaining("stop");

        assertThat(interceptor.context.httpRequest().matchingHeaders("TestHeader")).containsExactly("TestValue");
    }

    @Test
    public void async_endpointProviderReturnsHeaders_includedInHttpRequest() {
        RestJsonEndpointProvidersEndpointProvider defaultProvider = RestJsonEndpointProvidersEndpointProvider.defaultProvider();

        CapturingInterceptor interceptor = new CapturingInterceptor();
        RestJsonEndpointProvidersAsyncClient client = asyncClientBuilder()
            .overrideConfiguration(o -> o.addExecutionInterceptor(interceptor))
            .endpointProvider(r -> defaultProvider.resolveEndpoint(r)
                                                  .thenApply(e -> e.toBuilder()
                                                                   .putHeader("TestHeader", "TestValue")
                                                                   .build()))
            .build();

        assertThatThrownBy(() -> client.operationWithHostPrefix(r -> {}).join())
            .hasMessageContaining("stop");

        assertThat(interceptor.context.httpRequest().matchingHeaders("TestHeader")).containsExactly("TestValue");
    }

    @Test
    public void sync_endpointProviderReturnsHeaders_appendedToExistingRequest() {
        RestJsonEndpointProvidersEndpointProvider defaultProvider = RestJsonEndpointProvidersEndpointProvider.defaultProvider();

        CapturingInterceptor interceptor = new CapturingInterceptor();
        RestJsonEndpointProvidersClient client = syncClientBuilder()
            .overrideConfiguration(o -> o.addExecutionInterceptor(interceptor))
            .endpointProvider(r -> defaultProvider.resolveEndpoint(r)
                                                  .thenApply(e -> e.toBuilder()
                                                                   .putHeader("TestHeader", "TestValue")
                                                                   .build()))
            .build();

        assertThatThrownBy(() -> client.operationWithHostPrefix(r -> r.overrideConfiguration(c -> c.putHeader("TestHeader",
                                                                                                              "TestValue0"))))
            .hasMessageContaining("stop");

        assertThat(interceptor.context.httpRequest().matchingHeaders("TestHeader")).containsExactly("TestValue", "TestValue0");
    }

    @Test
    public void async_endpointProviderReturnsHeaders_appendedToExistingRequest() {
        RestJsonEndpointProvidersEndpointProvider defaultProvider = RestJsonEndpointProvidersEndpointProvider.defaultProvider();

        CapturingInterceptor interceptor = new CapturingInterceptor();
        RestJsonEndpointProvidersAsyncClient client = asyncClientBuilder()
            .overrideConfiguration(o -> o.addExecutionInterceptor(interceptor))
            .endpointProvider(r -> defaultProvider.resolveEndpoint(r)
                                                  .thenApply(e -> e.toBuilder()
                                                                   .putHeader("TestHeader", "TestValue")
                                                                   .build()))
            .build();

        assertThatThrownBy(() -> client.operationWithHostPrefix(r -> r.overrideConfiguration(c -> c.putHeader("TestHeader",
                                                                                                              "TestValue0")))
                                       .join())
            .hasMessageContaining("stop");

        assertThat(interceptor.context.httpRequest().matchingHeaders("TestHeader")).containsExactly("TestValue", "TestValue0");
    }

    @Test
    public void sync_endpointProviderReturnsSignerProperties_overridesAuthSchemeResolverProperties() {
        RestJsonEndpointProvidersEndpointProvider defaultEndpointProvider =
            RestJsonEndpointProvidersEndpointProvider.defaultProvider();

        List<EndpointAuthScheme> endpointAuthSchemes = new ArrayList<>();
        endpointAuthSchemes.add(SigV4AuthScheme.builder().signingRegion("region-from-endpoint-provider").build());

        CapturingInterceptor interceptor = new CapturingInterceptor();
        RestJsonEndpointProvidersClient client = syncClientBuilder()
            .overrideConfiguration(o -> o.addExecutionInterceptor(interceptor))
            .endpointProvider(r -> defaultEndpointProvider.resolveEndpoint(r)
                                                          .thenApply(e -> e.toBuilder()
                                                                           .putAttribute(AwsEndpointAttribute.AUTH_SCHEMES, endpointAuthSchemes)
                                                                           .build()))
            .build();

        assertThatThrownBy(() -> client.operationWithHostPrefix(r -> {}))
            .hasMessageContaining("stop");

        assertThat(interceptor.context.httpRequest().matchingHeaders("Authorization"))
            .singleElement()
            .asString()
            .contains("/region-from-endpoint-provider/restjson/aws4_request");
    }

    @Test
    public void async_endpointProviderReturnsSignerProperties_overridesAuthSchemeResolverProperties() {
        RestJsonEndpointProvidersEndpointProvider defaultEndpointProvider =
            RestJsonEndpointProvidersEndpointProvider.defaultProvider();

        List<EndpointAuthScheme> endpointAuthSchemes = new ArrayList<>();
        endpointAuthSchemes.add(SigV4AuthScheme.builder()
                                               .signingRegion("region-from-endpoint-provider")
                                               .build());

        CapturingInterceptor interceptor = new CapturingInterceptor();
        RestJsonEndpointProvidersAsyncClient client = asyncClientBuilder()
            .overrideConfiguration(o -> o.addExecutionInterceptor(interceptor))
            .endpointProvider(r -> defaultEndpointProvider.resolveEndpoint(r)
                                                          .thenApply(e -> e.toBuilder()
                                                                           .putAttribute(AwsEndpointAttribute.AUTH_SCHEMES, endpointAuthSchemes)
                                                                           .build()))
            .build();

        assertThatThrownBy(() -> client.operationWithHostPrefix(r -> {}).join())
            .hasMessageContaining("stop");

        assertThat(interceptor.context.httpRequest().matchingHeaders("Authorization"))
            .singleElement()
            .asString()
            .contains("/region-from-endpoint-provider/restjson/aws4_request");
    }

    public static class CapturingInterceptor implements ExecutionInterceptor {

        private Context.BeforeTransmission context;
        private ExecutionAttributes executionAttributes;

        @Override
        public void beforeTransmission(Context.BeforeTransmission context, ExecutionAttributes executionAttributes) {
            this.context = context;
            this.executionAttributes = executionAttributes;
            throw new CaptureCompletedException("stop");
        }

        public ExecutionAttributes executionAttributes() {
            return executionAttributes;
        }

        public class CaptureCompletedException extends RuntimeException {
            CaptureCompletedException(String message) {
                super(message);
            }
        }
    }

    private RestJsonEndpointProvidersClientBuilder syncClientBuilder() {
        return RestJsonEndpointProvidersClient.builder()
                                              .region(Region.US_WEST_2)
                                              .credentialsProvider(
                                                  StaticCredentialsProvider.create(
                                                      AwsBasicCredentials.create("akid", "skid")));
    }

    private RestJsonEndpointProvidersAsyncClientBuilder asyncClientBuilder() {
        return RestJsonEndpointProvidersAsyncClient.builder()
                                            .region(Region.US_WEST_2)
                                            .credentialsProvider(
                                                StaticCredentialsProvider.create(
                                                    AwsBasicCredentials.create("akid", "skid")));
    }
}
