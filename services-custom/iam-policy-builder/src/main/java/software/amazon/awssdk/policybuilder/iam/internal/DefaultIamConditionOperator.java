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

package software.amazon.awssdk.policybuilder.iam.internal;

import software.amazon.awssdk.policybuilder.iam.IamConditionOperator;
import software.amazon.awssdk.policybuilder.iam.operator.IfExistsIamConditionOperator;
import software.amazon.awssdk.policybuilder.iam.operator.NullIamConditionOperator;
import software.amazon.awssdk.policybuilder.iam.operator.SetIamConditionOperator;
import software.amazon.awssdk.policybuilder.iam.operator.SetIfExistsIamConditionOperator;
import software.amazon.awssdk.policybuilder.iam.operator.StandardIamConditionOperator;

public class DefaultIamConditionOperator implements IamConditionOperator,
                                                    IfExistsIamConditionOperator,
                                                    NullIamConditionOperator,
                                                    SetIamConditionOperator,
                                                    SetIfExistsIamConditionOperator,
                                                    StandardIamConditionOperator {
    private final String value;

    public DefaultIamConditionOperator(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public SetIfExistsIamConditionOperator forAllValues() {
        return null;
    }

    @Override
    public SetIfExistsIamConditionOperator forAnyValues() {
        return null;
    }

    @Override
    public SetIfExistsIamConditionOperator ifExists() {
        return null;
    }
}
