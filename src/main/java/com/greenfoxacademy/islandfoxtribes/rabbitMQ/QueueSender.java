package com.greenfoxacademy.islandfoxtribes.rabbitMQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

// This class sends the message to the receiver.
public class QueueSender {

    private final String qName;

    private final ConnectionFactory factory = new ConnectionFactory();
    private final Connection connection = factory.newConnection(CommonConfigs.AMQP_URL);
    private final Channel channel = connection.createChannel();

    public QueueSender(String qName) throws IOException, TimeoutException {
        this.qName = qName;
    }

    public void sendMessage(String message) throws IOException, TimeoutException {
        channel.queueDeclare(qName, true, false, true, null);
        channel.basicPublish("", qName, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
    }

}
