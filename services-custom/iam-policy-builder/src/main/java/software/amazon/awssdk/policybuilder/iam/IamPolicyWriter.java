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

import software.amazon.awssdk.policybuilder.iam.internal.DefaultIamPolicyWriter;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public interface IamPolicyWriter extends ToCopyableBuilder<IamPolicyWriter.Builder, IamPolicyWriter> {
    static IamPolicyWriter create() {
        return DefaultIamPolicyWriter.create();
    }

    static Builder builder() {
        return new DefaultIamPolicyWriter.Builder();
    }

    String write(IamPolicy policy);

    interface Builder extends CopyableBuilder<Builder, IamPolicyWriter> {
        Builder prettyPrint(Boolean prettyPrint);
    }
}
