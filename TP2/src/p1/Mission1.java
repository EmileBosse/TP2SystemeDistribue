package p1;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.Connection;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Blob;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RootPaneContainer;

import org.htmlcleaner.CommentNode;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;
import org.htmlcleaner.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

import helper.Data;

/*Récupérer, à l’intérieur d’une boucle, des données (texte en Anglais + images)
 * d’une source distante (site web) ou locale (base de données ou fichiers json)
 * et les soumettre à un cluster RabbitMQ pour qu’elles soient traitées par 
 * d’autres programme*/
public class Mission1 {
	
	private static Data d = new Data();
	static String NOM_FILE_ATTENTE = "file_d-attente_bd";
	static String hostName = "localhost";
	//String hostName = "192.168.56.1";
	static boolean durable = true;
	
	
	public static void main(String[] argv) throws Exception {
		
		EventQueue.invokeLater(() -> {
			JFrame frame = new JFrame();
			initUI(frame);
			frame.setVisible(true);
        });
	}
	
	private static void initUI(JFrame frame) {
		
		createMenuBar(frame);
		
		ImageIcon icon = new ImageIcon("../groot.png");
		
		frame.setIconImage(icon.getImage());
		
		frame.setTitle("machine 1");
		frame.setSize(600, 200);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	 private static void createMenuBar(JFrame frame) {

        JMenuBar menubar = new JMenuBar();

        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);

        JMenu impMenu = new JMenu("import");
        
        JMenuItem impWeb = new JMenuItem("from website");
        JMenuItem impBd = new JMenuItem("from data base");
        JMenuItem impLocal = new JMenuItem("from local directories");
        
        impMenu.add(impWeb);
        impMenu.add(impBd);
        impMenu.add(impLocal);
        
        impWeb.addActionListener((ActionEvent event) -> {clearUI(frame);impWebUI(frame);});
        impBd.addActionListener((ActionEvent event) -> {clearUI(frame);impBdUI(frame);});
        impLocal.addActionListener((ActionEvent event) -> {clearUI(frame);impLocalUI(frame);});

        file.add(impMenu);

        menubar.add(file);

        frame.setJMenuBar(menubar);
    }
	
	private static void clearUI(JFrame frame) {
		frame.getContentPane().removeAll();
	}

