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

public class DefaultIamConditionOperator implements IamConditionOperator {
    private final String value;

    public DefaultIamConditionOperator(String value) {
        this.value = value;
    }

    @Override
    public IamConditionOperator addPrefix(String prefix) {
        return IamConditionOperator.create(prefix + value);
    }

    @Override
    public IamConditionOperator addSuffix(String suffix) {
        return IamConditionOperator.create(value + suffix);
    }

    @Override
    public String value() {
        return value;
    }
}
