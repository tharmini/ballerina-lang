/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.messaging.rabbitmq.nativeimpl.channel;

import com.rabbitmq.client.Channel;
import org.ballerinalang.jvm.scheduling.Strand;
import org.ballerinalang.jvm.values.ObjectValue;
import org.ballerinalang.messaging.rabbitmq.RabbitMQConstants;
import org.ballerinalang.messaging.rabbitmq.RabbitMQTransactionContext;
import org.ballerinalang.messaging.rabbitmq.RabbitMQUtils;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;

import java.io.IOException;

/**
 * Binds a queue to an exchange.
 *
 * @since 0.995.0
 */
@BallerinaFunction(
        orgName = RabbitMQConstants.ORG_NAME,
        packageName = RabbitMQConstants.RABBITMQ,
        functionName = "queueBind",
        receiver = @Receiver(type = TypeKind.OBJECT,
                structType = RabbitMQConstants.CHANNEL_OBJECT,
                structPackage = RabbitMQConstants.PACKAGE_RABBITMQ),
        isPublic = true
)
public class QueueBind {

    public static Object queueBind(Strand strand, ObjectValue channelObjectValue, String queueName,
                                   String exchangeName, String bindingKey) {
        boolean isInTransaction = strand.isInTransaction();
        Channel channel = (Channel) channelObjectValue.getNativeData(RabbitMQConstants.CHANNEL_NATIVE_OBJECT);
        RabbitMQTransactionContext transactionContext = (RabbitMQTransactionContext) channelObjectValue.
                getNativeData(RabbitMQConstants.RABBITMQ_TRANSACTION_CONTEXT);
        try {
            channel.queueBind(queueName, exchangeName, bindingKey, null);
            if (isInTransaction) {
                transactionContext.handleTransactionBlock(strand);
            }
        } catch (IOException exception) {
            return RabbitMQUtils.returnErrorValue(RabbitMQConstants.RABBITMQ_CLIENT_ERROR
                    + "I/O exception while binding the queue; " + exception.getMessage());
        }
        return null;
    }

    private QueueBind() {
    }
}
