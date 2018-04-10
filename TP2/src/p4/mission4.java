package p4;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.MessageProperties;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;

import javax.sql.rowset.serial.SerialException;

import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;


//problème prévu est la gestion de quel entry et image vont être créé ensemble
public class mission4 {
	public static void main(String[] argv) throws Exception {
		//connection à la base de donné
		Connection conn = null;
		Statement stmt = null;
		System.out.println("mission 4 -> connection bdd..");
		if(connectToBd(conn, stmt)) {
		System.out.println("mission 4 -> le dernier id d'entré est : " + getLastEntryId(conn, stmt));
		System.out.println("mission 4 -> le dernier id d'image est : " + getLastImageId(conn, stmt));
		System.out.println("mission 4 -> connection bdd terminé.");
		
		//me permettera d'ajouter la bonne clé secondaire
		boolean entryCreated = false;
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = (Connection) factory.newConnection();
		Channel channel = ((com.rabbitmq.client.Connection) connection).createChannel();
		
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

		System.out.println("mission 4 -> écoute message..");
		
		Consumer consumer = new DefaultConsumer(channel) {
			@SuppressWarnings("unchecked")
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
		                             AMQP.BasicProperties properties, byte[] body) throws IOException {
			  				  		
				
				//read l'object
				ByteArrayInputStream b = new ByteArrayInputStream(body);
				ObjectInputStream obj = new ObjectInputStream(b);
	
				List<Object> list = null;
				try {
					list = (List<Object>)obj.readObject();
				} catch (ClassNotFoundException e1) {
					
					e1.printStackTrace();
				}
				
				//verifier si c'est du text ou une image
				PreparedStatement statement = null;
				if(envelope.getRoutingKey().equals("text")) {
	  				try {
	  					//on prend en considération que c'est toujours l'anglais en premier
	  					statement = ((java.sql.Connection) conn).prepareStatement("insert into entry(id,TextFr,TextEn) values(?, ?, ?)");
	  					statement.setString(2, (String)list.get(1));
	  					statement.setString(3, (String)list.get(0));
	  					statement.setInt(1, (getLastEntryId(conn, stmt)+1));
						
					} catch (SQLException e) {
						
						e.printStackTrace();
					}
	  				
	  				send(conn, stmt, statement);
	  			}
	  			else if(envelope.getRoutingKey().equals("image")){
	  				for(int i = 0; i < list.size(); i++) {
	  					//getLastEntryId pour savoir le id
	  					//ici je fais en sorte de faire référence à un id 
	  					//qui n'existe pas encore mais qui devrait dans un future proche
	  					//si qui pourrait être une cause de problème
	  					int idSecondaire = 0;
	  					if(entryCreated) {
	  						idSecondaire = getLastEntryId(conn, stmt);
	  					}else {
	  						idSecondaire = getLastEntryId(conn, stmt)+1;
	  					}
	  					
		  				try {
		  					statement = ((java.sql.Connection) conn).prepareStatement("insert into image(id, idEntry, image, isOriginal) values(?, ?, ?, ?)");
							Blob blob = new javax.sql.rowset.serial.SerialBlob((byte[])list.get(i));
							statement.setInt(1, (getLastImageId(conn, stmt)+1));
							statement.setInt(2, idSecondaire);
			  				statement.setBlob(3, blob);
			  				if(i == 0) {statement.setBoolean(4, true);} else {statement.setBoolean(3, false);}
			  				
						} catch (SerialException e) {
							
							e.printStackTrace();
						} catch (SQLException e) {
							
							e.printStackTrace();
						}
		  				
		  				send(conn, stmt, statement);
	  				}
	  			}
			  			//fermer la connexion seulement lorsqu'on ferme mission 4
			  			//close(conn, stmt);
	      	}
	    };
    	channel.basicConsume(queueName, true, consumer);
		}
	}
		
	
	private static boolean connectToBd(Connection conn, Statement stmt) {
		
		boolean connected = false;
		// JDBC driver name and database URL
		String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
		String DB_URL = "jdbc:mysql://localhost:3306/systemdistribue";

		//  Database credentials
		String USER = "root";
		String PASS = "";
		   
		try{
		   //STEP 2: Register JDBC driver
		   Class.forName(JDBC_DRIVER).newInstance();

		   //STEP 3: Open a connection
		   System.out.println("Connecting to database...");
		   conn = (Connection) DriverManager.getConnection(DB_URL,USER,PASS);
		   
		   //STEP 4: approuve connection
		   connected = true;
		   }
		catch(Exception e) {
			System.out.println("mission 4 -> La connection à la base de donné à échoué.");
		}
		return connected;
	}
		
	private static void send(Connection conn, Statement stmt, PreparedStatement statement){
		try {
			System.out.println("mission 4 -> envoie dans la bd.");
			stmt = ((java.sql.Connection) conn).createStatement();
			statement.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void close(Connection conn, Statement stmt) {
		try {
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static int getLastEntryId(Connection conn, Statement stmt){
		String sql = "SELECT MAX(id) FROM entry";
		int id = -1;
		ResultSet rs;
		try {
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				id = rs.getInt("id");
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return id;
	}

	private static int getLastImageId(Connection conn, Statement stmt) {
		String sql = "SELECT MAX(id) FROM image";
		int id = -1;
		ResultSet rs;
		try {
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				id = rs.getInt("id");
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return id;
	}
}
