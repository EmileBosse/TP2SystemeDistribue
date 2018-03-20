package p1;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.Connection;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.RootPaneContainer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

import helper.Data;

/*Récupérer, à l’intérieur d’une boucle, des données (texte en Anglais + images)
 * d’une source distante (site web) ou locale (base de données ou fichiers json)
 * et les soumettre à un cluster RabbitMQ pour qu’elles soient traitées par 
 * d’autres programme*/
public class Mission1 {
	
	Data d = new Data();
	
	public static void main(String[] argv) throws Exception {
		
		EventQueue.invokeLater(() -> {
			JFrame frame = new JFrame();
			initUI(frame);
			frame.setVisible(true);
        });
		/*
		boolean choixValid = false;
		while(!choixValid) {
			System.out.println("Choix de la source:\n1.distante\n2.locale");
			//create the Scanner
			@SuppressWarnings("resource")
			Scanner terminalInput = new Scanner(System.in);
			//read input
			String choix = terminalInput.nextLine();
			if(choix.equals("1")) {
				choixValid = true;
				getDistant();
			}else if(choix.equals("2")) {
				choixValid = true;
				getLocal();
			}else {
				choixValid = false;
				System.out.println("Choix invalide");
			}
		}
		System.out.println("Mission1 terminé");*/
	}
	
	@SuppressWarnings("serial")
	private static void initUI(JFrame frame) {
		
		createMenuBar(frame);
		
		ImageIcon icon = new ImageIcon("../groot.png");
		
		frame.setIconImage(icon.getImage());
		
		frame.setTitle("machine 1");
		frame.setSize(300, 200);
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
        
        impWeb.addActionListener((ActionEvent event) -> {impWebUI(frame);});
        impBd.addActionListener((ActionEvent event) -> {impBdUI(frame);});
        impLocal.addActionListener((ActionEvent event) -> {impLocalUI(frame);});

        file.add(impMenu);

        menubar.add(file);

        frame.setJMenuBar(menubar);
    }
	
	private static void clearUI(JFrame frame) {
		
	}
	 
	private static void impWebUI(JFrame frame) {
		
	}
	
	private static void impBdUI(JFrame frame) {
		
	}
	
	private static void impLocalUI(JFrame frame) {
		JButton importText = new JButton("text file");
		JButton importImage = new JButton("image file");
		
		
		
		Container pane = frame.getContentPane();
        GroupLayout gl = new GroupLayout(pane);
        pane.setLayout(gl);
        gl.setAutoCreateContainerGaps(true);

        gl.setHorizontalGroup(gl.createParallelGroup()
        		.addGroup(gl.createSequentialGroup()
        				.addComponent(importText)
        				.addComponent(importImage))
        );

        gl.setVerticalGroup(gl.createSequentialGroup()
        		.addGroup(gl.createParallelGroup()
        				.addComponent(importText)
        				.addComponent(importImage))
        );
	}
	 /*
	//aller chercher les données a distance
	@SuppressWarnings("resource")
	private static void getDistant() {
		System.out.println("Veuillez entrer le lien duquel vous voulez prendre votre text anglais:");
		//create the Scanner
		Scanner terminalInput = new Scanner(System.in);

		//read input
		String s = terminalInput.nextLine();
		
		
	}

	//aller chercher les données localement
	private static void getLocal() throws IOException {
		boolean b1 = true;
		while(b1) {
			System.out.println("Choix du type de recherche:\n1.base de donné\n2.explorateur de fichier");
			//create the Scanner
			@SuppressWarnings("resource")
			Scanner terminalInput = new Scanner(System.in);
			//read input
			String choix = terminalInput.nextLine();
			if(choix.equals("1")) {
				b1 = false;
				//créer la connexion à la base de donnée
				
				
			}else if(choix.equals("2")) {
				b1 = false;
				boolean b2 = true;
				while(b2) {
					System.out.println("Choix format:\n1.Format JSON\n2.Text et image");
					//create the Scanner
					@SuppressWarnings("resource")
					Scanner terminalInput2 = new Scanner(System.in);
					//read input
					String choix2 = terminalInput2.nextLine();
					if(choix2.equals("1")) {
						b2 = false;
						//ouvrir l'explorateur de fichier et chercher les .json 
						//pour les transférer ensuite en data
						
					}else if(choix2.equals("2")) {
						b2 = false;
						JFrame frame = new JFrame();
						//ouvrir l'explorateur de fichier pour le text et l'ajouter au data
						System.out.println("Veuillez sélectionner le fichier contenant le text.");
						JFileChooser fc = new JFileChooser();
						fc.showOpenDialog(frame);
						//fc.addChoosableFileFilter(new ImageFilter());
						//ouvrir l'explorateur de fichier pour l'image et l'ajouter au data
						
					}else {
						System.out.println("Choix invalide.");
					}
				}
			}else {
				System.out.println("Choix invalide.");
			}
		}
	}*/
}
