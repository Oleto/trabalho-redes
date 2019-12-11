import java.awt.BorderLayout;
import javax.swing.*;

import EnumConstants.Checkers;
import Session.HandleSession;

import java.io.*;
import java.net.*;
import java.util.Date;

/**
 * Server Application -> ServerApp
 * @author Keerthikan
 * 
 * Server and establish Connection
 */
public class ServerApp extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	//Frame components
	private JScrollPane scroll;
	private JTextArea information;
	private JLabel title;
	
	//Network properties
	private ServerSocket serverSocket;
	int sessionNo;
	
	public ServerApp(){
		BorderLayout layout = new BorderLayout();
		setLayout(layout);
		
		title = new JLabel("Server");
		information = new JTextArea();
		scroll = new JScrollPane(information);
		
		add(title,BorderLayout.NORTH);
		add(scroll, BorderLayout.CENTER);
	}	
	
	//Establish connection and wait for Clients
	public void startRunning(){
		
		try{
			
			PropertyManager pm = PropertyManager.getInstance();
			int port = pm.getPort();
			
			//Create a server socket
			serverSocket = new ServerSocket(port);
			information.append(serverSocket.getInetAddress().getHostAddress());
			information.append(new Date() + ":- Servidor começa na porta "+ port + " \n");
			sessionNo = 1;			
			
			while(true){
				
				information.append(new Date()+ ":- Sessão n° "+ sessionNo + " Começou\n");
				
				//Wait for player 1
				Socket player1 = serverSocket.accept();
				information.append(new Date() + ":- Jogador 1 entrou as ");
				information.append(player1.getInetAddress().getHostAddress() + "\n");
				
				//Notification to player1 that's he's connected successfully
				new DataOutputStream(player1.getOutputStream()).writeInt(Checkers.PLAYER_ONE.getValue());
				
				//Wait for player 2
				Socket player2 = serverSocket.accept();
				information.append(new Date() + ":- Jogador 2 entrou as ");
				information.append(player2.getInetAddress().getHostAddress() +"\n");
				
				//Notification to player2 that's he's connected successfully
				new DataOutputStream(player2.getOutputStream()).writeInt(Checkers.PLAYER_TWO.getValue());
				
				//Increase Session number
				sessionNo++;
				
				// Create a new thread for this session of two players
				HandleSession task = new HandleSession(player1, player2);
				new Thread(task).start();
			}
		}catch(Exception ex){			
			ex.printStackTrace();
			System.exit(0);
		}				
	}
}
