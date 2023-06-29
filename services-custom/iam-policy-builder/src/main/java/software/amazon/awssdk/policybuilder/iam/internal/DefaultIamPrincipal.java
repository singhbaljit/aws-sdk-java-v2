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
import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.policybuilder.iam.IamPrincipal;
import software.amazon.awssdk.policybuilder.iam.IamPrincipalType;
import software.amazon.awssdk.utils.Validate;

public class DefaultIamPrincipal implements IamPrincipal {
    private final IamPrincipalType type;
    private final List<String> ids;
    private final boolean notPrincipal;

    private DefaultIamPrincipal(Builder builder) {
        this.type = Validate.paramNotNull(builder.type, "type");
        this.ids = new ArrayList<>(Validate.notEmpty(builder.ids, "Principal IDs must not be empty"));
        this.notPrincipal = builder.notPrincipal;
    }

    @Override
    public IamPrincipalType type() {
        return type;
    }

    @Override
    public List<String> ids() {
        return Collections.unmodifiableList(ids);
    }

    @Override
    public boolean notPrincipal() {
        return notPrincipal;
    }

    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    public static class Builder implements IamPrincipal.Builder {
        private IamPrincipalType type;
        private List<String> ids = new ArrayList<>();
        private boolean notPrincipal = false;

        public Builder() {}

        public Builder(DefaultIamPrincipal principal) {
            this.type = principal.type;
            this.ids.addAll(principal.ids);
            this.notPrincipal = principal.notPrincipal;
        }

        @Override
        public IamPrincipal.Builder type(IamPrincipalType type) {
            this.type = type;
            return this;
        }

        @Override
        public IamPrincipal.Builder ids(List<String> ids) {
            this.ids.clear();
            this.ids.addAll(ids);
            return this;
        }

        @Override
        public IamPrincipal.Builder ids(String... ids) {
            return ids(Arrays.asList(ids));
        }

        @Override
        public IamPrincipal.Builder addId(String ids) {
            this.ids.add(ids);
            return this;
        }

        @Override
        public IamPrincipal.Builder notPrincipal() {
            this.notPrincipal = true;
            return this;
        }

        @Override
        public IamPrincipal build() {
            return new DefaultIamPrincipal(this);
        }
    }
}
