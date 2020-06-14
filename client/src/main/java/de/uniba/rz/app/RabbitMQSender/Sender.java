package de.uniba.rz.app.RabbitMQSender;


import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;


import com.rabbitmq.client.Channel;

public class Sender {
	private final static String QUEUE_NAME = "hello-world";
	
	
	public void MQconnectionSendMessage() {
		System.out.println("In sender MQConnection method");
		ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection(); 
        		Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message= "Hello world, wanna work?" + LocalDateTime.now();
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
            System.out.println(" !!! Sent message:  '" + message + "'");
           
        } catch (IOException e) {
			// TODO Auto-generated catch block
        	System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

}
