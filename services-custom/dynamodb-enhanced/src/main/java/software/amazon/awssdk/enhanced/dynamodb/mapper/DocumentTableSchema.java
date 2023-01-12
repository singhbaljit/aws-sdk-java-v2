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

package software.amazon.awssdk.enhanced.dynamodb.mapper;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.TableMetadata;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedDocument;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.utils.Validate;

/**
 * Implementation of {@link TableSchema} that builds a schema that is used to while using enhanced ddb client with Json input and
 * outputs {@link TableSchema}.
 * <p>
 */
@SdkInternalApi
public final class DocumentTableSchema implements TableSchema<EnhancedDocument> {

    private final TableMetadata tableMetadata;


    public DocumentTableSchema(Builder builder) {
        Validate.paramNotNull(builder.tableMetadata, "tableMetadata");
        this.tableMetadata = builder.tableMetadata;

    }


    public static Builder builder() {
        return new Builder();
    }

    @Override
    public TableMetadata tableMetadata() {
        return tableMetadata;
    }

    @Override
    public EnhancedType<EnhancedDocument> itemType() {
        return EnhancedType.of(EnhancedDocument.class);
    }

    @Override
    public List<String> attributeNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    /**
     * The attributeMap is passed as it is to the EnhancedDocument. The conversion are made only during the get of the key
     * Attribute*
     */
    @Override
    public EnhancedDocument mapToItem(Map<String, AttributeValue> attributeMap) {
        if (attributeMap == null) {
            return null;
        }
        return EnhancedDocument.fromAttributeValueMap(attributeMap);
    }

    /**
     * The EnhancedDocument internally stores the attributes as Map during the time of instantiation. This map is directly used
     * while calling the low level DDB APIs*
     */
    @Override
    public Map<String, AttributeValue> itemToMap(EnhancedDocument item, boolean ignoreNulls) {
        if (item == null) {
            return Collections.emptyMap();
        }
        return item.toAttributeMap();
    }

    @Override
    public Map<String, AttributeValue> itemToMap(EnhancedDocument item, Collection<String> attributes) {

        Map<String, AttributeValue> attributeValueMap = item.toAttributeMap();
        Map<String, AttributeValue> newMap = new LinkedHashMap<>();
        attributes.forEach(key -> Optional.ofNullable(attributeValueMap.get(key))
                                          .ifPresent(value -> newMap.put(key, value)));
        return newMap;
    }

    @Override
    public AttributeValue attributeValue(EnhancedDocument item, String attributeName) {
        if (item != null) {
            return item.toAttributeMap().get(attributeName);
        }
        return null;
    }

    public static class Builder {
        private TableMetadata tableMetadata;

        private String indexName;
        private String indexAttributeName;
        private AttributeValueType indexAttributeValueType;
        private String sortKeyName;
        private String sortKeyAttributeName;
        private AttributeValueType sortKeyAttributeValueType;


        public Builder addIndexSortKey(String indexName, String attributeName, AttributeValueType attributeValueType) {
            this.indexName = indexName;
            this.indexAttributeName = attributeName;
            this.indexAttributeValueType = attributeValueType;
            return this;

        }

        public Builder addIndexPartitionKey(String indexName, String attributeName, AttributeValueType attributeValueType) {
            this.sortKeyName = indexName;
            this.sortKeyAttributeName = attributeName;
            this.sortKeyAttributeValueType = attributeValueType;
            return this;
        }

        public DocumentTableSchema build() {
            this.tableMetadata = StaticTableMetadata
                .builder()
                .addIndexSortKey(this.indexName, this.indexAttributeName, this.indexAttributeValueType)
                .addIndexSortKey(this.sortKeyName, this.sortKeyAttributeName, this.sortKeyAttributeValueType)
                .build();
            return new DocumentTableSchema(this);
        }
    }
}
