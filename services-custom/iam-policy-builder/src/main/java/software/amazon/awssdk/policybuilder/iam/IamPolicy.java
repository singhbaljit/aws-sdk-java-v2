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
import software.amazon.awssdk.policybuilder.iam.internal.DefaultIamPolicy;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public interface IamPolicy extends ToCopyableBuilder<IamPolicy.Builder, IamPolicy>, IamStructure {
    static IamPolicy fromJson(String json) {
        // TODO
        return null;
    }

    static IamPolicy fromStatements(Collection<IamStatement> statements) {
        return builder().statements(statements).build();
    }

    static IamPolicy fromStatements(IamStatement... statements) {
        return builder().statements(statements).build();
    }

    static Builder builder() {
        return new DefaultIamPolicy.Builder();
    }

    String id();
    String version();
    List<IamStatement> statements();
    Map<String, String> additionalJsonFieldsUnsafe();

    String toJson();
    String toJson(IamPolicyWriter writer);

    interface Builder extends CopyableBuilder<Builder, IamPolicy>, IamStructure.Builder<Builder> {
        Builder id(String id);
        Builder version(String version);

        Builder statements(Collection<IamStatement> statements);
        Builder statements(IamStatement... statements);
        Builder addStatement(IamStatement statement);
        Builder addStatement(Consumer<IamStatement.Builder> statement);
    }
}
