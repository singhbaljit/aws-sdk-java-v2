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
        WriteState state = new WriteState();

        state.result.append("{");

        state.increaseIndentation();
        appendStringField(state, "Version", policy.version());
        appendStringField(state, "Id", policy.id());
        appendStatements(state, policy.statements());
        appendAdditionalJson(state, policy.additionalJsonFieldsUnsafe());
        state.decreaseIndentation();

        trimComma(state);
        state.result.append(indentationTable[0]).append("}");
        return state.result.toString();
    }

    private void appendStatements(WriteState state, List<IamStatement> statements) {
        if (statements.isEmpty()) {
            return;
        }

        if (statements.size() == 1) {
            appendObjectFieldStart(state, "Statement");
            state.increaseIndentation();
            appendStatementBody(state, statements.get(0));
            state.decreaseIndentation();
            trimComma(state);
            appendObjectFieldEnd(state);
            return;
        }

        appendArrayFieldStart(state, "Statement");

        state.increaseIndentation();
        statements.forEach(statement -> appendStatement(state, statement));
        state.decreaseIndentation();
        trimComma(state);

        appendArrayFieldEnd(state);
    }

    private void appendStatement(WriteState state, IamStatement statement) {
        state.result.append(indentationTable[state.indentation]).append("{");
        state.increaseIndentation();
        appendStatementBody(state, statement);
        trimComma(state);
        state.decreaseIndentation();
        state.result.append(indentationTable[state.indentation]).append("},");
    }

    private void appendStatementBody(WriteState state, IamStatement statement) {
        appendStringField(state, "Sid", statement.sid());
        appendStringField(state, "Effect", statement.effect());
        appendPrincipals(state, "Principal", statement.principals());
        appendPrincipals(state, "NotPrincipal", statement.notPrincipals());
        appendValueArrayField(state, "Action", statement.actions());
        appendValueArrayField(state, "NotAction", statement.notActions());
        appendValueArrayField(state, "Resource", statement.actions());
        appendValueArrayField(state, "NotResource", statement.notResources());
        appendConditions(state, statement.conditions());
        appendAdditionalJson(state, statement.additionalJsonFieldsUnsafe());
    }

    private void appendPrincipals(WriteState state, String fieldName, List<IamPrincipal> principals) {
        if (principals.isEmpty()) {
            return;
        }

        if (principals.size() == 1 && principals.get(0).equals(IamPrincipal.ALL)) {
            appendStringField(state, fieldName, IamPrincipal.ALL.id());
            return;
        }

        Map<IamPrincipalType, List<String>> aggregatedPrincipals = new LinkedHashMap<>();
        principals.forEach(principal -> {
            aggregatedPrincipals.computeIfAbsent(principal.type(), t -> new ArrayList<>())
                                .add(principal.id());
        });

        appendObjectFieldStart(state, fieldName);
        state.increaseIndentation();
        aggregatedPrincipals.forEach((principalType, ids) -> {
            appendArrayField(state, principalType.value(), ids);
        });
        state.decreaseIndentation();
        if (!aggregatedPrincipals.isEmpty()) {
            trimComma(state);
        }
        appendObjectFieldEnd(state);
    }


    private void appendConditions(WriteState state, List<IamCondition> conditions) {
        if (conditions.isEmpty()) {
            return;
        }

        Map<IamConditionOperator, Map<IamConditionKey, List<String>>> aggregatedConditions = new LinkedHashMap<>();
        conditions.forEach(condition -> {
            aggregatedConditions.computeIfAbsent(condition.operator(), t -> new LinkedHashMap<>())
                                .computeIfAbsent(condition.key(), t -> new ArrayList<>())
                                .add(condition.value());
        });

        appendObjectFieldStart(state, "Condition");
        aggregatedConditions.forEach((operator, keyValues) -> {
            state.increaseIndentation();
            appendObjectFieldStart(state, operator.value());
            state.increaseIndentation();
            keyValues.forEach((key, values) -> {
                appendArrayField(state, key.value(), values);
            });
            state.decreaseIndentation();
            trimComma(state);
            appendObjectFieldEnd(state);
            state.decreaseIndentation();
        });
        trimComma(state);
        appendObjectFieldEnd(state);
    }

    private void appendAdditionalJson(WriteState state, Map<String, String> jsonEntries) {
        if (jsonEntries.isEmpty()) {
            return;
        }

        jsonEntries.forEach((key, value) -> {
            state.result.append(indentationTable[state.indentation]);
            appendInlineString(state, key);
            appendColon(state);
            state.result.append(value);
            state.result.append(",");
        });
    }

    private void appendValueArrayField(WriteState state,
                                       String fieldName, List<? extends IamValue> fieldValues) {
        List<String> values = new ArrayList<>(fieldValues.size());
        fieldValues.forEach(v -> values.add(v.value()));
        appendArrayField(state, fieldName, values);
    }

    private void appendArrayField(WriteState state,
                                  String fieldName, List<String> fieldValues) {
        if (fieldValues.isEmpty()) {
            return;
        }

        if (fieldValues.size() == 1) {
            appendStringField(state, fieldName, fieldValues.get(0));
            return;
        }

        appendArrayFieldStart(state, fieldName);
        fieldValues.forEach(value -> {
            state.increaseIndentation();
            appendString(state, value);
            state.decreaseIndentation();
        });
        if (!fieldValues.isEmpty()) {
            trimComma(state);
        }
        appendArrayFieldEnd(state);
    }

    private void appendStringField(WriteState state, String key, IamValue value) {
        if (value == null) {
            return;
        }

        appendStringField(state, key, value.value());
    }

    private void appendStringField(WriteState state, String key, String value) {
        if (value != null) {
            state.result.append(indentationTable[state.indentation]);
            appendInlineString(state, key);
            appendColon(state);
            appendInlineString(state, value);
            state.result.append(",");
        }
    }

    private void appendObjectFieldStart(WriteState state, String key) {
        state.result.append(indentationTable[state.indentation]);
        appendInlineString(state, key);
        appendColon(state);
        state.result.append("{");
    }

    private void appendObjectFieldEnd(WriteState state) {
        state.result.append(indentationTable[state.indentation]);
        state.result.append("},");
    }

    private void appendArrayFieldStart(WriteState state, String key) {
        state.result.append(indentationTable[state.indentation]);
        appendInlineString(state, key);
        appendColon(state);
        state.result.append("[");
    }

    private void appendArrayFieldEnd(WriteState state) {
        state.result.append(indentationTable[state.indentation]);
        state.result.append("],");
    }

    private void appendColon(WriteState state) {
        state.result.append(":");
        state.result.append(afterColonSpacing);
    }

    private void trimComma(WriteState state) {
        state.result.setLength(state.result.length() - 1);
    }

    private void appendString(WriteState state, String stringToQuote) {
        state.result.append(indentationTable[state.indentation]);
        appendInlineString(state, stringToQuote);
        state.result.append(",");
    }

    private void appendInlineString(WriteState state, String stringToQuote) {
        state.result.append('"')
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

    private static class WriteState {
        private final StringBuilder result = new StringBuilder();
        private int indentation = 0;

        private void increaseIndentation() {
            ++indentation;
        }

        private void decreaseIndentation() {
            --indentation;
        }
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
