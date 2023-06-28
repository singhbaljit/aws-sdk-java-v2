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
import software.amazon.awssdk.policybuilder.iam.internal.DefaultStandardIamConditionOperator;
import software.amazon.awssdk.policybuilder.iam.operator.NullIamConditionOperator;
import software.amazon.awssdk.policybuilder.iam.operator.StandardIamConditionOperator;

public interface IamConditionOperator {
    StandardIamConditionOperator STRING_EQUALS =
        new DefaultStandardIamConditionOperator("StringEquals");
    StandardIamConditionOperator STRING_NOT_EQUALS =
        new DefaultStandardIamConditionOperator("StringNotEquals");
    StandardIamConditionOperator STRING_EQUALS_IGNORE_CASE =
        new DefaultStandardIamConditionOperator("StringEqualsIgnoreCase");
    StandardIamConditionOperator STRING_NOT_EQUALS_IGNORE_CASE =
        new DefaultStandardIamConditionOperator("StringNotEqualsIgnoreCase");
    StandardIamConditionOperator STRING_LIKE =
        new DefaultStandardIamConditionOperator("StringLike");
    StandardIamConditionOperator STRING_NOT_LIKE =
        new DefaultStandardIamConditionOperator("StringNotLike");

    StandardIamConditionOperator NUMERIC_EQUALS =
        new DefaultStandardIamConditionOperator("NumericEquals");
    StandardIamConditionOperator NUMERIC_NOT_EQUALS =
        new DefaultStandardIamConditionOperator("NumericNotEquals");
    StandardIamConditionOperator NUMERIC_LESS_THAN =
        new DefaultStandardIamConditionOperator("NumericLessThan");
    StandardIamConditionOperator NUMERIC_LESS_THAN_EQUALS =
        new DefaultStandardIamConditionOperator("NumericLessThanEquals");
    StandardIamConditionOperator NUMERIC_GREATER_THAN =
        new DefaultStandardIamConditionOperator("NumericGreaterThan");
    StandardIamConditionOperator NUMERIC_GREATER_THAN_EQUALS =
        new DefaultStandardIamConditionOperator("NumericGreaterThanEquals");

    StandardIamConditionOperator DATE_EQUALS =
        new DefaultStandardIamConditionOperator("DateEquals");
    StandardIamConditionOperator DATE_NOT_EQUALS =
        new DefaultStandardIamConditionOperator("DateNotEquals");
    StandardIamConditionOperator DATE_LESS_THAN =
        new DefaultStandardIamConditionOperator("DateLessThan");
    StandardIamConditionOperator DATE_LESS_THAN_EQUALS =
        new DefaultStandardIamConditionOperator("DateLessThanEquals");
    StandardIamConditionOperator DATE_GREATER_THAN =
        new DefaultStandardIamConditionOperator("DateGreaterThan");
    StandardIamConditionOperator DATE_GREATER_THAN_EQUALS =
        new DefaultStandardIamConditionOperator("DateGreaterThanEquals");

    StandardIamConditionOperator BOOL =
        new DefaultStandardIamConditionOperator("Bool");

    StandardIamConditionOperator BINARY_EQUALS =
        new DefaultStandardIamConditionOperator("BinaryEquals");

    StandardIamConditionOperator IP_ADDRESS =
        new DefaultStandardIamConditionOperator("IpAddress");
    StandardIamConditionOperator NOT_IP_ADDRESS =
        new DefaultStandardIamConditionOperator("NotIpAddress");

    StandardIamConditionOperator ARN_EQUALS =
        new DefaultStandardIamConditionOperator("ArnEquals");
    StandardIamConditionOperator ARN_NOT_EQUALS =
        new DefaultStandardIamConditionOperator("ArnNotEquals");
    StandardIamConditionOperator ARN_LIKE =
        new DefaultStandardIamConditionOperator("ArnLike");
    StandardIamConditionOperator ARN_NOT_LIKE =
        new DefaultStandardIamConditionOperator("ArnNotLike");

    NullIamConditionOperator NULL = new DefaultNullIamConditionOperator();

    String value();

    static IamConditionOperator create(String value) {
        return new DefaultIamConditionOperator(value);
    }
}