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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.policybuilder.iam.IamCondition;
import software.amazon.awssdk.policybuilder.iam.IamConditionKey;
import software.amazon.awssdk.policybuilder.iam.IamConditionOperator;

public class DefaultIamCondition implements IamCondition {
    private final IamConditionOperator operator;
    private final IamConditionKey key;
    private final List<String> values;

    private DefaultIamCondition(Builder builder) {
        this.operator = builder.operator;
        this.key = builder.key;
        this.values = new ArrayList<>(builder.values);
    }

    @Override
    public IamConditionOperator operator() {
        return operator;
    }

    @Override
    public IamConditionKey key() {
        return key;
    }

    @Override
    public List<String> values() {
        return Collections.unmodifiableList(values);
    }

    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    public static class Builder implements IamCondition.Builder {
        private IamConditionOperator operator;
        private IamConditionKey key;
        private final List<String> values = new ArrayList<>();
        
        public Builder() {}

        public Builder(DefaultIamCondition condition) {
            this.operator = condition.operator;
            this.key = condition.key;
            this.values.addAll(condition.values);
        }
        
        @Override
        public IamCondition.Builder operator(IamConditionOperator operator) {
            this.operator = operator;
            return this;
        }

        @Override
        public IamCondition.Builder operator(String operator) {
            this.operator = IamConditionOperator.create(operator);
            return this;
        }

        @Override
        public IamCondition.Builder key(IamConditionKey key) {
            this.key = key;
            return this;
        }

        @Override
        public IamCondition.Builder key(String key) {
            this.key = IamConditionKey.create(key);
            return this;
        }

        @Override
        public IamCondition.Builder values(Collection<String> values) {
            this.values.clear();
            this.values.addAll(values);
            return this;
        }

        @Override
        public IamCondition.Builder values(String... values) {
            this.values.clear();
            this.values.addAll(Arrays.asList(values));
            return this;
        }

        @Override
        public IamCondition.Builder addValue(String value) {
            this.values.add(value);
            return this;
        }

        @Override
        public IamCondition build() {
            return new DefaultIamCondition(this);
        }
    }
}
