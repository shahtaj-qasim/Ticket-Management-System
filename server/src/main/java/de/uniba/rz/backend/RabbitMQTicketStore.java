package de.uniba.rz.backend;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import de.uniba.rz.backend.RabbitmqConfiguration.RabbitMqConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;



import static de.uniba.rz.backend.RabbitmqConfiguration.RabbitMqConfiguration.SENDING_QUEUE;

public class RabbitMQTicketStore implements RemoteAccess {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQTicketStore.class);
    private static Channel channel;

    @Override
    public void prepareStartup(TicketStore ticketStore) { }

    @Override
    public void shutdown() {}

    @Override
    public void run() {

        LOGGER.warn("Logging is working on server side");
        try {
            channel = RabbitMqConfiguration.createQueue();


            System.out.println(" !! Waiting for message");

            Object monitor = new Object();
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .build();
                String response = "Server response!! Hey! a ticket is created or updated!!"+ LocalDateTime.now();
                try {
                    String message = new String(delivery.getBody(), "UTF-8");
                    if (!message.isEmpty()) {
                        System.out.println(" Server: '" + message + "");
                        System.out.println(" Sending server response: '" + response + "");

                        channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                       }
                }
                catch (RuntimeException e) {
                    System.out.println(" [.] " + e.toString());
                }
            };
            channel.basicConsume(SENDING_QUEUE, false, deliverCallback, (consumerTag -> { }));
            while (true) {
                synchronized (monitor) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
