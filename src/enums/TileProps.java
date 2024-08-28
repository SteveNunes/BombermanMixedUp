package enums;

public enum TileProps {
	
	GROUND, // Chão normal, qualquer coisa passa por cima
	WALL, // Parede (Só pode atravessar pulando)
	HIGH_WALL, // Parede (Não dá para passar nem pulando)
	HOLE, // Buraco (Só pode atravessar voando ou pulando, explosão passa por cima)
	WATER, // Água (Igual GROUND, mas gera efeito visual de água cobrindo as pernas)
	ICE, // Gelo (Personagem escorrega ao andar em cima)
	LAVA, // Lava (Causa dano e explode bombas)
	QUICKSAND, // Areia movediça (Deixa o movimento mais lento)
	SLIMY, // Pegajoso (Deixa o movimento mais lento, não dá para chutar nem agarrar bomba depositada nesse tile)
	PUSH_TO_LEFT, // Empurra o personagem/bomba para a esquerda
	PUSH_TO_UP, // Empurra o personagem/bomba para cima
	PUSH_TO_RIGHT, // Empurra o personagem/bomba para a direita
	PUSH_TO_DOWN, // Empurra o personagem/bomba para baixo
	TREADMILL_TO_LEFT, // Esteira (Vai empurrando objetos em cima dela (incluindo bombas e itens) para a esquerda  
	TREADMILL_TO_UP, // Esteira (Vai empurrando objetos em cima dela (incluindo bombas e itens) para cima  
	TREADMILL_TO_RIGHT, // Esteira (Vai empurrando objetos em cima dela (incluindo bombas e itens) para a direita  
	TREADMILL_TO_DOWN, // Esteira (Vai empurrando objetos em cima dela (incluindo bombas e itens) para baixo  
	REDIRECT_BOMB_TO_LEFT, // Faz a bomba chutada que passar por esse tile, ir para a esquerda
	REDIRECT_BOMB_TO_UP, // Faz a bomba chutada que passar por esse tile, ir para cima
	REDIRECT_BOMB_TO_RIGHT, // Faz a bomba chutada que passar por esse tile, ir para a direita
	REDIRECT_BOMB_TO_DOWN; // Faz a bomba chutada que passar por esse tile, ir para baixo
	
}
