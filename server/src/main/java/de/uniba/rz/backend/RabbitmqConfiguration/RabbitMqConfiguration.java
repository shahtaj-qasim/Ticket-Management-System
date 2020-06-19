package de.uniba.rz.backend.RabbitmqConfiguration;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMqConfiguration {

    public final static String SENDING_QUEUE = "sendingQueue";


    public static Channel createQueue() {

        try {
            Connection conn = createConnection();
            Channel channel = conn.createChannel();
            channel.queueDeclare(SENDING_QUEUE, false, false, false, null);
            channel.basicQos(1);
            return channel;
        } catch (Exception ex) {
            System.out.println("Rabbit MQ queue cannot be created");
            return null;
        }
    }

    public static Connection createConnection(){
        try {
            ConnectionFactory factory = new ConnectionFactory();
// "guest"/"guest" by default, limited to localhost connections
            factory.setUsername("guest");
            factory.setPassword("guest");
            factory.setVirtualHost("/");
            factory.setHost("localhost");
            factory.setPort(5672);
            return factory.newConnection();
        }
        catch (Exception ex){
            System.out.println("Error in rabbitmq connection");
            return null;
        }
    }
}
