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

import software.amazon.awssdk.policybuilder.iam.internal.DefaultIamPrincipal;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public interface IamPrincipal extends ToCopyableBuilder<IamPrincipal.Builder, IamPrincipal> {
    IamPrincipal ALL = create("*", "*");

    static IamPrincipal create(IamPrincipalType principalType, String principalId) {
        return builder().type(principalType).id(principalId).build();
    }

    static IamPrincipal create(String principalType, String principalId) {
        return builder().type(principalType).id(principalId).build();
    }

    static Builder builder() {
        return new DefaultIamPrincipal.Builder();
    }

    IamPrincipalType type();
    String id();

    interface Builder extends CopyableBuilder<Builder, IamPrincipal> {
        Builder type(IamPrincipalType type);
        Builder type(String type);
        Builder id(String id);
    }
}
