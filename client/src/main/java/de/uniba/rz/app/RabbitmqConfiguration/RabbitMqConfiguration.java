package de.uniba.rz.app.RabbitmqConfiguration;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMqConfiguration {

    public final static String SENDING_QUEUE = "sendingQueue";
    public final static String RECEIVING_QUEUE = "receivingQueue";
    public final static String EXCHANGE_NAME = "fanountExchange";

    //Creates queue, binds exchange to the queue
    //Author: Waleeha
    public static Channel createQueue() {

        try {
            Connection conn = createConnection();
            Channel channel = conn.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            channel.queueDeclare(SENDING_QUEUE, false, false, false, null);

            channel.queueDeclare(RECEIVING_QUEUE, false, false, false, null);
            channel.queueBind(RECEIVING_QUEUE, EXCHANGE_NAME, "");

            return channel;
        } catch (Exception ex) {
            System.out.println("Rabbit MQ queue cannot be created");
            return null;
        }
    }
    //sets properties (username, password, host, port etc) for connection
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
