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

package software.amazon.awssdk.policybuilder.iam;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import software.amazon.awssdk.policybuilder.iam.internal.DefaultIamStatement;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public interface IamStatement extends ToCopyableBuilder<IamStatement.Builder, IamStatement>, IamStructure {
    static IamStatement fromJson(String json) {
        // TODO
        return null;
    }

    static Builder builder() {
        return new DefaultIamStatement.Builder();
    }

    String sid();
    IamEffect effect();
    List<IamPrincipal> principals();
    List<IamPrincipal> notPrincipals();
    List<IamAction> actions();
    List<IamAction> notActions();
    List<IamResource> resources();
    List<IamResource> notResources();
    List<IamCondition> conditions();
    Map<String, String> additionalJsonFieldsUnsafe();

    interface Builder extends CopyableBuilder<Builder, IamStatement>, IamStructure.Builder<Builder> {

        Builder sid(String sid);
        Builder effect(IamEffect effect);

        Builder principals(Collection<IamPrincipal> principals);
        Builder principals(IamPrincipal... principals);
        Builder addPrincipal(IamPrincipal principal);
        Builder addPrincipal(Consumer<IamPrincipal.Builder> principal);
        Builder addPrincipals(IamPrincipalType iamPrincipalType, String... principals);
        Builder addPrincipals(String iamPrincipalType, String... principals);

        Builder notPrincipals(Collection<IamPrincipal> principals);
        Builder notPrincipals(IamPrincipal... principals);
        Builder addNotPrincipal(IamPrincipal principal);
        Builder addNotPrincipal(Consumer<IamPrincipal.Builder> principal);
        Builder addNotPrincipals(IamPrincipalType iamPrincipalType, String... principals);
        Builder addNotPrincipals(String iamPrincipalType, String... principals);

        Builder actions(Collection<IamAction> actions);
        Builder actions(IamAction... actions);
        Builder addAction(IamAction action);
        Builder addAction(String action);

        Builder notActions(Collection<IamAction> actions);
        Builder notActions(IamAction... actions);
        Builder addNotAction(IamAction action);
        Builder addNotAction(String action);

        Builder resources(Collection<IamResource> resources);
        Builder resources(IamResource... resources);
        Builder addResource(IamResource resource);
        Builder addResource(String resource);

        Builder notResources(Collection<IamResource> resources);
        Builder notResources(IamResource... resources);
        Builder addNotResource(IamResource resource);
        Builder addNotResource(String resource);

        Builder conditions(Collection<IamCondition> conditions);
        Builder conditions(IamCondition... conditions);
        Builder addCondition(IamCondition condition);
        Builder addCondition(Consumer<IamCondition.Builder> condition);
        Builder addCondition(IamConditionOperator operator, IamConditionKey key, String value);
        Builder addCondition(String operator, String key, String values);
        Builder addConditions(IamConditionOperator operator, IamConditionKey key, Collection<String> values);
        Builder addConditions(IamConditionOperator operator, IamConditionKey key, String... values);
        Builder addConditions(String operator, String key, Collection<String> values);
        Builder addConditions(String operator, String key, String... values);

        IamStatement build();
    }
}
