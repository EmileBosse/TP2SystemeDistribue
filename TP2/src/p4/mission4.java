package p4;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.Connection;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

public class mission4 {
	public static void main(String[] argv) throws Exception {
		System.out.println("mission 4 démarre indépendaement ?");
		System.in.read();
		System.out.println("Mission 4 terminé!");
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		
		channel.exchangeDeclare("toP4", "topic");
		String queueName = channel.queueDeclare().getQueue();
		
		/*if (argv.length < 1) {
		  System.err.println("Usage: ReceiveLogsTopic [binding_key]...");
		  System.exit(1);
		}*/
		String[] bindingKeys = { "image", "text" };
		
		for (String bindingKey : bindingKeys) {
		  channel.queueBind(queueName, "toP4", bindingKey);
		}
		
		Consumer consumer = new DefaultConsumer(channel) {
		  @Override
		  public void handleDelivery(String consumerTag, Envelope envelope,
		                             AMQP.BasicProperties properties, byte[] body) throws IOException {
			  //gestion ici
			  
		    String message = new String(body, "UTF-8");
		System.out.println(" [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
		      }
		    };
		    channel.basicConsume(queueName, true, consumer);
		  }
		
}
