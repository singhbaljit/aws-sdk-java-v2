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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import software.amazon.awssdk.policybuilder.iam.IamAction;
import software.amazon.awssdk.policybuilder.iam.IamCondition;
import software.amazon.awssdk.policybuilder.iam.IamConditionKey;
import software.amazon.awssdk.policybuilder.iam.IamConditionOperator;
import software.amazon.awssdk.policybuilder.iam.IamEffect;
import software.amazon.awssdk.policybuilder.iam.IamPrincipal;
import software.amazon.awssdk.policybuilder.iam.IamPrincipalType;
import software.amazon.awssdk.policybuilder.iam.IamResource;
import software.amazon.awssdk.policybuilder.iam.IamStatement;
import software.amazon.awssdk.utils.Validate;

public class DefaultIamStatement implements IamStatement {
    private final String sid;
    private final IamEffect effect;
    private final List<IamPrincipal> principals;
    private final List<IamAction> actions;
    private final List<IamResource> resources;
    private final List<IamCondition> conditions;
    private final Map<String, String> additionalJsonFields;

    public DefaultIamStatement(Builder builder) {
        this.sid = builder.sid;
        this.effect = Validate.paramNotNull(builder.effect, "statementEffect");
        this.principals = new ArrayList<>(builder.principals);
        this.actions = Validate.notEmpty(new ArrayList<>(builder.actions), "At least one action is required");
        this.resources = Validate.notEmpty(new ArrayList<>(builder.resources), "At least one resource is required");
        this.conditions = new ArrayList<>(builder.conditions);
        this.additionalJsonFields = new LinkedHashMap<>(builder.additionalJsonFields);
    }

    @Override
    public String sid() {
        return sid;
    }

    @Override
    public IamEffect effect() {
        return effect;
    }

    @Override
    public List<IamPrincipal> principals() {
        return Collections.unmodifiableList(principals);
    }

    @Override
    public List<IamAction> actions() {
        return Collections.unmodifiableList(actions);
    }

    @Override
    public List<IamResource> resources() {
        return Collections.unmodifiableList(resources);
    }

    @Override
    public List<IamCondition> conditions() {
        return Collections.unmodifiableList(conditions);
    }

    @Override
    public Map<String, String> additionalJsonFields() {
        return Collections.unmodifiableMap(additionalJsonFields);
    }

    @Override
    public IamStatement.Builder toBuilder() {
        return new Builder(this);
    }

    public static class Builder implements IamStatement.Builder {
        private String sid;
        private IamEffect effect;
        private final List<IamPrincipal> principals = new ArrayList<>();
        private final List<IamAction> actions = new ArrayList<>();
        private final List<IamResource> resources = new ArrayList<>();
        private final List<IamCondition> conditions = new ArrayList<>();
        private final Map<String, String> additionalJsonFields = new LinkedHashMap<>();

        public Builder () {
        }

        public Builder(DefaultIamStatement statement) {
            this.sid = statement.sid;
            this.effect = statement.effect;
            this.principals.addAll(statement.principals);
            this.actions.addAll(statement.actions);
            this.resources.addAll(statement.resources);
            this.conditions.addAll(statement.conditions);
            this.additionalJsonFields.putAll(statement.additionalJsonFields);

        }

        @Override
        public IamStatement.Builder sid(String sid) {
            this.sid = sid;
            return this;
        }

        @Override
        public IamStatement.Builder effect(IamEffect effect) {
            this.effect = effect;
            return this;
        }

        @Override
        public IamStatement.Builder principals(Collection<IamPrincipal> principals) {
            this.principals.clear();
            this.principals.addAll(principals);
            return this;
        }

        @Override
        public IamStatement.Builder principals(IamPrincipal... principals) {
            this.principals.clear();
            this.principals.addAll(Arrays.asList(principals));
            return this;
        }

        @Override
        public IamStatement.Builder addPrincipal(IamPrincipal principal) {
            this.principals.add(principal);
            return this;
        }

        @Override
        public IamStatement.Builder addPrincipal(Consumer<IamPrincipal.Builder> principal) {
            this.principals.add(IamPrincipal.builder().applyMutation(principal).build());
            return this;
        }

        @Override
        public IamStatement.Builder addPrincipals(IamPrincipalType iamPrincipalType, String... principals) {
            for (String principal : principals) {
                this.principals.add(IamPrincipal.create(iamPrincipalType, principal));
            }
            return this;
        }

        @Override
        public IamStatement.Builder addPrincipals(String iamPrincipalType, String... principals) {
            return addPrincipals(IamPrincipalType.create(iamPrincipalType), principals);
        }

        @Override
        public IamStatement.Builder actions(Collection<IamAction> actions) {
            this.actions.clear();
            this.actions.addAll(actions);
            return this;
        }

        @Override
        public IamStatement.Builder actions(IamAction... actions) {
            this.actions.clear();
            this.actions.addAll(Arrays.asList(actions));
            return this;
        }

        @Override
        public IamStatement.Builder addAction(IamAction action) {
            this.actions.add(action);
            return this;
        }

        @Override
        public IamStatement.Builder addAction(String action) {
            this.actions.add(IamAction.create(action));
            return this;
        }

        @Override
        public IamStatement.Builder resources(Collection<IamResource> resources) {
            this.resources.clear();
            this.resources.addAll(resources);
            return this;
        }

        @Override
        public IamStatement.Builder resources(IamResource... resources) {
            this.resources.clear();
            this.resources.addAll(Arrays.asList(resources));
            return this;
        }

        @Override
        public IamStatement.Builder addResource(IamResource resource) {
            this.resources.add(resource);
            return this;
        }

        @Override
        public IamStatement.Builder addResource(String resource) {
            this.resources.add(IamResource.create(resource));
            return this;
        }

        @Override
        public IamStatement.Builder conditions(Collection<IamCondition> conditions) {
            this.conditions.clear();
            this.conditions.addAll(conditions);
            return this;
        }

        @Override
        public IamStatement.Builder conditions(IamCondition... conditions) {
            this.conditions.clear();
            this.conditions.addAll(Arrays.asList(conditions));
            return this;
        }

        @Override
        public IamStatement.Builder addCondition(IamCondition condition) {
            this.conditions.add(condition);
            return this;
        }

        @Override
        public IamStatement.Builder addCondition(Consumer<IamCondition.Builder> condition) {
            this.conditions.add(IamCondition.builder().applyMutation(condition).build());
            return this;
        }

        @Override
        public IamStatement.Builder addConditions(IamConditionOperator operator,
                                                  IamConditionKey key,
                                                  Collection<String> values) {
            for (String value : values) {
                this.conditions.add(IamCondition.create(operator, key, value));
            }
            return this;
        }

        @Override
        public IamStatement.Builder addConditions(IamConditionOperator operator, IamConditionKey key, String... values) {
            return addConditions(operator, key, Arrays.asList(values));
        }

        @Override
        public IamStatement.Builder addConditions(String operator, String key, Collection<String> values) {
            return addConditions(IamConditionOperator.create(operator), IamConditionKey.create(key), values);
        }

        @Override
        public IamStatement.Builder addConditions(String operator, String key, String... values) {
            return addConditions(operator, key, Arrays.asList(values));
        }

        @Override
        public IamStatement.Builder putAdditionalJsonField(String key, String json) {
            this.additionalJsonFields.put(key, json);
            return this;
        }

        @Override
        public IamStatement build() {
            return new DefaultIamStatement(this);
        }
    }
}
