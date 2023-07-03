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

import static java.util.Arrays.asList;

public class UserExperience {
    public static void main(String... args) {
        IamPolicy policy =
            IamPolicy.builder()
                     .addStatement(IamStatement.builder()
                                               .sid("sid")
                                               .effect(IamEffect.ALLOW)
                                               .addPrincipal(IamPrincipal.ALL)
                                               .addAction(IamAction.ALL)
                                               .addResource(IamResource.ALL)
                                               .addCondition("StringEquals", "aws:PrincipalTag/job-category", "iamuser-admin")
                                               .addConditions("StringEquals", "aws:PrincipalTag/role", asList("audit", "finance"))
                                               .build())
                     .addStatement(s -> s.effect(IamEffect.DENY)
                                         .addPrincipal(IamPrincipal.ALL))
                     .build();

        System.out.println(policy);
        System.out.println(policy.toJson());
        System.out.println(IamPolicyWriter.builder().prettyPrint(true).build().write(policy));
    }
}
