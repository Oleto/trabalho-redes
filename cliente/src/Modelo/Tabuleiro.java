package Modelo;

import java.util.LinkedList;

import Constantes.Damas;

public class Tabuleiro {

    private Quadrado[][] Quadrados;

    public Tabuleiro() {
        Quadrados = new Quadrado[8][8];

        setQuadrado(Quadrados);
        assignPlayerIDs();

    }

    public Quadrado[][] getQuadrados() {
        return this.Quadrados;
    }

    public void setQuadrado(Quadrado[][] quadrados) {
        this.Quadrados = quadrados;
    }

    public int getTotlaSquares() {
        return Quadrados.length;
    }

    public void printSquareDetails() {
        for (int r = 0; r < Damas.NUM_Lin.getValue(); r++) {
            for (int c = 0; c < Damas.Num_col.getValue(); c++) {
               
                System.out.println(Quadrados[r][c].getSquareID() + " --" + Quadrados[r][c].isPossibleToMove());
            }
        }
    }

    private void assignPlayerIDs() {

        for (int r = 0; r < 3; r++) {

            for (int c = 0; c < Damas.Num_col.getValue(); c++) {
                if (Quadrados[r][c].getIsFilled()) {
                    Quadrados[r][c].setPlayerID(Damas.JOGADOR_UM.getValue());
                }
            }
        }

        //Rows 5-7 for player TWO
        for (int r = 5; r < 8; r++) {
            //Columns
            for (int c = 0; c < Damas.Num_col.getValue(); c++) {
                if (Quadrados[r][c].getIsFilled()) {
                    Quadrados[r][c].setPlayerID(Damas.JOGADOR_DOIS.getValue());
                }
            }
        }
    }

    public LinkedList<Quadrado> findPlayableSquares(Quadrado selectedSquare) {

        LinkedList<Quadrado> playableSquares = new LinkedList<Quadrado>();

        int selectedRow = selectedSquare.getSquareRow();
        int selectedCol = selectedSquare.getSquareCol();

        int movableRow = (selectedSquare.getPlayerID() == 1) ? selectedRow + 1 : selectedRow - 1;

        twoFrontSquares(playableSquares, movableRow, selectedCol);
        crossJumpFront(playableSquares, (selectedSquare.getPlayerID() == 1) ? movableRow + 1 : movableRow - 1, selectedCol, movableRow);
        if (selectedSquare.isKing()) {
            movableRow = (selectedSquare.getPlayerID() == 1) ? selectedRow - 1 : selectedRow + 1;
            twoFrontSquares(playableSquares, movableRow, selectedCol);
            crossJumpFront(playableSquares, (selectedSquare.getPlayerID() == 1) ? movableRow - 1 : movableRow + 1, selectedCol, movableRow);
        }
        return playableSquares;
    }

    private void twoFrontSquares(LinkedList<Quadrado> pack, int movableRow, int selectedCol) {

        if (movableRow >= 0 && movableRow < 8) {

            if (selectedCol >= 0 && selectedCol < 7) {
                Quadrado rightCorner = Quadrados[movableRow][selectedCol + 1];
                if (rightCorner.getPlayerID() == 0) {
                    rightCorner.setPossibleToMove(true);
                    pack.add(rightCorner);
                }
            }

            if (selectedCol > 0 && selectedCol <= 8) {
                Quadrado leftCorner = Quadrados[movableRow][selectedCol - 1];
                if (leftCorner.getPlayerID() == 0) {
                    leftCorner.setPossibleToMove(true);
                    pack.add(leftCorner);
                }
            }
        }
    }

    private void crossJumpFront(LinkedList<Quadrado> pack, int movableRow, int selectedCol, int middleRow) {

        int middleCol;

        if (movableRow >= 0 && movableRow < 8) {

            if (selectedCol >= 0 && selectedCol < 6) {
                Quadrado rightCorner = Quadrados[movableRow][selectedCol + 2];
                middleCol = (selectedCol + selectedCol + 2) / 2;
                if (rightCorner.getPlayerID() == 0 && isOpponentInbetween(middleRow, middleCol)) {
                    rightCorner.setPossibleToMove(true);
                    pack.add(rightCorner);
                }
            }

        
            if (selectedCol > 1 && selectedCol <= 7) {
                Quadrado CantoEsqu = Quadrados[movableRow][selectedCol - 2];
                middleCol = (selectedCol + selectedCol - 2) / 2;
                if (CantoEsqu.getPlayerID() == 0 && isOpponentInbetween(middleRow, middleCol)) {
                    CantoEsqu.setPossibleToMove(true);
                    pack.add(CantoEsqu);
                }
            }
        }
    }

    private boolean isOpponentInbetween(int row, int col) {
        return Quadrados[row][col].isOpponentSquare();
    }
}
