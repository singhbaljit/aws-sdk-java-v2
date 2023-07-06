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
        IamPolicy policy;


        // Simple builder with strings
        policy = IamPolicy.builder()
                          .addStatement(s -> s.effect("Allow")
                                              .addPrincipal("AWS", "arn:aws:iam::727820809195:root")
                                              .addAction("sts:AssumeRole")
                                              .addCondition("StringEquals", "sts:ExternalId", "IsengardExternalIdiFb2acZAYZLz"))
                          .build();







        // Can write to string
        System.out.println(policy.toJson());






        // Pretty formatting of the JSON is possible
        IamPolicyWriter writer = IamPolicyWriter.builder()
                                                .prettyPrint(true)
                                                .build();
        System.out.println(writer.writeToString(policy));






        // Can read from string
        policy = IamPolicy.fromJson("{\n"
                                    + "  \"Version\" : \"2012-10-17\",\n"
                                    + "  \"Statement\" : {\n"
                                    + "    \"Effect\" : \"Allow\",\n"
                                    + "    \"Principal\" : {\n"
                                    + "      \"AWS\" : \"arn:aws:iam::727820809195:root\"\n"
                                    + "    },\n"
                                    + "    \"Action\" : \"sts:AssumeRole\",\n"
                                    + "    \"Resource\" : \"sts:AssumeRole\",\n"
                                    + "    \"Condition\" : {\n"
                                    + "      \"StringEquals\" : {\n"
                                    + "        \"sts:ExternalId\" : \"IsengardExternalIdiFb2acZAYZLz\"\n"
                                    + "      }\n"
                                    + "    }\n"
                                    + "  }\n"
                                    + "}");






        // "Enums" are available for common fields
        policy = IamPolicy.builder()
                          .addStatement(s -> s.effect(IamEffect.ALLOW)
                                              .addPrincipal(IamPrincipalType.AWS, "arn:aws:iam::727820809195:root")
                                              .addAction(IamAction.ALL)
                                              .addCondition(IamConditionOperator.STRING_EQUALS,
                                                            "sts:ExternalId",
                                                            "IsengardExternalIdiFb2acZAYZLz"))
                          .build();





        // All statement fields are supported
        policy = IamPolicy.builder()
                          .id("PolicyId")
                          .version("2012-10-17")
                          .addStatement(s -> s.effect(IamEffect.DENY)
                                              .sid("StatementId")
                                              .addPrincipal(IamPrincipal.ALL)
                                              .addNotPrincipal(IamPrincipal.ALL)
                                              .addAction(IamAction.ALL)
                                              .addNotAction(IamAction.ALL)
                                              .addResource(IamResource.ALL)
                                              .addNotResource(IamResource.ALL)
                                              .addCondition(IamConditionOperator.ARN_LIKE, "s3:Bucket", "*"))
                          .build();






        // Multiple ways to specify each field, the following principal settings are equivalent:

        policy = IamPolicy.builder()
                          .addStatement(s -> s.effect(IamEffect.DENY)
                                              .addPrincipal(IamPrincipal.create("AWS", "*"))
                                              .addPrincipal(IamPrincipal.ALL)
                                              .addPrincipal(IamPrincipalType.AWS, "*")
                                              .addPrincipal("AWS", "*"))
                          .build();





        // Differences from V1:

        // Difference 1: Account IDs do NOT have dashes stripped.  Customers have to do their own stripping (this will be in the
        // migration guide).
        IamPrincipal.create("AWS", "1234-5678-9101"); // Stripped to 123456789101 in 1.x, not in 2.x.










        // Difference 2: All "list" values are flattened into multiple values at setting time. In 1.x, this was done for
        // principals, but not conditions.
        IamStatement statement =
            IamStatement.builder()
                        .addConditions("StringEquals", "sts:ExternalId", asList("foo", "bar"))
                        .build();

        statement.conditions(); // In 2.x, this is two conditions: one for "foo" and one for "bar". In 1.x it would be just
        // one, but would be automatically merged with other "StringEquals" and "sts:ExternalId" conditions at marshalling.







        // Other minor differences:
        // 1. Iam prefix for types, IamPolicy instead of just Policy
        // 2. Builders instead of withers
        // 3. Policies are immutable
        // 4. "Not" resources are done with a separate setter on statement instead of a boolean flag on the resource
        // 5. No code-generated service-specific actions (they weren't always accurate anyway), but the door is open to the
        // possibility if we ever get a good data source for them.
        // 6. "ALL" IAM principal is modeled as IamPrincipal("AWS", "*") instead of Principal("*", "*").
        // 7. No Jackson-databind dependency (obviously)




        // Extra features:
        // 1. Pretty formatting of written JSON is possible
        // 2. Support for NotPrincipals and NotActions
        // 3. Enums for global condition operators
        // 4. Easier to specify actions and other properties using strings (previously required custom classes)









        // Remaining work:
        // Javadoc!
        // Tests!
    }
}
