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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.policybuilder.iam.IamAction;
import software.amazon.awssdk.policybuilder.iam.IamCondition;
import software.amazon.awssdk.policybuilder.iam.IamPolicy;
import software.amazon.awssdk.policybuilder.iam.IamPolicyWriter;
import software.amazon.awssdk.policybuilder.iam.IamPrincipal;
import software.amazon.awssdk.policybuilder.iam.IamPrincipalType;
import software.amazon.awssdk.policybuilder.iam.IamResource;
import software.amazon.awssdk.policybuilder.iam.IamStatement;
import software.amazon.awssdk.policybuilder.iam.IamValue;
import software.amazon.awssdk.utils.StringUtils;

public class DefaultIamPolicyWriter implements IamPolicyWriter {
    private static final String INDENTATION_1 = "    ";
    private static final String INDENTATION_2 = INDENTATION_1 + INDENTATION_1;
    private static final String INDENTATION_3 = INDENTATION_2 + INDENTATION_1;
    private static final String INDENTATION_4 = INDENTATION_3 + INDENTATION_1;
    private static final String INDENTATION_5 = INDENTATION_4 + INDENTATION_1;

    private static final IamPolicyWriter INSTANCE = new DefaultIamPolicyWriter();

    public static IamPolicyWriter create() {
        return INSTANCE;
    }

    @Override
    public String write(IamPolicy policy) {
        StringBuilder result = new StringBuilder();
        result.append("{\n");

        appendStringField(result, INDENTATION_1, "Version", policy.version());
        appendStringField(result, INDENTATION_1, "Id", policy.version());
        appendStatements(result, policy.statements());

        trimLastLineComma(result);
        result.append("}\n");
        return result.toString();
    }

    private void appendStatements(StringBuilder result, List<IamStatement> statements) {
        if (statements.isEmpty()) {
            return;
        }

        appendArrayFieldStart(result, INDENTATION_1, "Statement");
        statements.forEach(statement -> appendStatement(result, statement));
        trimLastLineComma(result);
        appendArrayFieldEnd(result, INDENTATION_1);
    }

    private void appendStatement(StringBuilder result, IamStatement statement) {
        result.append(INDENTATION_1).append("{\n");
        appendStringField(result, INDENTATION_2, "Sid", statement.sid());
        appendStringField(result, INDENTATION_2, "Effect", statement.effect());
        appendPrincipals(result, statement.principals());
        appendActions(result, statement.actions());
        appendResources(result, statement.resources());
        appendConditions(result, statement.conditions());
        trimLastLineComma(result);
        result.append(INDENTATION_1).append("},");
    }

    private void appendConditions(StringBuilder result, List<IamCondition> conditions) {
        // TODO
    }

    private void appendPrincipals(StringBuilder result, List<IamPrincipal> principals) {
        if (principals.isEmpty()) {
            return;
        }

        Map<Boolean, Map<IamPrincipalType, List<String>>> aggregatedPrincipals = new LinkedHashMap<>(2);
        principals.forEach(principal -> {
            aggregatedPrincipals.computeIfAbsent(principal.notPrincipal(), t -> new LinkedHashMap<>())
                                .computeIfAbsent(principal.type(), t -> new ArrayList<>())
                                .addAll(principal.ids());
        });

        appendPrincipals(result, "Principal", aggregatedPrincipals.get(true));
        appendPrincipals(result, "NotPrincipal", aggregatedPrincipals.get(false));
    }

    private void appendPrincipals(StringBuilder result,
                                  String principalKey,
                                  Map<IamPrincipalType, List<String>> principals) {
        if (principals == null) {
            return;
        }

        appendObjectFieldStart(result, INDENTATION_2, principalKey);
        principals.forEach((principalType, ids) -> {
            appendArrayFieldStart(result, INDENTATION_3, principalType.value());
            ids.forEach(action -> {
                appendQuoteString(result, INDENTATION_4, action);
            });
            if (!ids.isEmpty()) {
                trimLastLineComma(result);
            }
            appendArrayFieldEnd(result, INDENTATION_3);
        });
        if (!principals.isEmpty()) {
            trimLastLineComma(result);
        }
        appendObjectFieldEnd(result, INDENTATION_2);
    }

