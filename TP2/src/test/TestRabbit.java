package test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class TestRabbit {

	private Connection connection;
	private Channel channel;
	String queueName;
	
	public TestRabbit() {}
	
	public void start(String nomFile) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		connection = (Connection) factory.newConnection();
		channel = ((com.rabbitmq.client.Connection) connection).createChannel();
		
		channel.exchangeDeclare(nomFile, "topic");
		queueName = channel.queueDeclare().getQueue();
	}
	
	public void startListen(String nomFile, String[] keys) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = (Connection) factory.newConnection();
		Channel channel = ((com.rabbitmq.client.Connection) connection).createChannel();
		
		channel.exchangeDeclare(nomFile, "topic");
		String queueName = channel.queueDeclare().getQueue();
		
		for (String bindingKey : keys) {
			channel.queueBind(queueName, "fromP1", bindingKey);
		}
		
		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
			                           AMQP.BasicProperties properties, byte[] body)
			                          		 throws IOException {
				
				if(envelope.getRoutingKey().equals("text")) {
					System.out.println("reception d'un text");
				}else if(envelope.getRoutingKey().equals("image")) {
					System.out.println("reception d'un image");
				}else {
					System.out.println("reception d'un incognito");
				}
				//String message = new String(body, "UTF-8");
				//System.out.println(" [x] Received '" + message + "'");
			}
		};
		channel.basicConsume(queueName, true, consumer);
	}
	
	public void setRoutingKey(String[] keys) throws IOException {
		for (String bindingKey : keys) {
			  channel.queueBind(queueName, "fromP1", bindingKey);
			}
	}
	
	public void listen() throws IOException {
		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
			                           AMQP.BasicProperties properties, byte[] body)
			                          		 throws IOException {
				
				if(envelope.getRoutingKey().equals("text")) {
					System.out.println("reception d'un text");
				}else if(envelope.getRoutingKey().equals("image")) {
					System.out.println("reception d'un image");
				}else {
					System.out.println("reception d'un incognito");
				}
			}
		};
		channel.basicConsume(queueName, true, consumer);
	}
	
}
