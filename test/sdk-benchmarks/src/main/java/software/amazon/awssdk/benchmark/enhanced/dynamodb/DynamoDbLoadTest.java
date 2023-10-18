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

package software.amazon.awssdk.benchmark.enhanced.dynamodb;

import java.util.HashMap;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.benchmark.utils.MockHttpClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamoDbLoadTest {
    public static void main(String... args) {
        DynamoDbClient dynamoDb = DynamoDbClient.builder()
                                                .httpClient(new MockHttpClient("{}", "{}"))
                                                .addPlugin(x -> {})
                                                .addPlugin(x -> {})
                                                .addPlugin(x -> {})
                                                .build();

        AwsRequestOverrideConfiguration requestConfig =
            AwsRequestOverrideConfiguration.builder()
                                           .addPlugin(x -> {})
                                           .addPlugin(x -> {})
                                           .addPlugin(x -> {})
                                           .build();

        for (int i = 0; i < 100000; i++) {
            dynamoDb.putItem(r -> r.tableName("foo")
                                   .overrideConfiguration(requestConfig)
                                   .item(new HashMap<>()));
            dynamoDb.getItem(r -> r.tableName("foo")
                                   .overrideConfiguration(requestConfig)
                                   .key(new HashMap<>()));
        }
    }
}
