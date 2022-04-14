package com.greenfoxacademy.islandfoxtribes.rabbitMQ;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public abstract class QueueReceiver {

    private final ConnectionFactory factory = new ConnectionFactory();
    private final Connection connection = factory.newConnection(CommonConfigs.AMQP_URL);
    private final Channel channel = connection.createChannel();

    private final String qName;

    protected QueueReceiver(String qName) throws IOException, TimeoutException {
        this.qName = qName;
    }

    public void receiveMessage() throws IOException {
        channel.queueDeclare(qName, true, false, true, null);
        channel.basicQos(1);
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            try {
                doWork(message);
            } catch (InterruptedException | TimeoutException e) {
            }
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };
        CancelCallback cancelCallback = consumerTag -> {
        };
        channel.basicConsume(qName, false, deliverCallback, cancelCallback);
    }

    protected abstract void doWork(String message) throws InterruptedException, IOException, TimeoutException;
}

