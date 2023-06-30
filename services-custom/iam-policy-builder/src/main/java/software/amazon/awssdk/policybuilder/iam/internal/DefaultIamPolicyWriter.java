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

package software.amazon.awssdk.policybuilder.iam.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.policybuilder.iam.IamCondition;
import software.amazon.awssdk.policybuilder.iam.IamConditionKey;
import software.amazon.awssdk.policybuilder.iam.IamConditionOperator;
import software.amazon.awssdk.policybuilder.iam.IamPolicy;
import software.amazon.awssdk.policybuilder.iam.IamPolicyWriter;
import software.amazon.awssdk.policybuilder.iam.IamPrincipal;
import software.amazon.awssdk.policybuilder.iam.IamPrincipalType;
import software.amazon.awssdk.policybuilder.iam.IamStatement;
import software.amazon.awssdk.policybuilder.iam.IamValue;
import software.amazon.awssdk.utils.StringUtils;

public class DefaultIamPolicyWriter implements IamPolicyWriter {
    private static final int INDENTATION_TABLE_SIZE = 7;
    private static final String SINGLE_INDENTATION = "    ";
    private static final IamPolicyWriter INSTANCE = IamPolicyWriter.builder().build();

    private final String[] indentationTable = new String[INDENTATION_TABLE_SIZE];
    private final String afterColonSpacing;
    private final Boolean prettyPrint;

    public static IamPolicyWriter create() {
        return INSTANCE;
    }

    public DefaultIamPolicyWriter(Builder builder) {
        prettyPrint = builder.prettyPrint;

        // Initialize pretty-printing tables
        if (Boolean.TRUE.equals(builder.prettyPrint)) {
            StringBuilder indent = new StringBuilder();
            indent.append("\n");
            for (int i = 0; i < INDENTATION_TABLE_SIZE; i++) {
                indentationTable[i] = indent.toString();
                indent.append(SINGLE_INDENTATION);
            }
            afterColonSpacing = " ";
        } else {
            Arrays.fill(indentationTable, "");
            afterColonSpacing = "";
        }
    }

    @Override
    public String write(IamPolicy policy) {
        StringBuilder result = new StringBuilder();
        result.append("{");

        appendStringField(result, 1, "Version", policy.version());
        appendStringField(result, 1, "Id", policy.id());
        appendStatements(result, 1, policy.statements());
        appendAdditionalJson(result, 1, policy.additionalJsonFieldsUnsafe());

        trimComma(result);
        result.append(indentationTable[0]).append("}");
        return result.toString();
    }

    private void appendStatements(StringBuilder result, int indentation, List<IamStatement> statements) {
        if (statements.isEmpty()) {
            return;
        }

        if (statements.size() == 1) {
            appendObjectFieldStart(result, indentation, "Statement");
            appendStatementBody(result, indentation + 1, statements.get(0));
            trimComma(result);
            appendObjectFieldEnd(result, indentation);
            return;
        }

        appendArrayFieldStart(result, indentation, "Statement");
        statements.forEach(statement -> appendStatement(result, indentation + 1, statement));
        trimComma(result);
        appendArrayFieldEnd(result, indentation);
    }

    private void appendStatement(StringBuilder result, int indentation, IamStatement statement) {
        result.append(indentationTable[indentation]).append("{");
        appendStatementBody(result, indentation + 1, statement);
        trimComma(result);
        result.append(indentationTable[indentation]).append("},");
    }

    private void appendStatementBody(StringBuilder result, int indentation, IamStatement statement) {
        appendStringField(result, indentation, "Sid", statement.sid());
        appendStringField(result, indentation, "Effect", statement.effect());
        appendPrincipals(result, indentation, "Principal", statement.principals());
        appendPrincipals(result, indentation, "NotPrincipal", statement.notPrincipals());
        appendValueArrayField(result, indentation, "Action", statement.actions());
        appendValueArrayField(result, indentation, "NotAction", statement.notActions());
        appendValueArrayField(result, indentation, "Resource", statement.actions());
        appendValueArrayField(result, indentation, "NotResource", statement.notResources());
        appendConditions(result, indentation, statement.conditions());
        appendAdditionalJson(result, indentation, statement.additionalJsonFieldsUnsafe());
    }

    private void appendPrincipals(StringBuilder result, int indentation, String fieldName, List<IamPrincipal> principals) {
        if (principals.isEmpty()) {
            return;
        }

        if (principals.size() == 1 && principals.get(0).equals(IamPrincipal.ALL)) {
            appendStringField(result, indentation, fieldName, IamPrincipal.ALL.id());
            return;
        }

        Map<IamPrincipalType, List<String>> aggregatedPrincipals = new LinkedHashMap<>();
        principals.forEach(principal -> {
            aggregatedPrincipals.computeIfAbsent(principal.type(), t -> new ArrayList<>())
                                .add(principal.id());
        });

        appendObjectFieldStart(result, indentation, fieldName);
        aggregatedPrincipals.forEach((principalType, ids) -> {
            appendArrayField(result, indentation + 1, principalType.value(), ids);
        });
        if (!aggregatedPrincipals.isEmpty()) {
            trimComma(result);
        }
        appendObjectFieldEnd(result, indentation);
    }


