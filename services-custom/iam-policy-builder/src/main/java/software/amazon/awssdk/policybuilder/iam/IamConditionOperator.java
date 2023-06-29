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

import software.amazon.awssdk.policybuilder.iam.internal.DefaultIamConditionOperator;

public interface IamConditionOperator extends IamValue {
    IamConditionOperator STRING_EQUALS = create("StringEquals");
    IamConditionOperator STRING_NOT_EQUALS = create("StringNotEquals");
    IamConditionOperator STRING_EQUALS_IGNORE_CASE = create("StringEqualsIgnoreCase");
    IamConditionOperator STRING_NOT_EQUALS_IGNORE_CASE = create("StringNotEqualsIgnoreCase");
    IamConditionOperator STRING_LIKE = create("StringLike");
    IamConditionOperator STRING_NOT_LIKE = create("StringNotLike");

    IamConditionOperator NUMERIC_EQUALS = create("NumericEquals");
    IamConditionOperator NUMERIC_NOT_EQUALS = create("NumericNotEquals");
    IamConditionOperator NUMERIC_LESS_THAN = create("NumericLessThan");
    IamConditionOperator NUMERIC_LESS_THAN_EQUALS = create("NumericLessThanEquals");
    IamConditionOperator NUMERIC_GREATER_THAN = create("NumericGreaterThan");
    IamConditionOperator NUMERIC_GREATER_THAN_EQUALS = create("NumericGreaterThanEquals");

    IamConditionOperator DATE_EQUALS = create("DateEquals");
    IamConditionOperator DATE_NOT_EQUALS = create("DateNotEquals");
    IamConditionOperator DATE_LESS_THAN = create("DateLessThan");
    IamConditionOperator DATE_LESS_THAN_EQUALS = create("DateLessThanEquals");
    IamConditionOperator DATE_GREATER_THAN = create("DateGreaterThan");
    IamConditionOperator DATE_GREATER_THAN_EQUALS = create("DateGreaterThanEquals");

    IamConditionOperator BOOL = create("Bool");

    IamConditionOperator BINARY_EQUALS = create("BinaryEquals");

    IamConditionOperator IP_ADDRESS = create("IpAddress");
    IamConditionOperator NOT_IP_ADDRESS = create("NotIpAddress");

    IamConditionOperator ARN_EQUALS = create("ArnEquals");
    IamConditionOperator ARN_NOT_EQUALS = create("ArnNotEquals");
    IamConditionOperator ARN_LIKE = create("ArnLike");
    IamConditionOperator ARN_NOT_LIKE = create("ArnNotLike");

    IamConditionOperator NULL = create("Null");

    IamConditionOperator addPrefix(String prefix);
    IamConditionOperator addSuffix(String suffix);

    String value();

    static IamConditionOperator create(String value) {
        return new DefaultIamConditionOperator(value);
    }
}