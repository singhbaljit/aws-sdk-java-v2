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

import static software.amazon.awssdk.policybuilder.iam.IamPrincipalType.AWS;

import java.util.List;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public interface IamPrincipal extends JsonConvertible, ToCopyableBuilder<IamPrincipal.Builder, IamPrincipal> {
    IamPrincipal ALL = create(AWS, "*");

    static IamPrincipal create(IamPrincipalType principalType,
                               String... principalIds) {
        return builder().type(principalType)
                        .ids(principalIds)
                        .build();
    }

    static Builder builder() {
        return new DefaultIamPrincipal.Builder();
    }

    IamPrincipalType type();
    List<String> ids();
    boolean notPrincipal();

    interface Builder extends CopyableBuilder<Builder, IamPrincipal> {
        Builder type(IamPrincipalType type);
        Builder ids(List<String> ids);
        Builder ids(String... ids);
        Builder addId(String ids);

        Builder notPrincipal();

        IamPrincipal build();
    }
}
