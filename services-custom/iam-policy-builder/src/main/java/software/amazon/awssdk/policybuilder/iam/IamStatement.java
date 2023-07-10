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
        /**
         * Configure the <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_sid.html">{@code
         * Sid}</a> element of the policy, specifying an identifier for the statement.
         * <p>
         * {@snippet :
         * IamStatement statement =
         *     IamStatement.builder()
         *                 .sid("1")
         *                 // Additional fields
         *                 .build();
         * }
         *
         * @see <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_sid.html">Usage Guide</a>
         */
        Builder sid(String sid);

        /**
         * Configure the
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_effect.html">{@code Effect}</a>
         * element of the policy, specifying whether the statement results in an allow or deny.
         * <p>
         * This value is required.
         * <p>
         * {@snippet :
         * IamStatement statement =
         *     IamStatement.builder()
         *                 .effect(IamEffect.DENY)
         *                 .addPrincipal(IamPrincipal.ALL)
         *                 .build();
         * }
         *
         * @see <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_effect.html">Usage Guide</a>
         */
        Builder effect(IamEffect effect);

        /**
         * Configure the
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_effect.html">{@code Effect}</a>
         * element of the policy, specifying whether the statement results in an allow or deny.
         * <p>
         * This works the same as {@link #effect(IamEffect)}, except you do not need to {@link IamEffect}. This value is required.
         * <p>
         * {@snippet :
         * IamStatement statement =
         *     IamStatement.builder()
         *                 .effect("Deny")
         *                 .addPrincipal(IamPrincipal.ALL)
         *                 .build();
         * }
         *
         * @see <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_effect.html">Usage Guide</a>
         */
        Builder effect(String effect);

        /**
         * Configure the
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_principal.html">{@code
         * Principal}</a> element of the statement, specifying the principals that are allowed or denied
         * access to a resource.
         * <p>
         * This will replace any other principals already added to the statement.
         * <p>
         * {@snippet :
         * IamStatement statement =
         *     IamStatement.builder()
         *                 .effect(IamEffect.DENY)
         *                 .principals(Arrays.asList(IamPrincipal.ALL))
         *                 .build();
         * }
         * @see
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_principal.html">Usage Guide</a>
         */
        Builder principals(Collection<IamPrincipal> principals);

        /**
         * Append a
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_principal.html">{@code
         * Principal}</a> to this statement, specifying a principal that is allowed or denied access to
         * a resource.
         * <p>
         * {@snippet :
         * IamStatement statement =
         *     IamStatement.builder()
         *                 .effect(IamEffect.DENY)
         *                 .addPrincipal(IamPrincipal.ALL)
         *                 .build();
         * }
         * @see
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_principal.html">Usage Guide</a>
         */
        Builder addPrincipal(IamPrincipal principal);

        /**
         * Append a
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_principal.html">{@code
         * Principal}</a> to this statement, specifying a principal that is allowed or denied access to
         * a resource.
         * <p>
         * This works the same as {@link #addPrincipal(IamPrincipal)}, except you do not need to specify {@code IamPrincipal
         * .builder()} or {@code build()}.
         * <p>
         * {@snippet :
         * IamStatement statement =
         *     IamStatement.builder()
         *                 .effect(IamEffect.DENY)
         *                 .addPrincipal(p -> p.type(IamPrincipalType.AWS).id("*"))
         *                 .build();
         * }
         * @see
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_principal.html">Usage Guide</a>
         */
        Builder addPrincipal(Consumer<IamPrincipal.Builder> principal);

        /**
         * Append a
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_principal.html">{@code
         * Principal}</a> to this statement, specifying a principal that is allowed or denied access to
         * a resource.
         * <p>
         * This works the same as {@link #addPrincipal(IamPrincipal)}, except you do not need to specify {@code IamPrincipal
         * .create()}.
         * <p>
         * {@snippet :
         * IamStatement statement =
         *     IamStatement.builder()
         *                 .effect(IamEffect.DENY)
         *                 .addPrincipal(IamPrincipalType.AWS, "*")
         *                 .build();
         * }
         * @see
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_principal.html">Usage Guide</a>
         */
        Builder addPrincipal(IamPrincipalType iamPrincipalType, String principal);

        /**
         * Append a
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_principal.html">{@code
         * Principal}</a> to this statement, specifying a principal that is allowed or denied access to
         * a resource.
         * <p>
         * This works the same as {@link #addPrincipal(IamPrincipalType, String)}, except you do not need to specify {@code
         * IamPrincipalType.create()}.
         * <p>
         * {@snippet :
         * IamStatement statement =
         *     IamStatement.builder()
         *                 .effect(IamEffect.DENY)
         *                 .addPrincipal("AWS", "*")
         *                 .build();
         * }
         * @see
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_principal.html">Usage Guide</a>
         */
        Builder addPrincipal(String iamPrincipalType, String principal);

        /**
         * Append multiple
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_principal.html">{@code
         * Principal}s</a> to this statement, specifying principals that are allowed or denied access to
         * a resource.
         * <p>
         * This works the same as calling {@link #addPrincipal(IamPrincipalType, String)} multiple times with the same
         * {@link IamPrincipalType}.
         * <p>
         * {@snippet :
         * IamStatement statement =
         *     IamStatement.builder()
         *                 .effect(IamEffect.ALLOW)
         *                 .addPrincipals(IamPrincipalType.AWS, Arrays.asList("arn:aws:iam::123456789012:role/role-1",
         *                                                                    "arn:aws:iam::123456789012:role/role-2"))
         *                 .build();
         *
         * assert statement.principals().size() == 2;
         * }
         * @see
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_principal.html">Usage Guide</a>
         */
        Builder addPrincipals(IamPrincipalType iamPrincipalType, Collection<String> principals);

        /**
         * Append multiple
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_principal.html">{@code
         * Principal}s</a> to this statement, specifying principals that are allowed or denied access to
         * a resource.
         * <p>
         * This works the same as calling {@link #addPrincipal(String, String)} multiple times with the same
         * {@link IamPrincipalType}.
         * <p>
         * {@snippet :
         * IamStatement statement =
         *     IamStatement.builder()
         *                 .effect(IamEffect.ALLOW)
         *                 .addPrincipals("AWS", Arrays.asList("arn:aws:iam::123456789012:role/role-1",
         *                                                     "arn:aws:iam::123456789012:role/role-2"))
         *                 .build();
         *
         * assert statement.principals().size() == 2;
         * }
         * @see
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_principal.html">Usage Guide</a>
         */
        Builder addPrincipals(String iamPrincipalType, Collection<String> principals);

        /**
         * Configure the
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_notprincipal.html">{@code
         * NotPrincipal}</a> element of the statement, denying only the principals that are specified.
         * <p>
         * Very few scenarios require the use of {@code NotPrincipal}. We recommend that you explore other authorization options
         * before you decide to use {@code NotPrincipal}.
         * <p>
         * This will replace any other not-principals already added to the statement.
         * <p>
         * {@snippet :
         * IamStatement statement =
         *     IamStatement.builder()
         *                 .effect(IamEffect.ALLOW)
         *                 .notPrincipals(Arrays.asList(IamPrincipal.ALL))
         *                 .build();
         * }
         * @see
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_notprincipal.html">Usage Guide</a>
         */
        Builder notPrincipals(Collection<IamPrincipal> notPrincipals);
        
        /**
         * Append a
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_notprincipal.html">{@code
         * NotPrincipal}</a> to this statement, denying access to the principal that is specified.
         * <p>
         * Very few scenarios require the use of {@code NotPrincipal}. We recommend that you explore other authorization options
         * before you decide to use {@code NotPrincipal}.
         * <p>
         * {@snippet :
         * IamStatement statement =
         *     IamStatement.builder()
         *                 .effect(IamEffect.ALLOW)
         *                 .addNotPrincipal(IamPrincipal.ALL)
         *                 .build();
         * }
         * @see
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_notprincipal.html">Usage Guide</a>
         */
        Builder addNotPrincipal(IamPrincipal notPrincipal);

        /**
         * Append a
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_notprincipal.html">{@code
         * NotPrincipal}</a> to this statement, denying access to the principal that is specified.
         * <p>
         * Very few scenarios require the use of {@code NotPrincipal}. We recommend that you explore other authorization options
         * before you decide to use {@code NotPrincipal}.
         * <p>
         * This works the same as {@link #addNotPrincipal(IamPrincipal)}, except you do not need to specify {@code IamPrincipal
         * .builder()} or {@code build()}.
         * <p>
         * {@snippet :
         * IamStatement statement =
         *     IamStatement.builder()
         *                 .effect(IamEffect.ALLOW)
         *                 .addNotPrincipal(p -> p.type(IamPrincipalType.AWS).id("*"))
         *                 .build();
         * }
         * @see
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_notprincipal.html">Usage Guide</a>
         */
        Builder addNotPrincipal(Consumer<IamPrincipal.Builder> notPrincipal);

        /**
         * Append a
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_notprincipal.html">{@code
         * NotPrincipal}</a> to this statement, denying access to the principal that is specified.
         * <p>
         * Very few scenarios require the use of {@code NotPrincipal}. We recommend that you explore other authorization options
         * before you decide to use {@code NotPrincipal}.
         * <p>
         * This works the same as {@link #addNotPrincipal(IamPrincipal)}, except you do not need to specify {@code IamPrincipal
         * .create()}.
         * <p>
         * {@snippet :
         * IamStatement statement =
         *     IamStatement.builder()
         *                 .effect(IamEffect.ALLOW)
         *                 .addNotPrincipal(IamPrincipalType.AWS, "*")
         *                 .build();
         * }
         * @see
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_notprincipal.html">Usage Guide</a>
         */
        Builder addNotPrincipal(IamPrincipalType IamPrincipalType, String notPrincipal);

        /**
         * Append a
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_notprincipal.html">{@code
         * NotPrincipal}</a> to this statement, denying access to the principal that is specified.
         * <p>
         * Very few scenarios require the use of {@code NotPrincipal}. We recommend that you explore other authorization options
         * before you decide to use {@code NotPrincipal}.
         * <p>
         * This works the same as {@link #addNotPrincipal(IamPrincipalType, String)}, except you do not need to specify {@code
         * IamPrincipalType.create()}.
         * <p>
         * {@snippet :
         * IamStatement statement =
         *     IamStatement.builder()
         *                 .effect(IamEffect.ALLOW)
         *                 .addNotPrincipal("AWS", "*")
         *                 .build();
         * }
         * @see
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_notprincipal.html">Usage Guide</a>
         */
        Builder addNotPrincipal(String IamPrincipalType, String notPrincipal);

        /**
         * Append multiple 
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_notprincipal.html">{@code
         * NotPrincipal}s</a> to this statement, denying access to the principal that is specified.
         * <p>
         * Very few scenarios require the use of {@code NotPrincipal}. We recommend that you explore other authorization options
         * before you decide to use {@code NotPrincipal}.
         * <p>
         * This works the same as calling {@link #addNotPrincipal(IamPrincipalType, String)} multiple times with the same
         * {@link IamPrincipalType}.
         * <p>
         * {@snippet :
         * IamStatement statement =
         *     IamStatement.builder()
         *                 .effect(IamEffect.ALLOW)
         *                 .addNotPrincipals(IamPrincipalType.AWS, Arrays.asList("arn:aws:iam::123456789012:role/role-1",
         *                                                                       "arn:aws:iam::123456789012:role/role-2"))
         *                 .build();
         *
         * assert statement.notPrincipals().size() == 2;
         * }
         * @see
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_notprincipal.html">Usage Guide</a>
         */
        Builder addNotPrincipals(IamPrincipalType IamPrincipalType, Collection<String> notPrincipals);

        /**
         * Append multiple 
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_notprincipal.html">{@code
         * NotPrincipal}s</a> to this statement, denying access to the principal that is specified.
         * <p>
         * Very few scenarios require the use of {@code NotPrincipal}. We recommend that you explore other authorization options
         * before you decide to use {@code NotPrincipal}.
         * <p>
         * This works the same as calling {@link #addNotPrincipal(String, String)} multiple times with the same
         * {@link IamPrincipalType}.
         * <p>
         * {@snippet :
         * IamStatement statement =
         *     IamStatement.builder()
         *                 .effect(IamEffect.ALLOW)
         *                 .addNotPrincipals("AWS", Arrays.asList("arn:aws:iam::123456789012:role/role-1",
         *                                                        "arn:aws:iam::123456789012:role/role-2"))
         *                 .build();
         *
         * assert statement.notPrincipals().size() == 2;
         * }
         * @see
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_notprincipal.html">Usage Guide</a>
         */
        Builder addNotPrincipals(String IamPrincipalType, Collection<String> notPrincipals);

        /**
         * Configure the
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_action.html">{@code Action}</a>
         * element of the statement, specifying the actions that are allowed or denied.
         * <p>
         * This will replace any other actions already added to the statement.
         * <p>
         * {@snippet :
         * IamStatement statement =
         *     IamStatement.builder()
         *                 .effect(IamEffect.DENY)
         *                 .actions(Arrays.asList(IamAction.ALL))
         *                 .build();
         * }
         * @see
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_action.html">Usage Guide</a>
         */
        Builder actions(Collection<IamAction> actions);

        /**
         * Configure the
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_action.html">{@code Action}</a>
         * element of the statement, specifying the actions that are allowed or denied.
         * <p>
         * This works the same as {@link #actions(Collection)}, except you do not need to call {@code IamAction.create()
         * } on each action. This will replace any other actions already added to the statement.
         * <p>
         * {@snippet :
         * IamStatement statement =
         *     IamStatement.builder()
         *                 .effect(IamEffect.DENY)
         *                 .actionStrings(Arrays.asList("*"))
         *                 .build();
         * }
         * @see
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_action.html">Usage Guide</a>
         */
        Builder actionStrings(Collection<String> actions);

        /**
         * Append an <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_action.html">{@code
         * Action}</a> element to this statement, specifying an action that is allowed or denied.
         * <p>
         * {@snippet :
         * IamStatement statement =
         *     IamStatement.builder()
         *                 .effect(IamEffect.DENY)
         *                 .addAction(IamAction.ALL)
         *                 .build();
         * }
         * @see
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_action.html">Usage Guide</a>
         */
        Builder addAction(IamAction action);

        /**
         * Append an <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_action.html">{@code
         * Action}</a> element to this statement, specifying an action that is allowed or denied.
         * <p>
         * This works the same as {@link #addAction(IamAction)}, except you do not need to call {@code IamAction.create()}.
         * <p>
         * {@snippet :
         * IamStatement statement =
         *     IamStatement.builder()
         *                 .effect(IamEffect.DENY)
         *                 .addAction("*")
         *                 .build();
         * }
         * @see
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_action.html">Usage Guide</a>
         */
        Builder addAction(String action);

        /**
         * Configure the
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_notaction.html">{@code
         * NotAction}</a> element of the statement, specifying actions that are denied or allowed.
         * <p>
         * This will replace any other not-actions already added to the statement.
         * <p>
         * {@snippet :
         * IamStatement statement =
         *     IamStatement.builder()
         *                 .effect(IamEffect.ALLOW)
         *                 .notActions(Arrays.asList(IamAction.ALL))
         *                 .build();
         * }
         * @see
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_notaction.html">Usage Guide</a>
         */
        Builder notActions(Collection<IamAction> actions);

        /**
         * Configure the
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_notaction.html">{@code
         * NotAction}</a> element of the statement, specifying actions that are denied or allowed.
         * <p>
         * This works the same as {@link #notActions(Collection)}, except you do not need to call {@code IamAction.create()}
         * on each action. This will replace any other not-actions already added to the statement.
         * <p>
         * {@snippet :
         * IamStatement statement =
         *     IamStatement.builder()
         *                 .effect(IamEffect.ALLOW)
         *                 .notActionStrings("*")
         *                 .build();
         * }
         * @see
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_notaction.html">Usage Guide</a>
         */
        Builder notActionStrings(Collection<String> actions);

        /**
         * Append a
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_notaction.html">{@code
         * NotAction}</a> element to this statement, specifying an action that is denied or allowed.
         * <p>
         * {@snippet :
         * IamStatement statement =
         *     IamStatement.builder()
         *                 .effect(IamEffect.ALLOW)
         *                 .addNotAction(IamAction.ALL)
         *                 .build();
         * }
         * @see
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_notaction.html">Usage Guide</a>
         */
        Builder addNotAction(IamAction action);

        /**
         * Append a
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_notaction.html">{@code
         * NotAction}</a> element to this statement, specifying an action that is denied or allowed.
         * <p>
         * This works the same as {@link #addNotAction(IamAction)}, except you do not need to call {@code IamAction.create()}.
         * <p>
         * {@snippet :
         * IamStatement statement =
         *     IamStatement.builder()
         *                 .effect(IamEffect.ALLOW)
         *                 .addNotAction("*")
         *                 .build();
         * }
         * @see
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_notaction.html">Usage Guide</a>
         */
        Builder addNotAction(String action);

        /**
         * Configure the
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_resource.html">{@code Resource}
         * </a> element of the statement, specifying the resource(s) that the statement covers.
         * <p>
         * This will replace any other resources already added to the statement.
         * <p>
         * {@snippet :
         * IamStatement statement =
         *     IamStatement.builder()
         *                 .effect(IamEffect.DENY)
         *                 .resources(Arrays.asList(IamResource.ALL))
         *                 .build();
         * }
         * @see
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_resource.html">Usage Guide</a>
         */
        Builder resources(Collection<IamResource> resources);

        /**
         * Configure the
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_resource.html">{@code Resource}
         * </a> element of the statement, specifying the resource(s) that the statement covers.
         * <p>
         * This works the same as {@link #resources(Collection)}, except you do not need to call {@code IamResource.create()}
         * on each resource. This will replace any other resources already added to the statement.
         * <p>
         * {@snippet :
         * IamStatement statement =
         *     IamStatement.builder()
         *                 .effect(IamEffect.DENY)
         *                 .resourceStrings(Arrays.asList("*"))
         *                 .build();
         * }
         * @see
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_resource.html">Usage Guide</a>
         */
        Builder resourceStrings(Collection<String> resources);

        /**
         * Append a
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_resource.html">{@code Resource}
         * </a> element to the statement, specifying a resource that the statement covers.
         * <p>
         * {@snippet :
         * IamStatement statement =
         *     IamStatement.builder()
         *                 .effect(IamEffect.DENY)
         *                 .addResource(IamResource.ALL)
         *                 .build();
         * }
         * @see
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_resource.html">Usage Guide</a>
         */
        Builder addResource(IamResource resource);

        /**
         * Append a
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_resource.html">{@code Resource}
         * </a> element to the statement, specifying a resource that the statement covers.
         * <p>
         * This works the same as {@link #addResource(IamResource)}, except you do not need to call {@code IamResource.create()}.
         * <p>
         * {@snippet :
         * IamStatement statement =
         *     IamStatement.builder()
         *                 .effect(IamEffect.DENY)
         *                 .addResource("*")
         *                 .build();
         * }
         * @see
         * <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_elements_resource.html">Usage Guide</a>
         */
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