    private void appendConditions(StringBuilder result, int indentation, List<IamCondition> conditions) {
        if (conditions.isEmpty()) {
            return;
        }

        Map<IamConditionOperator, Map<IamConditionKey, List<String>>> aggregatedConditions = new LinkedHashMap<>();
        conditions.forEach(condition -> {
            aggregatedConditions.computeIfAbsent(condition.operator(), t -> new LinkedHashMap<>())
                                .computeIfAbsent(condition.key(), t -> new ArrayList<>())
                                .add(condition.value());
        });

        appendObjectFieldStart(result, indentation, "Condition");
        aggregatedConditions.forEach((operator, keyValues) -> {
            appendObjectFieldStart(result, indentation + 1, operator.value());
            keyValues.forEach((key, values) -> {
                appendArrayField(result, indentation + 2, key.value(), values);
            });
            trimComma(result);
            appendObjectFieldEnd(result, indentation + 1);
        });
        trimComma(result);
        appendObjectFieldEnd(result, indentation);
    }

    private void appendAdditionalJson(StringBuilder result, int indentation, Map<String, String> jsonEntries) {
        if (jsonEntries.isEmpty()) {
            return;
        }

        jsonEntries.forEach((key, value) -> {
            result.append(indentationTable[indentation]);
            appendInlineString(result, key);
            appendColon(result);
            result.append(value);
            result.append(",");
        });
    }

    private void appendValueArrayField(StringBuilder result, int indentation,
                                       String fieldName, List<? extends IamValue> fieldValues) {
        List<String> values = new ArrayList<>(fieldValues.size());
        fieldValues.forEach(v -> values.add(v.value()));
        appendArrayField(result, indentation, fieldName, values);
    }

    private void appendArrayField(StringBuilder result, int indentation,
                                  String fieldName, List<String> fieldValues) {
        if (fieldValues.isEmpty()) {
            return;
        }

        if (fieldValues.size() == 1) {
            appendStringField(result, indentation, fieldName, fieldValues.get(0));
            return;
        }

        appendArrayFieldStart(result, indentation, fieldName);
        fieldValues.forEach(value -> {
            appendString(result, indentation + 1, value);
        });
        if (!fieldValues.isEmpty()) {
            trimComma(result);
        }
        appendArrayFieldEnd(result, indentation);
    }

    private void appendStringField(StringBuilder builder, int indentation, String key, IamValue value) {
        if (value == null) {
            return;
        }

        appendStringField(builder, indentation, key, value.value());
    }

    private void appendStringField(StringBuilder builder, int indentation, String key, String value) {
        if (value != null) {
            builder.append(indentationTable[indentation]);
            appendInlineString(builder, key);
            appendColon(builder);
            appendInlineString(builder, value);
            builder.append(",");
        }
    }

    private void appendObjectFieldStart(StringBuilder builder, int indentation, String key) {
        builder.append(indentationTable[indentation]);
        appendInlineString(builder, key);
        appendColon(builder);
        builder.append("{");
    }

    private void appendObjectFieldEnd(StringBuilder builder, int indentation) {
        builder.append(indentationTable[indentation]);
        builder.append("},");
    }

    private void appendArrayFieldStart(StringBuilder builder, int indentation, String key) {
        builder.append(indentationTable[indentation]);
        appendInlineString(builder, key);
        appendColon(builder);
        builder.append("[");
    }

    private void appendArrayFieldEnd(StringBuilder builder, int indentation) {
        builder.append(indentationTable[indentation]);
        builder.append("],");
    }

    private void appendColon(StringBuilder builder) {
        builder.append(":");
        builder.append(afterColonSpacing);
    }

    private void trimComma(StringBuilder builder) {
        builder.setLength(builder.length() - 1);
    }

    private void appendString(StringBuilder builder, int indentation, String stringToQuote) {
        builder.append(indentationTable[indentation]);
        appendInlineString(builder, stringToQuote);
        builder.append(",");
    }

    private void appendInlineString(StringBuilder builder, String stringToQuote) {
        builder.append('"')
               .append(escapeQuotes(stringToQuote))
               .append('"');
    }

    private String escapeQuotes(String stringToQuote) {
        return StringUtils.replace(stringToQuote, "\"", "\\\"");
    }

    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    public static class Builder implements IamPolicyWriter.Builder {
        private Boolean prettyPrint;

        public Builder() {
        }

        public Builder(DefaultIamPolicyWriter writer) {
            this.prettyPrint = writer.prettyPrint;
        }

        @Override
        public IamPolicyWriter.Builder prettyPrint(Boolean prettyPrint) {
            this.prettyPrint = prettyPrint;
            return this;
        }

        @Override
        public IamPolicyWriter build() {
            return new DefaultIamPolicyWriter(this);
        }
    }
}