	//fonction qui affiche l'interface lorsque qu'importation via site web
	private static void impWebUI(JFrame frame) {
		JLabel lblText = new JLabel("text link:");
		JLabel lblImage = new JLabel("image link:");
		JLabel lblTextAttribute = new JLabel("Enter the css selector for the text:");
		JLabel lblImageAttribute = new JLabel("Enter the css selector for the image:");
		JTextField textLink = new JTextField();
		JTextField imageLink = new JTextField();
		JTextField textCssAttribute = new JTextField();
		JTextField imageCssAttribute = new JTextField();
		
		JButton sendButton = new JButton("send to database");
		sendButton.setEnabled(false);
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//envoyer l'image
				try {
					actionSendData(Files.readAllBytes(d.getImages()[0].toPath()), "image");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				//envoyer le texte
				actionSendData(d.getTextEN().getBytes(), "text");
			}
		});

		JButton getButton = new JButton("Get data");
		getButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					actionGetWebData(textLink, imageLink, textCssAttribute, imageCssAttribute , sendButton);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		Container pane = frame.getContentPane();
        GroupLayout gl = new GroupLayout(pane);
        pane.setLayout(gl);
        gl.setAutoCreateContainerGaps(true);

        gl.setHorizontalGroup(gl.createParallelGroup()
        		.addGroup(gl.createSequentialGroup()
        				.addComponent(lblText)
        				.addComponent(textLink))
        		.addGroup(gl.createSequentialGroup()
        				.addComponent(lblTextAttribute)
        				.addComponent(textCssAttribute))
        		.addGroup(gl.createSequentialGroup()
        				.addComponent(lblImage)
        				.addComponent(imageLink))
        		.addGroup(gl.createSequentialGroup()
        				.addComponent(lblImageAttribute)
        				.addComponent(imageCssAttribute))
        		.addGroup(gl.createSequentialGroup()
        				.addComponent(getButton)
        				.addComponent(sendButton))
        );

        gl.setVerticalGroup(gl.createSequentialGroup()
        		.addGroup(gl.createParallelGroup()
        				.addComponent(lblText)
        				.addComponent(textLink))
        		.addGroup(gl.createParallelGroup()
        				.addComponent(lblTextAttribute)
        				.addComponent(textCssAttribute))
        		.addGroup(gl.createParallelGroup()
        				.addComponent(lblImage)
        				.addComponent(imageLink))
        		.addGroup(gl.createParallelGroup()
        				.addComponent(lblImageAttribute)
        				.addComponent(imageCssAttribute))
        		.addGroup(gl.createParallelGroup()
        				.addComponent(getButton)
        				.addComponent(sendButton))
        );
	}

	//fonction qui affiche l'interface lorsque qu'importation via base de donnée
	private static void impBdUI(JFrame frame) {
		JLabel lblUsername = new JLabel("userName:");
		JTextField txtUsername = new JTextField();
		JLabel lblPassword = new JLabel("password:");
		JTextField txtPassword = new JTextField();
		JLabel lbldbms = new JLabel("dbms:");
		JTextField txtdbms = new JTextField();
		JLabel lblServerName = new JLabel("Server name:");
		JTextField txtServerName = new JTextField();
		JLabel lblportNumber = new JLabel("port number:");
		JTextField txtPortNumber = new JTextField();
		JLabel lblDbName = new JLabel("Db name:");
		JTextField txtDbName = new JTextField();
		JLabel lblSelectStatement = new JLabel("Select statement:");
		JTextArea txtSelectStatement = new JTextArea();
		
		JButton sendButton = new JButton("Send to database");
		sendButton.setToolTipText("It will first translate your text to french and edit your image. Then send it to a preetablish database.");
		
		JButton getButton = new JButton("Get the data");
		
		
		Container pane = frame.getContentPane();
        GroupLayout gl = new GroupLayout(pane);
        pane.setLayout(gl);
        gl.setAutoCreateContainerGaps(true);

        gl.setHorizontalGroup(gl.createParallelGroup()
        		.addGroup(gl.createSequentialGroup()
        				.addComponent(lblUsername)
        				.addComponent(txtUsername)
        				.addComponent(lblPassword)
        				.addComponent(txtPassword)
        				.addComponent(lblSelectStatement))
        		.addGroup(gl.createSequentialGroup()
        				.addComponent(lbldbms)
        				.addComponent(txtdbms)
        				.addComponent(lblServerName)
        				.addComponent(txtServerName)
        				.addComponent(txtSelectStatement))
        		.addGroup(gl.createSequentialGroup()
        				.addComponent(lblportNumber)
        				.addComponent(txtPortNumber)
        				.addComponent(lblDbName)
        				.addComponent(txtDbName)
        				.addComponent(txtSelectStatement))
        		.addGroup(gl.createSequentialGroup()
        				.addComponent(getButton)
        				.addComponent(sendButton))
        );

        gl.setVerticalGroup(gl.createSequentialGroup()
        		.addGroup(gl.createParallelGroup()
        				.addComponent(lblUsername)
        				.addComponent(txtUsername)
        				.addComponent(lblPassword)
        				.addComponent(txtPassword)
                		.addComponent(lblSelectStatement))
        		.addGroup(gl.createParallelGroup()
        				.addComponent(lbldbms)
        				.addComponent(txtdbms)
        				.addComponent(lblServerName)
        				.addComponent(txtServerName)
                		.addComponent(txtSelectStatement))
        		.addGroup(gl.createParallelGroup()
        				.addComponent(lblportNumber)
        				.addComponent(txtPortNumber)
        				.addComponent(lblDbName)
        				.addComponent(txtDbName)
                		.addComponent(txtSelectStatement))
        		.addGroup(gl.createParallelGroup()
        				.addComponent(getButton)
        				.addComponent(sendButton))
        );
	}
	
	//fonction qui affiche l'interface lorsque qu'importation via explorateur fichier
	private static void impLocalUI(JFrame frame) {
		JLabel textPath = new JLabel("");
		JLabel imagePath = new JLabel("");

		JButton sendButton = new JButton("send to database");
		sendButton.setEnabled(false);
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//envoyer l'image
				try {
					actionSendData(Files.readAllBytes(d.getImages()[0].toPath()), "image");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				//envoyer le texte
				actionSendData(d.getTextEN().getBytes(), "text");
			}
		});
		
		JButton importText = new JButton("text file");
		importText.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				try {
					actionImportText(frame, imagePath, textPath, sendButton);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		JButton importImage = new JButton("image file");
		importImage.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {
					actionImportImage(frame, imagePath, textPath, sendButton);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		Container pane = frame.getContentPane();
        GroupLayout gl = new GroupLayout(pane);
        pane.setLayout(gl);
        gl.setAutoCreateContainerGaps(true);

        gl.setHorizontalGroup(gl.createParallelGroup()
        		.addGroup(gl.createSequentialGroup()
        				.addComponent(importText)
        				.addComponent(importImage))
        		.addGroup(gl.createSequentialGroup()
        				.addComponent(textPath))
        		.addGroup(gl.createSequentialGroup()
        				.addComponent(imagePath))
        		.addGroup(gl.createSequentialGroup()
        				.addComponent(sendButton))
        );

        gl.setVerticalGroup(gl.createSequentialGroup()
        		.addGroup(gl.createParallelGroup()
        				.addComponent(importText)
        				.addComponent(importImage))
        		.addGroup(gl.createParallelGroup()
        				.addComponent(textPath))
        		.addGroup(gl.createParallelGroup()
        				.addComponent(imagePath))
        		.addGroup(gl.createParallelGroup()
        				.addComponent(sendButton))
        );
	}
	
	//cette fonction récupère les informations d'un site web
	private static void actionGetWebData(JTextField textLink, JTextField imageLink, JTextField textCssAttribute, JTextField imageCssAttribute, JButton sendButton) throws IOException {
		String urlText = textLink.getText();
		String urlImage = imageLink.getText();
		
		if(!urlText.equals("")) {
			Document doc = Jsoup.connect(urlText).get();
			String textContents = "";
			Element el = doc.select(textCssAttribute.getText()).first();
			Elements els = el.getAllElements();
			Iterator<Element> it = els.iterator();
			it.next();
			while(it.hasNext()) {
				textContents += it.next().text();
				textContents += "\n";
			}
			System.out.print("Voici le text en Anglais:\n"+textContents);
			
			d.setTextEN(textContents);
		}
//ici communiquer avec Jean pour son avis
		if(!urlImage.equals("")) {
			Document doc = Jsoup.connect(urlImage).get();
			Element el = doc.selectFirst(imageCssAttribute.getText());
			//System.out.println(el.text());
			System.out.println("***********");
			System.out.println(el.toString());
			System.out.println("***********");
			//System.out.println(el.html());
			
		}
		if(!d.getTextEN().equals("") && !d.getImages()[0].equals(null)) {
			sendButton.setEnabled(true);
		}
	}
	
	//cette fonction utilise rabbitmq pour communiquer avec p2 et p3 qui a leurs tours vont envoyer le tout au serveur
	private static void actionSendData(byte[] message, String topics) {
		ConnectionFactory factory = new ConnectionFactory();
		
		factory.setHost(hostName);
		
		Connection connexion;
		try {
			connexion = factory.newConnection();
			
			Channel canalCommunication = connexion.createChannel();
			
			canalCommunication.exchangeDeclare("fromP1", "topic");
			
			canalCommunication.basicPublish("fromP1", topics, null, message);
			
			connexion.close();
			
		
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//fonction lorsque l'import est fait avec l'explorateur de fichier pour le text
	private static void actionImportText(JFrame frame, JLabel imagePath, JLabel textPath, JButton sendButton) throws IOException
	{
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			textPath.setText(fc.getSelectedFile().getPath());
			fileToData(fc.getSelectedFile(), null);
        }
		if(imagePath.getText() != "") {
			sendButton.setEnabled(true);
		}
	}

	//fonction lorsque l'import est fait avec l'explorateur de fichier pour l'image
	private static void actionImportImage(JFrame frame, JLabel imagePath, JLabel textPath, JButton sendButton) throws IOException
	{
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			imagePath.setText(fc.getSelectedFile().getPath());
			fileToData(null,fc.getSelectedFile());
        }
		if(textPath.getText() != "") {
			sendButton.setEnabled(true);
		}
	}
	
	//fonction qui transfer les fichiers en Data
	private static void fileToData(File text, File image) throws IOException
	{
		if(text != null) {
			BufferedReader br = new BufferedReader(new FileReader(text.getPath()));
			try {
			    StringBuilder sb = new StringBuilder();
			    String line = br.readLine();

			    while (line != null) {
			        sb.append(line);
			        sb.append(System.lineSeparator());
			        line = br.readLine();
			    }
			    String everything = sb.toString();
				d.setTextEN(everything);
			} finally {
			    br.close();
			}
		}
		if(image != null) {
			//encore mistérieux
			d.setInitialImage(image);
		}
	}

	public byte[] extractBytes (String ImageName) throws IOException {
		 // open image
		 File imgPath = new File(ImageName);
		 BufferedImage bufferedImage = ImageIO.read(imgPath);

		 // get DataBufferBytes from Raster
		 WritableRaster raster = bufferedImage .getRaster();
		 DataBufferByte data   = (DataBufferByte) raster.getDataBuffer();

		 return ( data.getData() );
	}

}
