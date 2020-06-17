package de.uniba.rz.app.RabbitMQReceiver;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import de.uniba.rz.app.RabbitMQSender.Sender;

public class Receiver {
	
	private final static String QUEUE_NAME = "hello-world";

	public static void main(String[] args) throws IOException, TimeoutException {
		// TODO Auto-generated method stub
		System.out.println("In Receiver main method");
		ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection(); 
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null); //if you start consumer before sender, then u wont have any queues so you can just leave this here 
        channel.queuePurge(QUEUE_NAME);
        
        //channel.basicQos(1);
        
        System.out.println(" !! Waiting for message");
        
        Object monitor = new Object();
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            BasicProperties replyProps = new BasicProperties
                    .Builder()
                    .correlationId(delivery.getProperties().getCorrelationId())
                    .build();
            String response = "This is a response from server";
            try {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" !! Received message: '" + message + "'");
            }
            catch (RuntimeException e) {
                System.out.println(" [.] " + e.toString());
            }finally {
   //     DeliverCallback deliverCallback = (consumerTag, delivery) -> {
   //         String message = new String(delivery.getBody(), "UTF-8");
    //        System.out.println(" !! Received message: '" + message + "'");
            channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            // RabbitMq consumer worker thread notifies the RPC server owner thread
            synchronized (monitor) {
                monitor.notify();
            }
            }
        };
        System.out.println(" !! Received message: ");
        channel.basicConsume(QUEUE_NAME, false, deliverCallback, (consumerTag -> { }));
        while (true) {
            synchronized (monitor) {
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
          
            		
        
	}

}
