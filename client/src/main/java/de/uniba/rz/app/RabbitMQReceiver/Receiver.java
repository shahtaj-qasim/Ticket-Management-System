package de.uniba.rz.app.RabbitMQSender;
import com.rabbitmq.client.ConnectionFactory;

import de.uniba.rz.app.RabbitMQReceiver.Receiver;

import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;

public class Sender {
	
	private final static String QUEUE_NAME = "hello-world";
	private static Connection connection;
	
	public static String main(String[] args) throws IOException, TimeoutException, InterruptedException {
		// TODO Auto-generated method stub
		System.out.println("In sender MQConnection method");
		ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection(); 
        
        try (Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String callbackQueueName = channel.queueDeclare().getQueue();
            final String corrId = UUID.randomUUID().toString();
            BasicProperties props = new BasicProperties
                            .Builder()
                            .correlationId(corrId)
                            .replyTo(callbackQueueName)
                            .build();
            String message= "Hello world, wanna work?" + LocalDateTime.now();
            channel.basicPublish("", QUEUE_NAME, props, message.getBytes("UTF-8"));
            System.out.println(" !!! Sent message:  '" + message + "'");
            
           //Receiver rcv= new Receiver();
            //rcv.MQconnectionReceiveMessage();
            // code to read a response message from the queue
            final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

            String ctag = channel.basicConsume(callbackQueueName, true, (consumerTag, delivery) -> {
                if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                    response.offer(new String(delivery.getBody(), "UTF-8"));
                }
            }, consumerTag -> {
            });

            String result = response.take();
            channel.basicCancel(ctag);
            System.out.println("get response sender method: "+result);
            return result;
           
        } catch (IOException e) {
			// TODO Auto-generated catch block
        	System.out.println(e.getMessage());
			e.printStackTrace();
			return e.getMessage();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
			return e.getMessage();
		}
	}
	public void close() throws IOException {
        connection.close();
    }

}
