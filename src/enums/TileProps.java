package enums;

public enum TileProps {
	
	GROUND(1), // Chão normal, qualquer coisa passa por cima
	WALL(1), // Parede (Só pode atravessar pulando)
	HIGH_WALL(1), // Parede (Não dá para passar nem pulando)
	HOLE(1), // Buraco (Só pode atravessar voando ou pulando, explosão passa por cima)
	WATER(1), // Água (Igual GROUND, mas gera efeito visual de água cobrindo as pernas)
	ICE(1), // Gelo (Personagem escorrega ao andar em cima)
	LAVA(1), // Lava (Causa dano e explode bombas)
	QUICKSAND(1), // Areia movediça (Deixa o movimento mais lento)
	SLIMY(1), // Pegajoso (Deixa o movimento mais lento, não dá para chutar nem agarrar bomba depositada nesse tile)
	PUSH_TO_LEFT(1), // Empurra o personagem/bomba para a esquerda
	PUSH_TO_UP(1), // Empurra o personagem/bomba para cima
	PUSH_TO_RIGHT(1), // Empurra o personagem/bomba para a direita
	PUSH_TO_DOWN(1), // Empurra o personagem/bomba para baixo
	TREADMILL_TO_LEFT(1), // Esteira (Vai empurrando objetos em cima dela (incluindo bombas e itens) para a esquerda  
	TREADMILL_TO_UP(1), // Esteira (Vai empurrando objetos em cima dela (incluindo bombas e itens) para cima  
	TREADMILL_TO_RIGHT(1), // Esteira (Vai empurrando objetos em cima dela (incluindo bombas e itens) para a direita  
	TREADMILL_TO_DOWN(1), // Esteira (Vai empurrando objetos em cima dela (incluindo bombas e itens) para baixo  
	REDIRECT_BOMB_TO_LEFT(1), // Faz a bomba chutada que passar por esse tile, ir para a esquerda
	REDIRECT_BOMB_TO_UP(1), // Faz a bomba chutada que passar por esse tile, ir para cima
	REDIRECT_BOMB_TO_RIGHT(1), // Faz a bomba chutada que passar por esse tile, ir para a direita
	REDIRECT_BOMB_TO_DOWN(1); // Faz a bomba chutada que passar por esse tile, ir para baixo

	private int value;
	
	TileProps(int value)
		{ this.value = value;	}
	
	public int getValue()
		{ return value; }
	
}
