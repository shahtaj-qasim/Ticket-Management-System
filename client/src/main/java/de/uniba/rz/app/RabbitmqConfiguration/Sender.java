package de.uniba.rz.app.RabbitmqConfiguration;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import de.uniba.rz.entities.Ticket;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static de.uniba.rz.app.RabbitmqConfiguration.RabbitMqConfiguration.EXCHANGE_NAME;
import static de.uniba.rz.app.RabbitmqConfiguration.RabbitMqConfiguration.SENDING_QUEUE;

public class Sender {
    public static void SenderMethod(Ticket ticket, String method) {

        try {
            final Channel channel = RabbitMqConfiguration.createQueue();
            String callbackQueueName = channel.queueDeclare().getQueue();

            final String corrId = UUID.randomUUID().toString();
            BasicProperties props = new BasicProperties
                    .Builder()
                    .correlationId(corrId)
                    .replyTo(callbackQueueName)
                    .build();
            if(method == "create") {
                String message = "You just created a ticket! Check ticket in Server! " + LocalDateTime.now();
                channel.basicPublish("", SENDING_QUEUE, props, ticket.toString().getBytes("UTF-8"));
                //channel.basicPublish( EXCHANGE_NAME, "", props, ticket.toString().getBytes());
                System.out.println(" !!! Message from client:  '" + message + "'");
            }
            if(method == "update") {
                String message = "You just updated a ticket! Check ticket in Server! " + LocalDateTime.now();
                channel.basicPublish("", SENDING_QUEUE, props, ticket.toString().getBytes("UTF-8"));
                //channel.basicPublish( EXCHANGE_NAME, "", props, ticket.toString().getBytes());
                System.out.println(" !!! Message from client:  '" + message + "'");
            }

            final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

            String ctag = channel.basicConsume(callbackQueueName, true, (consumerTag, delivery) -> {
                if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                    response.offer(new String(delivery.getBody(), "UTF-8"));
                }
            }, consumerTag -> {
            });

            String result = response.take();

            channel.basicCancel(ctag);
            channel.basicPublish( EXCHANGE_NAME, "", props, result.getBytes());
            System.out.println("!!! Received response in client:  '" + result + "'");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
