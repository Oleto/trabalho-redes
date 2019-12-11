package View;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.LinkedList;

import javax.sound.midi.ControllerEventListener;
import javax.swing.*;
import Modelo.Tabuleiro;
import Modelo.Quadrado;
import Constantes.SessionVariable;
import Handler.*;


public class BoardPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private Dimension panelSize = new Dimension(720,720);
	private Tabuleiro boardModel;
	private MyMouseListener listener;
	private LinkedList<SquarePanel> panels;
	private Quadrado[][] squares;

	public BoardPanel(MyMouseListener listener){
		setPreferredSize(panelSize);
		setLayout(new GridLayout(8,8));
		
		boardModel = new Tabuleiro();
		squares = boardModel.getQuadrados();
		this.listener = listener;		
		panels = new LinkedList<SquarePanel>();		
		
		initializeSquarePanels();
		
		System.out.println(boardModel.getTotlaSquares());		
	}
	
	private void initializeSquarePanels() {
		for(int i=0;i<8;i++){
			for(int k=0;k<8;k++){
				SquarePanel sPanel = new SquarePanel(squares[i][k]);
				if(sPanel.getSquare().isPossibleToMove() || sPanel.getSquare().getPlayerID()==SessionVariable.myID.getValue()){
					sPanel.addMouseListener(listener);
				}
				panels.add(sPanel);
				add(sPanel);				
			}			
		}
	}
	
	public void repaintPanels(){
		for(SquarePanel panel : panels){
			panel.setListner(listener);	
		}
		
		repaint();
	}

	public LinkedList<Quadrado> getPlayableSquares(Quadrado s){
		return boardModel.findPlayableSquares(s);		
	}
	
	public Quadrado getSquare(int ID){
		return panels.get(ID-1).getSquare();
	}
}
