package test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public abstract class TestThings {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*System.out.println("D�marrage des tests de bd.");
		TestConnectionBd bd = new TestConnectionBd();
		System.out.println("On load..");
		bd.loadClass();
		System.out.println("load termin�.\n connection..");
		bd.connectTo();
		System.out.println("connection termin�.");*/
		
		/*System.out.println("test de rabbit mq");
		TestRabbit rabbit = new TestRabbit();
		try {
			rabbit.start("fromP1");
			String[] keys = {"text","image"};
			rabbit.setRoutingKey(keys);
			System.out.println("On commence l'�coute");
			rabbit.listen();
			System.in.read();
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		}*/
	}

}