    private void appendActions(StringBuilder result, List<IamAction> actions) {
        if (actions.isEmpty()) {
            return;
        }

        Map<Boolean, List<String>> aggregatedActions = new LinkedHashMap<>(2);
        actions.forEach(action -> {
            aggregatedActions.computeIfAbsent(action.notAction(), t -> new ArrayList<>())
                             .add(action.value());
        });

        appendActions(result, "Action", aggregatedActions.get(true));
        appendActions(result, "NotAction", aggregatedActions.get(false));
    }

    private void appendActions(StringBuilder result, String actionKey, List<String> actions) {
        if (actions == null) {
            return;
        }

        appendArrayFieldStart(result, INDENTATION_2, actionKey);
        actions.forEach(action -> {
            appendQuoteString(result, INDENTATION_4, action);
        });
        if (!actions.isEmpty()) {
            trimLastLineComma(result);
        }
        appendArrayFieldEnd(result, INDENTATION_2);
    }

    private void appendResources(StringBuilder result, List<IamResource> resources) {
        if (resources.isEmpty()) {
            return;
        }

        Map<Boolean, List<String>> aggregatedResources = new LinkedHashMap<>(2);
        resources.forEach(resource -> {
            aggregatedResources.computeIfAbsent(resource.notResource(), t -> new ArrayList<>())
                             .add(resource.value());
        });

        appendResources(result, "Resource", aggregatedResources.get(true));
        appendResources(result, "NotResource", aggregatedResources.get(false));
    }

    private void appendResources(StringBuilder result, String resourceKey, List<String> resources) {
        if (resources == null) {
            return;
        }

        appendArrayFieldStart(result, INDENTATION_2, resourceKey);
        resources.forEach(resource -> {
            appendQuoteString(result, INDENTATION_4, resource);
        });
        if (!resources.isEmpty()) {
            trimLastLineComma(result);
        }
        appendArrayFieldEnd(result, INDENTATION_2);
    }

    private void appendStringField(StringBuilder builder, String indentation, String key, IamValue value) {
        if (value == null) {
            return;
        }

        appendStringField(builder, indentation, key, value.value());
    }

    private void appendStringField(StringBuilder builder, String indentation, String key, String value) {
        if (value != null) {
            builder.append(indentation);
            appendInlineQuoteString(builder, key);
            builder.append(": ");
            appendInlineQuoteString(builder, value);
            builder.append(",\n");
        }
    }

    private void appendObjectFieldStart(StringBuilder builder, String indentation, String key) {
        builder.append(indentation);
        appendInlineQuoteString(builder, key);
        builder.append(": ");
        builder.append("{\n");
    }

    private void appendObjectFieldEnd(StringBuilder builder, String indentation) {
        builder.append(indentation);
        builder.append("},\n");
    }

    private void appendArrayFieldStart(StringBuilder builder, String indentation, String key) {
        builder.append(indentation);
        appendInlineQuoteString(builder, key);
        builder.append(": ");
        builder.append("[\n");
    }

    private void appendArrayFieldEnd(StringBuilder builder, String indentation) {
        builder.append(indentation);
        builder.append("],\n");
    }

    private void trimLastLineComma(StringBuilder builder) {
        builder.setLength(builder.length() - 2);
        builder.append('\n');
    }

    private void appendQuoteString(StringBuilder builder, String indentation, String stringToQuote) {
        builder.append(indentation)
               .append(stringToQuote)
               .append(",\n");
    }

    private void appendInlineQuoteString(StringBuilder builder, String stringToQuote) {
        builder.append('"')
               .append(escapeStringForQuote(stringToQuote))
               .append('"');
    }

    private String escapeStringForQuote(String stringToQuote) {
        return StringUtils.replace(stringToQuote, "\"", "\\\"");
    }

    @Override
    public Builder toBuilder() {
        return null;
    }
}
