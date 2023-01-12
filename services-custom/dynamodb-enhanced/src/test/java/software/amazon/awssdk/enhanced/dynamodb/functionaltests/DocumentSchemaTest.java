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

package software.amazon.awssdk.enhanced.dynamodb.functionaltests;

import static software.amazon.awssdk.enhanced.dynamodb.TableMetadata.primaryIndexName;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.Test;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.DefaultAttributeConverterProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.ChainConverterProvider;
import software.amazon.awssdk.enhanced.dynamodb.mapper.DocumentTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.testbeans.AttributeConverterBean;
import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedDocument;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.streams.endpoints.internal.Value;

public class DocumentSchemaTest {

    @Test
    public void testCreationFromDocumentSchema(){

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().build();

        DocumentTableSchema documentTableSchema =
            TableSchema.fromDocumentSchemaBuilder()
                       .addIndexPartitionKey(primaryIndexName(), "sample_hash_name", AttributeValueType.S)
                       .addIndexSortKey(primaryIndexName(), "sample_sort_name", AttributeValueType.N)
                       .build();

        DynamoDbTable<EnhancedDocument> documentTable = enhancedClient.table("table-name", documentTableSchema);

        ;


        EnhancedDocument hashKeyDocument = EnhancedDocument.builder()
                                                           .addString("sample_hash_name", "sample_value")
                                                           .build();

        EnhancedDocument documentTableItem = documentTable.getItem(hashKeyDocument);

        EnhancedDocument changedValue = documentTableItem.toBuilder().addString("key-to-change", "changedValue").build();
        documentTable.putItem(changedValue);

        Number sample_sort_name = documentTableItem.get("sample_sort_name", Number.class);

        Set<String> setOfStrings = documentTableItem.get("set_attribute_name", Set.class);
//        CustomClass customClass = documentTableItem.get("custom_nested_map", new CustomAttributeConverter()));

        EnhancedDocument documentToPut = EnhancedDocument.fromJson(("{\"sample_hash_name\": \"sample_value_2\"}"));
        documentTable.putItem(documentToPut);

        EnhancedDocument itemToPutTwo = EnhancedDocument.builder()
                                                 .addString("sample_hash_name", "sample_value_2")
                                                 .addNumber("sample_sort_name", 111)
                                                 .build();
        documentTable.putItem(itemToPutTwo);



    }

    @Test
    public void testDefaultConvertors(){

        DefaultAttributeConverterProvider defaultAttributeConverterProvider = DefaultAttributeConverterProvider.create();


        AttributeConverter<Collection<String>> collectionAttributeConverter =
            defaultAttributeConverterProvider.converterFor(EnhancedType.collectionOf(String.class));


        List<AttributeValue> attributeValues = Arrays.asList(AttributeValue.fromS("hello"), AttributeValue.fromS("world"));


        Collection<String> strings =
            collectionAttributeConverter.transformTo(AttributeValue.builder().l(attributeValues).build());

        strings.stream().forEach(System.out::println);



    }

    @Test
    public void addGenericObjectTest() throws IntrospectionException {

        List<?> listofList = new ArrayList<>();


        AttributeValue attributeValue = getAttributeValue(listofList);

        EnhancedType<? extends List<? extends List>> listEnhancedType = EnhancedType.listOf(listofList.getClass());


    }

    private AttributeValue getAttributeValue(List<?> listofList) {

        /**
         * *
         * {"one", "two"}, {"three"}}; *
         * {{"key" : value}, }*
         */
        AttributeValue.fromL(Arrays.asList(AttributeValue.fromM()))

        {


    }
}
