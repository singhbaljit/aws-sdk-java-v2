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

import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import software.amazon.awssdk.policybuilder.iam.IamCondition;
import software.amazon.awssdk.policybuilder.iam.IamPolicy;
import software.amazon.awssdk.policybuilder.iam.IamPolicyReader;
import software.amazon.awssdk.policybuilder.iam.IamPrincipal;
import software.amazon.awssdk.policybuilder.iam.IamStatement;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.protocols.jsoncore.JsonNodeParser;
import software.amazon.awssdk.utils.Validate;

public class DefaultIamPolicyReader implements IamPolicyReader {
    private static final JsonNodeParser JSON_NODE_PARSER = JsonNodeParser.create();

    @Override
    public IamPolicy read(String policyString) {
        JsonNode jsonNode = JSON_NODE_PARSER.parse(policyString);
        return readPolicy(jsonNode);
    }

    private IamPolicy readPolicy(JsonNode policyNode) {
        Map<String, JsonNode> policyObject = expectObject(policyNode, "Policy did not start with {");

        IamPolicy.Builder policy = IamPolicy.builder();

        policy.version(getString(policyObject, "Version"));
        policy.id(getString(policyObject, "Id"));
        policy.statements(readStatements(policyObject.get("Statement")));

        return policy.build();
    }

    private List<IamStatement> readStatements(JsonNode statementsNode) {
        if (statementsNode == null) {
            return null;
        }

        if (statementsNode.isArray()) {
            return statementsNode.asArray()
                                 .stream()
                                 .map(n -> expectObject(n, "Statement entry"))
                                 .map(this::readStatement)
                                 .collect(Collectors.toList());
        }

        if (statementsNode.isObject()) {
            return singletonList(readStatement(statementsNode.asObject()));
        }

        throw new IllegalArgumentException("Statement was not an array or object.");
    }

    private IamStatement readStatement(Map<String, JsonNode> statementObject) {
        IamStatement.Builder statement = IamStatement.builder();

        statement.sid(getString(statementObject, "Sid"));
        statement.effect(getString(statementObject, "Effect"));
        statement.principals(readPrincipals(statementObject, "Principal")); // TODO: setting varargs should be at least one.
        statement.notPrincipals(readPrincipals(statementObject, "NotPrincipal"));
        statement.actionStrings(readStringArray(statementObject, "Action"));
        statement.notActionStrings(readStringArray(statementObject, "NotAction"));
        statement.resourceStrings(readStringArray(statementObject, "Resource"));
        statement.notResourceStrings(readStringArray(statementObject, "NotResource"));
        statement.conditions(readConditions(statementObject.get("Condition")));
    }

    private List<IamPrincipal> readPrincipals(Map<String, JsonNode> statementObject, String name) {
        JsonNode principalsNode = statementObject.get(name);

        if (principalsNode == null) {
            return null;
        }

        if (principalsNode.isString() && principalsNode.asString().equals(IamPrincipal.ALL.id())) {
            return singletonList(IamPrincipal.ALL);
        }

        if (principalsNode.isObject()) {
            List<IamPrincipal> result = new ArrayList<>();
            principalsNode.asObject().forEach((id, value) -> {
                result.add(IamPrincipal.create(id, expectString(value, name + " entry value")));
            });
            return result;
        }

        throw new IllegalArgumentException(name + " was not \"" + IamPrincipal.ALL.id() + "\" or an object");
    }

    private Collection<IamCondition> readConditions(JsonNode conditionNode) {
        return null;
    }

    private List<String> readStringArray(Map<String, JsonNode> statementObject, String nodeKey) {
        JsonNode node = statementObject.get(nodeKey);

        if (node == null) {
            return null;
        }

        if (node.isString()) {
            return singletonList(node.asString());
        }

        if (node.isArray()) {
            return node.asArray()
                             .stream()
                             .map(n -> expectString(n, nodeKey + " entry"))
                             .collect(Collectors.toList());
        }

        throw new IllegalArgumentException(nodeKey + " was not an array or string.");
    }

    private String getString(Map<String, JsonNode> object, String key) {
        JsonNode node = object.get(key);
        if (node == null) {
            return null;
        }

        return expectString(node, key);
    }

    private String expectString(JsonNode node, String name) {
        Validate.isTrue(node.isString(), "%s was not a string");
        return node.asString();
    }

    private Map<String, JsonNode> expectObject(JsonNode node, String name) {
        Validate.isTrue(node.isObject(), "%s was not an object", name);
        return node.asObject();
    }
}
