package Constantes;


public enum Damas {	
	
	NUM_Lin(8),
	Num_col(8),
	Pecas_total(12),
	Quadrado_total(64),
	Quadrado_jogavel(32),
	Quadrado_vazio(0),
	JOGADOR_UM(1),
	JOGADOR_DOIS(2),
	Voce_Ganhou(90),
	Voce_Perdeu(91),
	DOUBLE_JUMP(92);
	
	private int value;
	
	private Damas(int value) {
		this.value = value;
	}
	
	public int getValue(){
		return this.value;
	}	
}
