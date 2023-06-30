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

import software.amazon.awssdk.policybuilder.iam.IamAction;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;

public class DefaultIamAction implements IamAction {
    private final String value;
    private final boolean notAction;

    public DefaultIamAction(Builder builder) {
        this.value = Validate.paramNotNull(builder.value, "actionValue");
        this.notAction = builder.notAction;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public boolean notAction() {
        return notAction;
    }

    @Override
    public IamAction.Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DefaultIamAction that = (DefaultIamAction) o;

        if (notAction != that.notAction) {
            return false;
        }
        if (!value.equals(that.value)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + (notAction ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return ToString.builder("IamAction")
                       .add("value", value)
                       .add("notAction", notAction)
                       .build();
    }

    public static class Builder implements IamAction.Builder {
        private String value;
        private boolean notAction = false;

        public Builder() {
        }

        public Builder(DefaultIamAction action) {
            this.value = action.value;
            this.notAction = action.notAction;
        }

        @Override
        public IamAction.Builder value(String value) {
            this.value = value;
            return this;
        }

        @Override
        public IamAction.Builder notAction() {
            this.notAction = true;
            return this;
        }

        @Override
        public IamAction build() {
            return new DefaultIamAction(this);
        }
    }
}
