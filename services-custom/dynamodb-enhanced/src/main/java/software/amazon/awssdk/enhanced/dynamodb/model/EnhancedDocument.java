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

package software.amazon.awssdk.enhanced.dynamodb.model;

import static java.util.Collections.unmodifiableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverterProvider;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@SdkPublicApi
public interface EnhancedDocument {


    static EnhancedDocument fromAttributeValueMap(Map<String, AttributeValue> attributeMap) {
        return new DefaultEnhancedDocument(attributeMap);
    }

    static EnhancedDocument fromJson(String json) {
        return DefaultEnhancedDocument.builder().addJson(json).build();

    }

    static EnhancedDocument.Builder builder(){
        return new DefaultEnhancedDocument.DefaultEnhancedDocumentBuilder();
    }

    public Builder toBuilder();

    <T> T get(String key, Class<T> type);
    <T> T get(String key, AttributeConverter<T> attributeConverter);
    String toJson();
    Map<String, AttributeValue> toAttributeMap();


    interface Builder {

        Builder addString(String key, String value);

        Builder addNumber(String key, Number value);

        Builder addBoolean(String key, boolean value);

        Builder addStringSet(String key, Set<String> values);

        Builder addAttributeConverterProvider(AttributeConverterProvider attributeConverterProvider);

        EnhancedDocument build();

        Builder addJson(String json);
    }
}
