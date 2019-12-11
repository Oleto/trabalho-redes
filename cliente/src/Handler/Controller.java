package Handler;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import Constantes.Damas;
import Modelo.Player;
import Modelo.Quadrado;
import View.BoardPanel;


public class Controller implements Runnable {
	private boolean continueToPlay;
	private boolean waitingForAction;
	private boolean isOver;
	
	//Network
	private DataInputStream fromServer;
	private DataOutputStream toServer;
	
	private BoardPanel boardPanel;
	private Player player;
	
	//Data
	private LinkedList<Quadrado> selectedSquares;
	private LinkedList<Quadrado> playableSquares;
	//private LinkedList<Square> crossableSquares;
	
	public Controller(Player player, DataInputStream input, DataOutputStream output){
		this.player = player;
		this.fromServer = input;
		this.toServer= output;
		
		selectedSquares = new LinkedList<Quadrado>();
		playableSquares = new LinkedList<Quadrado>();
	}
	
	public void setBoardPanel(BoardPanel panel){
		this.boardPanel = panel;
	}
	
	@Override
	public void run() {
		continueToPlay = true;
		waitingForAction = true;
		isOver=false;
		
		try {
			
			//Player One
			if(player.getPlayerID()==Damas.JOGADOR_UM.getValue()){
				//wait for the notification to start
				fromServer.readInt();
				player.setMyTurn(true);
			}
					
			while(continueToPlay && !isOver){
				if(player.getPlayerID()==Damas.JOGADOR_UM.getValue()){
					waitForPlayerAction();
					if(!isOver)
						receiveInfoFromServer();
				}else if(player.getPlayerID()==Damas.JOGADOR_DOIS.getValue()){
					receiveInfoFromServer();
					if(!isOver)
						waitForPlayerAction();
				}
			}
			
			if(isOver){
				JOptionPane.showMessageDialog(null, "Game  over",
						"Informação", JOptionPane.INFORMATION_MESSAGE, null);
				System.exit(0);
			}
			
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Conexão perdida",
					"Erro", JOptionPane.ERROR_MESSAGE, null);
			System.exit(0);
		} catch (InterruptedException e) {
			JOptionPane.showMessageDialog(null, "Conexão interrompida ",
					"Erro", JOptionPane.ERROR_MESSAGE, null);
		}			
	}
	
	private void receiveInfoFromServer() throws IOException {
		player.setMyTurn(false);
		int from = fromServer.readInt();
		if(from==Damas.Voce_Perdeu.getValue()){
			from = fromServer.readInt();
			int to = fromServer.readInt();
			updateReceivedInfo(from, to);
			isOver=true;
		}else if(from==Damas.Voce_Ganhou.getValue()){
			isOver=true;
			continueToPlay=false;
		}else{
			int to = fromServer.readInt();
			updateReceivedInfo(from, to);
		}
	}	

	private void sendMove(Quadrado from, Quadrado to) throws IOException {
		toServer.writeInt(from.getSquareID());
		toServer.writeInt(to.getSquareID());
	}

	private void waitForPlayerAction() throws InterruptedException {
		player.setMyTurn(true);
		while(waitingForAction){
			Thread.sleep(100);
		}
		waitingForAction = true;		
	}
	
	public void move(Quadrado from, Quadrado to){
		to.setPlayerID(from.getPlayerID());
		from.setPlayerID(Damas.Quadrado_vazio.getValue());
		checkCrossJump(from, to);
		checkKing(from, to);
		squareDeselected();
		
		waitingForAction = false;
		try {
			sendMove(from, to);
		} catch (IOException e) {
			System.out.println("Sending failed");
		}		
	}
	
	//When a square is selected
	public void squareSelected(Quadrado s) {
		
		if(selectedSquares.isEmpty()){
			addToSelected(s);
		}		
		//if one is already selected, check if it is possible move
		else if(selectedSquares.size()>=1){
			if(playableSquares.contains(s)){
				move(selectedSquares.getFirst(),s);
			}else{
				squareDeselected();
				addToSelected(s);
			}
		}
	}
	
	private void addToSelected(Quadrado s){
		s.setSelected(true);
		selectedSquares.add(s);
		getPlayableSquares(s);
	}

	public void squareDeselected() {
		
		for(Quadrado square:selectedSquares)
			square.setSelected(false);
		
		selectedSquares.clear();
		
		for(Quadrado square:playableSquares){
			square.setPossibleToMove(false);
		}
		
		playableSquares.clear();
		boardPanel.repaintPanels();
	}
	
	
	private void getPlayableSquares(Quadrado s){
		playableSquares.clear();		
		playableSquares = boardPanel.getPlayableSquares(s);
		
		for(Quadrado square:playableSquares){
			System.out.println(square.getSquareID());
		}		
		boardPanel.repaintPanels();
	}
	
	public boolean isHisTurn(){
		return player.isMyTurn();
	}
	
	private void checkCrossJump(Quadrado from, Quadrado to){		
		if(Math.abs(from.getSquareRow()-to.getSquareRow())==2){		
			int middleRow = (from.getSquareRow() + to.getSquareRow())/2;
			int middleCol = (from.getSquareCol() + to.getSquareCol())/2;
			
			Quadrado middleSquare = boardPanel.getSquare((middleRow*8)+middleCol+1);
			middleSquare.setPlayerID(Damas.Quadrado_vazio.getValue());
			middleSquare.removeKing();
		}
	}
	
	private void checkKing(Quadrado from, Quadrado movedSquare){		
		if(from.isKing()){
			movedSquare.setKing();
			from.removeKing();
		}else if(movedSquare.getSquareRow()==7 && movedSquare.getPlayerID()==Damas.JOGADOR_UM.getValue()){
			movedSquare.setKing();
		}else if(movedSquare.getSquareRow()==0 && movedSquare.getPlayerID()==Damas.JOGADOR_DOIS.getValue()){
			movedSquare.setKing();
		}
	}
	
	private void updateReceivedInfo(int from, int to){
		Quadrado fromSquare = boardPanel.getSquare(from);
		Quadrado toSquare = boardPanel.getSquare(to);
		toSquare.setPlayerID(fromSquare.getPlayerID());
		fromSquare.setPlayerID(Damas.Quadrado_vazio.getValue());
		checkCrossJump(fromSquare, toSquare);
		checkKing(fromSquare, toSquare);
		boardPanel.repaintPanels();
	}
}
