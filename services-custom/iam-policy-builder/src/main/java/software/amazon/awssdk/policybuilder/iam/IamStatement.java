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
import java.util.function.Consumer;
import software.amazon.awssdk.policybuilder.iam.internal.DefaultIamStatement;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

/**
 * A statement is the formal description of a single permission, and is always
 * contained within a policy object.
 * <p>
 * A statement describes a rule for allowing or denying access to a specific AWS
 * resource based on how the resource is being accessed, and who is attempting
 * to access the resource. Statements can also optionally contain a list of
 * conditions that specify when a statement is to be honored.
 * <p>
 * For example, consider a statement that:
 * <ul>
 * <li>allows access (the effect)
 * <li>for a list of specific AWS account IDs (the principals)
 * <li>when accessing an SQS queue (the resource)
 * <li>using the SendMessage operation (the action)
 * <li>and the request occurs before a specific date (a condition)
 * </ul>
 *
 * <p>
 * Statements takes the form:  "A has permission to do B to C where D applies".
 * <ul>
 *   <li>A is the <b>principal</b> - the AWS account that is making a request to
 *       access or modify one of your AWS resources.
 *   <li>B is the <b>action</b> - the way in which your AWS resource is being accessed or modified, such
 *       as sending a message to an Amazon SQS queue, or storing an object in an Amazon S3 bucket.
 *   <li>C is the <b>resource</b> - your AWS entity that the principal wants to access, such
 *       as an Amazon SQS queue, or an object stored in Amazon S3.
 *   <li>D is the set of <b>conditions</b> - optional constraints that specify when to allow or deny
 *       access for the principal to access your resource.  Many expressive conditions are available,
 *       some specific to each service.  For example you can use date conditions to allow access to
 *       your resources only after or before a specific time.
 * </ul>
 *
 * <p>
 * There are many resources and conditions available for use in statements, and
 * you can combine them to form fine grained custom access control polices.
 *
 * <p>
 * Statements are typically attached to a {@link IamPolicy}.
 *
 * <p>
 * For more information, see <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/">The IAM User Guide</a>
 *
 * <h2>Usage Examples</h2>
 * <b>Create a statement that allows a role to write items to an Amazon DynamoDB table.</b>
 * {@snippet :
 * IamStatement statement =
 *     IamStatement.builder()
 *                 .effect(IamEffect.ALLOW)
 *                 .addAction("dynamodb:PutItem")
 *                 .addResource("arn:aws:dynamodb:us-east-2:123456789012:table/Books")
 *                 .build();
 * }
 *
 * <p>
 * <b>Create a statement that denies access to all users.</b>
 * {@snippet :
 * IamStatement statement =
 *     IamStatement.builder()
 *                 .effect(IamEffect.DENY)
 *                 .addPrincipal(IamPrincipal.ALL)
 *                 .build();
 * }
 *
 * @see IamPolicy
 * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_statement.html">Usage Guide</a>
 */
public interface IamStatement extends ToCopyableBuilder<IamStatement.Builder, IamStatement> {
    /**
     * Create a {@link Builder} for an {@code IamStatement}.
     */
    static Builder builder() {
        return new DefaultIamStatement.Builder();
    }

    /**
     * Retrieve the value set by {@link Builder#sid(String)}.
     */
    String sid();

    /**
     * Retrieve the value set by {@link Builder#effect(IamEffect)}.
     */
    IamEffect effect();

    /**
     * Retrieve the value set by {@link Builder#principals(Collection)}.
     */
    List<IamPrincipal> principals();

    /**
     * Retrieve the value set by {@link Builder#notPrincipals(Collection)}.
     */
    List<IamPrincipal> notPrincipals();

    /**
     * Retrieve the value set by {@link Builder#actions(Collection)}.
     */
    List<IamAction> actions();

    /**
     * Retrieve the value set by {@link Builder#notActions(Collection)}.
     */
    List<IamAction> notActions();

    /**
     * Retrieve the value set by {@link Builder#resources(Collection)}.
     */
    List<IamResource> resources();

    /**
     * Retrieve the value set by {@link Builder#notResources(Collection)}.
     */
    List<IamResource> notResources();

    /**
     * Retrieve the value set by {@link Builder#conditions(Collection)}.
     */
    List<IamCondition> conditions();

    interface Builder extends CopyableBuilder<Builder, IamStatement> {
        Builder sid(String sid);
        Builder effect(IamEffect effect);
        Builder effect(String effect);

        Builder principals(Collection<IamPrincipal> principals);
        Builder addPrincipal(IamPrincipal principal);
        Builder addPrincipal(Consumer<IamPrincipal.Builder> principal);
        Builder addPrincipal(IamPrincipalType iamPrincipalType, String principal);
        Builder addPrincipal(String iamPrincipalType, String principal);
        Builder addPrincipals(IamPrincipalType iamPrincipalType, Collection<String> principals);
        Builder addPrincipals(String iamPrincipalType, Collection<String> principals);

        Builder notPrincipals(Collection<IamPrincipal> principals);
        Builder addNotPrincipal(IamPrincipal principal);
        Builder addNotPrincipal(Consumer<IamPrincipal.Builder> principal);
        Builder addNotPrincipal(IamPrincipalType iamPrincipalType, String principal);
        Builder addNotPrincipal(String iamPrincipalType, String principal);
        Builder addNotPrincipals(IamPrincipalType iamPrincipalType, Collection<String> principals);
        Builder addNotPrincipals(String iamPrincipalType, Collection<String> principals);

        Builder actions(Collection<IamAction> actions);
        Builder actionStrings(Collection<String> actions);
        Builder addAction(IamAction action);
        Builder addAction(String action);

        Builder notActions(Collection<IamAction> actions);
        Builder notActionStrings(Collection<String> actions);
        Builder addNotAction(IamAction action);
        Builder addNotAction(String action);

        Builder resources(Collection<IamResource> resources);
        Builder resourceStrings(Collection<String> resources);
        Builder addResource(IamResource resource);
        Builder addResource(String resource);

        Builder notResources(Collection<IamResource> resources);
        Builder notResourceStrings(Collection<String> resources);
        Builder addNotResource(IamResource resource);
        Builder addNotResource(String resource);

        Builder conditions(Collection<IamCondition> conditions);
        Builder addCondition(IamCondition condition);
        Builder addCondition(Consumer<IamCondition.Builder> condition);
        Builder addCondition(IamConditionOperator operator, IamConditionKey key, String value);
        Builder addCondition(IamConditionOperator operator, String key, String value);
        Builder addCondition(String operator, String key, String values);
        Builder addConditions(IamConditionOperator operator, IamConditionKey key, Collection<String> values);
        Builder addConditions(IamConditionOperator operator, String key, Collection<String> values);
        Builder addConditions(String operator, String key, Collection<String> values);
    }
}
