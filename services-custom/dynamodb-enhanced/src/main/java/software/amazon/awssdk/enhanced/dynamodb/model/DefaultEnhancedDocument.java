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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverterProvider;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.ChainConverterProvider;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@SdkInternalApi
public class DefaultEnhancedDocument implements EnhancedDocument {


    private final ChainConverterProvider chainConverterProvider;
    private Map<String, AttributeValue> attributeMap;


    public DefaultEnhancedDocument(DefaultEnhancedDocumentBuilder builder) {

        List<AttributeConverterProvider> converterProviders = new ArrayList<>();
        if (builder.attributeConverterProviders != null) {
            converterProviders.addAll(builder.attributeConverterProviders);
        }
        converterProviders.add(AttributeConverterProvider.defaultProvider());
        chainConverterProvider = ChainConverterProvider.create(converterProviders);
    }

    public DefaultEnhancedDocument(Map<String, AttributeValue> attributeMap) {
        this.attributeMap = attributeMap;
        this.chainConverterProvider = ChainConverterProvider.create(AttributeConverterProvider.defaultProvider());
    }

    public static DefaultEnhancedDocumentBuilder builder() {
        return new DefaultEnhancedDocumentBuilder();

    }

    @Override
    public Builder toBuilder() {
        return new DefaultEnhancedDocumentBuilder();
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        return chainConverterProvider.converterFor(EnhancedType.of(type)).transformTo(attributeMap.get(key));
    }

    @Override
    public <T> T get(String key, AttributeConverter<T> attributeConverter) {
        return attributeConverter.transformTo(attributeMap.get(key));
    }

    @Override
    public Map<String, AttributeValue> toAttributeMap() {
        return attributeMap;
    }

    @Override
    public String toJson() {
        return attributeMap.toString();
    }

    public static class DefaultEnhancedDocumentBuilder implements EnhancedDocument.Builder {
        List<AttributeConverterProvider> attributeConverterProviders = new ArrayList<>();
        Map<String, AttributeValue> attributeValueMap = new LinkedHashMap<>();


        @Override
        public DefaultEnhancedDocumentBuilder addString(String key, String value) {
            attributeValueMap.put(key, AttributeValue.builder().s(value).build());
            return this;
        }

        @Override
        public DefaultEnhancedDocumentBuilder addNumber(String key, Number value) {
            attributeValueMap.put(key, AttributeValue.builder().n(value.toString()).build());
            return this;
        }

        @Override
        public DefaultEnhancedDocumentBuilder addBoolean(String key, boolean value) {
            attributeValueMap.put(key, AttributeValue.builder().bool(value).build());
            return this;
        }

        @Override
        public DefaultEnhancedDocumentBuilder addStringSet(String key, Set<String> values) {
            attributeValueMap.put(key,
                                  AttributeValue.fromL(
                                      values.stream().map(value -> AttributeValue.fromS(value)).collect(Collectors.toList())));
            return this;
        }

        @Override
        public DefaultEnhancedDocumentBuilder addAttributeConverterProvider(AttributeConverterProvider attributeConverterProvider) {
            attributeConverterProviders.add(attributeConverterProvider);
            return this;
        }

        @Override
        public EnhancedDocument build() {
            return new DefaultEnhancedDocument(this);
        }

        @Override
        public Builder addJson(String json) {
            JsonNode jsonNode = JsonNode.parser().parse(json);
            // Convert jsonNode to attributeValueMap
            return this;
        }
    }


}
