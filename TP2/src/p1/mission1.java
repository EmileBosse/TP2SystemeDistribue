package p1;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.Connection;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

//
public class mission1 {
	public static void main(String[] argv) throws Exception {
		System.out.println("mission1");
		System.in.read();
		System.out.println("Mission1 terminé");
	}
}
