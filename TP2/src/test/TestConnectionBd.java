package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestConnectionBd {
	private String JDBC_DRIVER;  
    private String DB_URL;
	//Database credentials
    private String USER;
    private String PASS;
    private Connection conn;
	
	public TestConnectionBd() {
		JDBC_DRIVER = "com.mysql.jdbc.Driver";
		DB_URL = "jdbc:mysql://localhost/systemdistribue?user=root&password=";
		USER = "root";
		PASS = "";
		conn = null;
	}
	
	public void loadClass() {
		try {
	        Class.forName(JDBC_DRIVER).newInstance();
	    } catch (Exception ex) {
	        System.out.println("error :"+ex.getMessage());
	    }
	}
	
	public void connectTo() {
		try {
		    conn = DriverManager.getConnection(DB_URL);
		    
		    
		} catch (SQLException ex) {
		    // handle any errors
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
	}

    public String getDB_URL() {
		return DB_URL;
	}

	public void setDB_URL(String dB_URL) {
		DB_URL = dB_URL;
	}

	public String getUSER() {
		return USER;
	}

	public void setUSER(String uSER) {
		USER = uSER;
	}

	public String getPASS() {
		return PASS;
	}

	public void setPASS(String pASS) {
		PASS = pASS;
	}
}
