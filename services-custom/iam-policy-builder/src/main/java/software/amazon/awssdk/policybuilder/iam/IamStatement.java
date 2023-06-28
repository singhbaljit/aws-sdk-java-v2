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

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public interface IamStatement extends ToCopyableBuilder<IamStatement.Builder, IamStatement> {
    static IamStatement fromJson(String json) {
        return null;
    }

    static Builder builder() {
        return null;
    }

    String sid();
    IamEffect effect();
    List<IamPrincipal> principals();
    List<IamAction> actions();
    List<IamResource> resources();
    List<IamCondition> conditions();
    Map<String, String> additionalJsonFields();

    interface Builder extends CopyableBuilder<Builder, IamStatement> {

        Builder sid(String sid);
        Builder effect(IamEffect effect);

        Builder principals(List<IamPrincipal> principals);
        Builder principals(IamPrincipal... principals);
        Builder addPrincipal(IamPrincipal principal);
        Builder addPrincipal(Consumer<IamPrincipal.Builder> principal);
        Builder addPrincipal(IamPrincipalType iamPrincipalType, String... principals);

        Builder actions(List<IamAction> actions);
        Builder actions(IamAction... actions);
        Builder addAction(IamAction action);
        Builder addAction(String action);

        Builder resources(List<IamResource> resources);
        Builder resources(IamResource... resources);
        Builder addResource(IamResource resource);
        Builder addResource(String resource);

        Builder conditions(List<IamCondition> conditions);
        Builder conditions(IamCondition... conditions);
        Builder addCondition(IamCondition condition);
        Builder addCondition(Consumer<IamCondition.Builder> condition);
        Builder addCondition(IamConditionOperator operator, String key, String... values);

        Builder putAdditionalJsonField(String key, String json);

        IamStatement build();
    }
}
