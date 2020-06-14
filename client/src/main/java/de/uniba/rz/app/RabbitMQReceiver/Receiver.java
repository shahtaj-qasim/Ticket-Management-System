package de.uniba.rz.app.RabbitMQReceiver;


import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import de.uniba.rz.app.RabbitMQSender.Sender;



public class Receiver {
	private final static String QUEUE_NAME = "hello-world";

	
	public void MQconnectionReceiveMessage() throws IOException, TimeoutException {
		Sender sndmsg= new Sender();
		sndmsg.MQconnectionSendMessage();
		System.out.println("In Consumer MQConnection method");
		ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection(); 
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null); //if you start consumer before sender, then u wont have any queues so you can just leave this here 
        System.out.println(" !! Waiting for message");
            
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" !! Received message: '" + message + "'");
        };
        System.out.println(" !! Received message: ");
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
            
            
        }
	}


