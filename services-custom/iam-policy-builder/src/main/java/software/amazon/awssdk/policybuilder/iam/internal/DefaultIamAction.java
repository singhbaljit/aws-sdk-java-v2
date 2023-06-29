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

public class DefaultIamAction implements IamAction {
    private final String value;
    private final boolean notAction;

    public DefaultIamAction(Builder builder) {
        this.value = builder.value;
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
