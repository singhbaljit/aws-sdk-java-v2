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

import software.amazon.awssdk.policybuilder.iam.IamResource;

public class DefaultIamResource implements IamResource {
    private final String value;
    private final boolean notResource;

    public DefaultIamResource(Builder builder) {
        this.value = builder.value;
        this.notResource = builder.notResource;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public boolean notResource() {
        return notResource;
    }

    @Override
    public IamResource.Builder toBuilder() {
        return new Builder(this);
    }

    public static class Builder implements IamResource.Builder {
        private String value;
        private boolean notResource = false;

        public Builder() {
        }

        public Builder(DefaultIamResource resource) {
            this.value = resource.value;
            this.notResource = resource.notResource;
        }

        @Override
        public IamResource.Builder value(String value) {
            this.value = value;
            return this;
        }

        @Override
        public IamResource.Builder notResource() {
            this.notResource = true;
            return this;
        }

        @Override
        public IamResource build() {
            return new DefaultIamResource(this);
        }
    }
}
