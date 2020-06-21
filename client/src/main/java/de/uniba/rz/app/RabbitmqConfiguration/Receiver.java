package de.uniba.rz.app.RabbitmqConfiguration;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;


import static de.uniba.rz.app.RabbitmqConfiguration.RabbitMqConfiguration.*;

public class Receiver {
	private static Channel channel = null;

	//Receives messages from server and publishes to all clients
	//Author: Shahtaj
	public static void ReceiverMethod(String[] args) throws IOException, TimeoutException {


		try {
			channel = RabbitMqConfiguration.createQueue();


			System.out.println(" !! Waiting for message");

			Object monitor = new Object();
			DeliverCallback deliverCallback = (consumerTag, delivery) -> {
				AMQP.BasicProperties replyProps = new AMQP.BasicProperties
						.Builder()
						.correlationId(delivery.getProperties().getCorrelationId())
						.build();

					// RabbitMq consumer worker thread notifies the RPC server owner thread
					synchronized (monitor) {
						monitor.notify();
					}

			};
			//edit by Waleeha
			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
					String message = new String(body, "UTF-8");
					System.out.println(" Helloo !! There's a notification from server: '" + message + "'");
				}
			};
			channel.basicConsume(RECEIVING_QUEUE, false, consumer);
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
