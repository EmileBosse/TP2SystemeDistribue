package test;

public abstract class TestThings {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("D�marrage des tests de bd.");
		TestConnectionBd bd = new TestConnectionBd();
		System.out.println("On load..");
		bd.loadClass();
		System.out.println("load termin�.\n connection..");
		bd.connectTo();
		System.out.println("connection termin�.");
		
	}

}
